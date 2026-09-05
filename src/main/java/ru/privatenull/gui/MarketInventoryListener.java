package ru.privatenull.gui;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import ru.privatenull.PnMarketPlugin;

public final class MarketInventoryListener implements Listener {
    private final PnMarketPlugin plugin;

    public MarketInventoryListener(PnMarketPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInventoryOpen(InventoryOpenEvent event) {
        applyConfiguredDecorations(event.getInventory());
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        Inventory top = event.getView().getTopInventory();
        Inventory clicked = event.getClickedInventory();
        if (clicked == null) return;
        if (isMarketView(top)) {
            plugin.getServer().getScheduler().runTask(plugin, () -> applyConfiguredDecorations(top));
        }
        if (isAnimating(top, player)) {
            event.setCancelled(true);
            return;
        }

        if (top.getHolder() instanceof BundlePreviewView view) {
            event.setCancelled(true);
            if (clicked.equals(top)) withTransition(player, event.getRawSlot(),
                    () -> view.controller.handleBundlePreviewClick(player, view, event.getRawSlot()));
            return;
        }
        if (top.getHolder() instanceof BundleCreateView view) {
            event.setCancelled(true);
            if (clicked.equals(top)) withTransition(player, event.getRawSlot(),
                    () -> view.controller.handleBundleCreateClick(player, view, event.getRawSlot()));
            return;
        }
        if (top.getHolder() instanceof FavoritesView view) {
            event.setCancelled(true);
            if (clicked.equals(top)) withTransition(player, event.getRawSlot(), () -> view.controller.handleFavoritesClick(
                    player, view, event.getRawSlot(), event.isShiftClick()));
            return;
        }
        if (top.getHolder() instanceof NotificationCatalogView view) {
            event.setCancelled(true);
            if (clicked.equals(top)) withTransition(player, event.getRawSlot(), () ->
                    view.controller.handleNotificationCatalogClick(player, view, event.getRawSlot(),
                            event.isLeftClick(), event.isRightClick(), event.isShiftClick()));
            return;
        }
        if (top.getHolder() instanceof MyItemsView view) {
            event.setCancelled(true);
            if (clicked.equals(top)) {
                withTransition(player, event.getRawSlot(), () ->
                        view.controller.handleMyItemsClick(player, view, event.getRawSlot(), event.isRightClick()));
            }
            return;
        }
        if (top.getHolder() instanceof SellerView view) {
            event.setCancelled(true);
            if (clicked.equals(top)) {
                withTransition(player, event.getRawSlot(), () -> view.controller.handleSellerClick(
                        player, view, event.getRawSlot(), event.isLeftClick(), event.isRightClick()));
            }
            return;
        }
        if (top.getHolder() instanceof PurchaseView view) {
            event.setCancelled(true);
            if (clicked.equals(top)) withTransition(player, event.getRawSlot(),
                    () -> view.controller.handlePurchaseClick(player, view, event.getRawSlot()));
            return;
        }
        if (top.getHolder() instanceof AuctionView view) {
            event.setCancelled(true);
            if (!player.getUniqueId().equals(view.viewer) || !clicked.equals(top)) return;
            withTransition(player, event.getRawSlot(), () -> view.controller.handleAuctionClick(
                    player, view, event.getRawSlot(), event.isLeftClick(), event.isRightClick()));
        }
    }

    private boolean isAnimating(Inventory top, Player player) {
        Object holder = top.getHolder();
        if (holder instanceof AuctionView view) return view.controller.isAnimating(player);
        if (holder instanceof PurchaseView view) return view.controller.isAnimating(player);
        if (holder instanceof SellerView view) return view.controller.isAnimating(player);
        if (holder instanceof MyItemsView view) return view.controller.isAnimating(player);
        if (holder instanceof BundlePreviewView view) return view.controller.isAnimating(player);
        if (holder instanceof BundleCreateView view) return view.controller.isAnimating(player);
        if (holder instanceof FavoritesView view) return view.controller.isAnimating(player);
        if (holder instanceof NotificationCatalogView view) return view.controller.isAnimating(player);
        return false;
    }

    private void withTransition(Player player, int sourceSlot, Runnable action) {
        plugin.withGuiTransition(player, sourceSlot, action);
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        Inventory top = event.getView().getTopInventory();
        if (!isMarketView(top)) return;
        int topSize = top.getSize();
        if (event.getRawSlots().stream().anyMatch(slot -> slot < topSize)) {
            event.setCancelled(true);
        }
    }

    private boolean isMarketView(Inventory inventory) {
        return viewKey(inventory) != null;
    }

    private void applyConfiguredDecorations(Inventory inventory) {
        String view = viewKey(inventory);
        if (view != null) plugin.guiConfig().applyDecorations(inventory, view);
    }

    private String viewKey(Inventory inventory) {
        Object holder = inventory.getHolder();
        if (holder instanceof AuctionView) return "auction";
        if (holder instanceof PurchaseView) return "purchase";
        if (holder instanceof SellerView) return "seller";
        if (holder instanceof MyItemsView) return "my-items";
        if (holder instanceof BundlePreviewView) return "bundle-preview";
        if (holder instanceof BundleCreateView) return "bundle-create";
        if (holder instanceof FavoritesView) return "favorites";
        if (holder instanceof NotificationCatalogView) return "notification-catalog";
        return null;
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getInventory().getHolder() instanceof AuctionView view) view.controller.closeView(event.getPlayer().getUniqueId(), event.getInventory());
        else if (event.getInventory().getHolder() instanceof PurchaseView view) view.controller.closeView(event.getPlayer().getUniqueId(), event.getInventory());
        else if (event.getInventory().getHolder() instanceof SellerView view) view.controller.closeView(event.getPlayer().getUniqueId(), event.getInventory());
        else if (event.getInventory().getHolder() instanceof MyItemsView view) view.controller.closeView(event.getPlayer().getUniqueId(), event.getInventory());
        else if (event.getInventory().getHolder() instanceof BundlePreviewView view) view.controller.closeView(event.getPlayer().getUniqueId(), event.getInventory());
        else if (event.getInventory().getHolder() instanceof BundleCreateView view) view.controller.closeView(event.getPlayer().getUniqueId(), event.getInventory());
        else if (event.getInventory().getHolder() instanceof FavoritesView view) view.controller.closeView(event.getPlayer().getUniqueId(), event.getInventory());
        else if (event.getInventory().getHolder() instanceof NotificationCatalogView view) view.controller.closeView(event.getPlayer().getUniqueId(), event.getInventory());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        plugin.removeViewer(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onLogin(PlayerLoginEvent event) {
        plugin.prepareJoinNotifications(event.getPlayer());
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        plugin.notifyOnJoin(event.getPlayer());
    }
}
