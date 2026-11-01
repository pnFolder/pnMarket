package ru.privatenull.integration;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.pnfolder.pnlibrary.api.commands.ArgumentType;
import org.pnfolder.pnlibrary.api.commands.CommandContext;
import org.pnfolder.pnlibrary.api.commands.CommandDefinition;
import org.pnfolder.pnlibrary.api.commands.NoPermission;
import org.pnfolder.pnlibrary.api.plugin.ModuleContext;
import ru.privatenull.command.PnMarketCommand;

import java.util.UUID;

/** Describes the pnMarket commands that run on the pnLibrary command tree. */
final class LibraryCommands {

    private LibraryCommands(){}

    static void register(ModuleContext module, PnMarketCommand actions) {
        module.registerCommand(CommandDefinition.builder("pnmarket")
                .aliases("market", "pnm")
                .permission("pnmarket.admin")
                .onError(NoPermission.class, (context, error) -> actions.noPermission(bukkit(context)))
                .executes(context -> actions.menu(bukkit(context)))
                .literal("machine", node -> node.executes(context -> actions.machine(bukkit(context))))
                .literal("reload", node -> node.executes(context -> actions.reload(bukkit(context))))
                // Any other word shows the menu, as it always did.
                .argument("action", ArgumentType.string(), node ->
                        node.executes(context -> actions.menu(bukkit(context))))
                .build());
    }

    private static CommandSender bukkit(CommandContext context) {
        if (context.getSender().isConsole()) return Bukkit.getConsoleSender();
        CommandSender player = Bukkit.getPlayer(UUID.fromString(context.getSender().getId()));
        return player != null ? player : Bukkit.getConsoleSender();
    }
}
