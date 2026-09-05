package ru.privatenull.gui;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import ru.privatenull.PnMarketPlugin;

import java.util.Map;

/**
 * Keeps all pnMarket GUI background slots in sync with auction.decor.black/orange.
 *
 * Some legacy views still create stained-glass background panes directly in Java.
 * This listener normalizes those panes to the configured GUI icons so changing the
 * background in gui.yml affects every pnMarket inventory, not only the main auction.
 */
public final class ConfiguredBackgroundListener implements Listener {
    private final PnMarketPlugin plugin;

    public ConfiguredBackgroundListener(PnMarketPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onOpen(InventoryOpenEvent event) {
        apply(event.getInventory());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onClick(InventoryClickEvent event) {
        Inventory top = event.getView().getTopInventory();
        if (!isMarketInventory(top)) return;
        plugin.getServer().getScheduler().runTask(plugin, () -> apply(top));
    }

    private void apply(Inventory inventory) {
        if (!isMarketInventory(inventory)) return;

        ItemStack black = plugin.guiConfig().item(
                "auction.decor.black", Material.BLACK_STAINED_GLASS_PANE, Map.of());
        ItemStack orange = plugin.guiConfig().item(
                "auction.decor.orange", Material.ORANGE_STAINED_GLASS_PANE, Map.of());

        for (int slot = 0; slot < inventory.getSize(); slot++) {
            ItemStack current = inventory.getItem(slot);
            if (!isLegacyBackground(current)) continue;

            ItemStack configured = current.getType() == Material.BLACK_STAINED_GLASS_PANE ? black : orange;
            inventory.setItem(slot, configured.clone());
        }
    }

    private boolean isMarketInventory(Inventory inventory) {
        InventoryHolder holder = inventory.getHolder();
        if (holder == null) return false;
        Package holderPackage = holder.getClass().getPackage();
        return holderPackage != null && holderPackage.getName().startsWith("ru.privatenull.gui");
    }

    @SuppressWarnings("deprecation")
    private boolean isLegacyBackground(ItemStack item) {
        if (item == null) return false;
        Material type = item.getType();
        if (type != Material.BLACK_STAINED_GLASS_PANE && type != Material.ORANGE_STAINED_GLASS_PANE) {
            return false;
        }
        ItemMeta meta = item.getItemMeta();
        return meta == null || !meta.hasDisplayName() || meta.getDisplayName().trim().isEmpty();
    }
}
