package ru.privatenull.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.privatenull.PnMarketPlugin;

/** Actions of /pnmarket; routing, permission and tab completion are done by the pnLibrary command tree. */
public final class PnMarketCommand {
    private final PnMarketPlugin plugin;
    private final PnMarketCommandMenu menu;

    public PnMarketCommand(PnMarketPlugin plugin) {
        this.plugin = plugin;
        this.menu = new PnMarketCommandMenu(plugin);
    }

    public void menu(CommandSender sender) {
        menu.send(sender);
    }

    public void noPermission(CommandSender sender) {
        sender.sendMessage(plugin.messages().message("command.no-permission"));
    }

    public void reload(CommandSender sender) {
        plugin.reloadRuntime();
        sender.sendMessage(plugin.messages().message("command.reloaded"));
    }

    public void machine(CommandSender sender) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(plugin.messages().message("command.only-player"));
            return;
        }
        plugin.openMachine(player);
    }
}
