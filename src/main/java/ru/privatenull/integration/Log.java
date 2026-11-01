package ru.privatenull.integration;

import org.bukkit.plugin.java.JavaPlugin;
import org.pnfolder.pnlibrary.api.logging.PnLogger;
import org.pnfolder.pnlibrary.api.plugin.ModuleContext;

import java.util.logging.Level;
import java.util.logging.Logger;

/** Единый логгер плагина: пишет через логгер модуля pnLibrary, а без него - через обычный логгер плагина. */
public final class Log {

    private static volatile JavaPlugin plugin;
    private static volatile ModuleContext module;

    private Log() {
    }

    /** Запоминает плагин для запасного пути, пока модуль ещё не зарегистрирован. */
    public static void plugin(JavaPlugin owner) {
        plugin = owner;
    }

    /** Подключает логгер модуля после регистрации. */
    public static void attach(JavaPlugin owner, ModuleContext context) {
        plugin = owner;
        module = context;
    }

    /** Отключает логгер модуля при закрытии. */
    public static void detach() {
        module = null;
    }

    public static void info(String message) {
        PnLogger logger = logger();
        if (logger != null) {
            try {
                logger.info(message);
                return;
            } catch (RuntimeException | LinkageError ignored) {
                // Модуль закрывается: пишем через запасной логгер.
            }
        }
        fallback().info(message);
    }

    public static void success(String message) {
        PnLogger logger = logger();
        if (logger != null) {
            try {
                logger.success(message);
                return;
            } catch (RuntimeException | LinkageError ignored) {
                // Модуль закрывается: пишем через запасной логгер.
            }
        }
        fallback().info(message);
    }

    public static void warning(String message) {
        PnLogger logger = logger();
        if (logger != null) {
            try {
                logger.warning(message);
                return;
            } catch (RuntimeException | LinkageError ignored) {
                // Модуль закрывается: пишем через запасной логгер.
            }
        }
        fallback().warning(message);
    }

    public static void error(String message) {
        error(message, null);
    }

    public static void error(String message, Throwable failure) {
        PnLogger logger = logger();
        if (logger != null) {
            try {
                logger.error(message, failure);
                return;
            } catch (RuntimeException | LinkageError ignored) {
                // Модуль закрывается: пишем через запасной логгер.
            }
        }
        if (failure == null) fallback().severe(message);
        else fallback().log(Level.SEVERE, message, failure);
    }

    private static PnLogger logger() {
        ModuleContext context = module;
        if (context == null || context.isClosed()) return null;
        return context.getLogger();
    }

    private static Logger fallback() {
        JavaPlugin owner = plugin;
        return owner == null ? Logger.getLogger("pn") : owner.getLogger();
    }
}
