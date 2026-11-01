package ru.privatenull.integration;

/** pnMarket-facing boundary that keeps pnLibrary API types out of the plugin entry point. */
public interface PnRuntime extends AutoCloseable {

    /** Registers pnMarket with pnLibrary: policy, metrics, diagnostics and updates. */
    void register(String storageType);

    /** Reports that pnMarket has started: shows the console card. */
    void enabled(String storageType);

    /** Update status text for /pnmarket. */
    String updateStatus(String currentVersion);

    String supportUrl();

    /** Registers the /pnmarket command on the pnLibrary command tree. */
    void registerCommands(ru.privatenull.command.PnMarketCommand actions);

    /** Task scheduler of the pnMarket module. */
    PnTasks tasks();

    @Override
    void close();
}
