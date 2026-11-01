package ru.privatenull.integration;

/** Scheduler boundary: runs pnMarket work through the pnLibrary task scope, so tasks end with the module. */
public interface PnTasks {

    /** Used when pnLibrary is already closed: nothing is scheduled. */
    PnTasks NONE = new PnTasks() {
        @Override public void sync(Runnable action) { }
        @Override public void async(Runnable action) { }
        @Override public Handle syncLater(Runnable action, long delayTicks) { return () -> { }; }
        @Override public Handle syncTimer(Runnable action, long delayTicks, long periodTicks) { return () -> { }; }
        @Override public Handle asyncTimer(Runnable action, long delayTicks, long periodTicks) { return () -> { }; }
    };

    /** Runs on the main server thread as soon as possible. */
    void sync(Runnable action);

    /** Runs once off the main thread. */
    void async(Runnable action);

    /** Runs once on the main thread after [delayTicks]. */
    Handle syncLater(Runnable action, long delayTicks);

    /** Repeats on the main thread. */
    Handle syncTimer(Runnable action, long delayTicks, long periodTicks);

    /** Repeats off the main thread. */
    Handle asyncTimer(Runnable action, long delayTicks, long periodTicks);

    /** Stops future runs of a repeating or delayed task. */
    @FunctionalInterface
    interface Handle {
        void cancel();
    }
}
