package dev.litchiu.maintainance.core;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.nio.file.FileStore;
import java.nio.file.Files;
import java.nio.file.Path;

/** Loader-independent JVM and host metrics. Values are best-effort and may be unavailable. */
public final class ServerMetrics {
    private static final long SECOND = 1_000_000_000L;
    private static final long[] TICK_TIMES = new long[1200];
    private static final long[] TICK_DURATIONS = new long[1200];
    private static int nextTick;
    private static int tickCount;
    private static volatile double latestMspt;

    private ServerMetrics() {}

    public static synchronized void recordTick(long durationNanos) {
        long now = System.nanoTime();
        long duration = Math.max(0L, durationNanos);
        TICK_TIMES[nextTick] = now;
        TICK_DURATIONS[nextTick] = duration;
        nextTick = (nextTick + 1) % TICK_TIMES.length;
        tickCount = Math.min(tickCount + 1, TICK_TIMES.length);
        latestMspt = duration / 1_000_000.0;
    }

    public static double getTps() { return getTps(5); }
    public static double getTps1m() { return getTps(60); }
    public static double getMspt() { return latestMspt; }
    public static double getMspt1m() { return averageMspt(60); }

    private static synchronized double getTps(int windowSeconds) {
        Window window = getWindow(windowSeconds);
        if (window.ticks == 0 || window.durationNanos <= 0) return 20.0;
        return Math.min(20.0, window.ticks * (double) SECOND / window.durationNanos);
    }

    private static synchronized double averageMspt(int windowSeconds) {
        Window window = getWindow(windowSeconds);
        return window.ticks == 0 ? 0.0 : window.durationNanos / (double) window.ticks / 1_000_000.0;
    }

    private static Window getWindow(int windowSeconds) {
        long cutoff = System.nanoTime() - windowSeconds * SECOND;
        long duration = 0L;
        int ticks = 0;
        for (int i = 0; i < tickCount; i++) {
            int index = (nextTick - 1 - i + TICK_TIMES.length) % TICK_TIMES.length;
            if (TICK_TIMES[index] < cutoff) break;
            duration += TICK_DURATIONS[index];
            ticks++;
        }
        return new Window(ticks, duration);
    }

    private record Window(int ticks, long durationNanos) {}

    public static long getUsedMemoryMb() {
        Runtime runtime = Runtime.getRuntime();
        return (runtime.totalMemory() - runtime.freeMemory()) / (1024L * 1024L);
    }

    public static long getMaxMemoryMb() {
        return Runtime.getRuntime().maxMemory() / (1024L * 1024L);
    }

    public static double getCpuLoadPercent() {
        OperatingSystemMXBean bean = ManagementFactory.getOperatingSystemMXBean();
        try {
            if (bean instanceof com.sun.management.OperatingSystemMXBean os) {
                double load = os.getCpuLoad();
                return load < 0 ? -1 : load * 100.0;
            }
        } catch (Throwable ignored) { }
        return -1;
    }

    public static long getUsableDiskMb(Path path) {
        try {
            FileStore store = Files.getFileStore(path);
            return store.getUsableSpace() / (1024L * 1024L);
        } catch (Exception ignored) { return -1; }
    }
}
