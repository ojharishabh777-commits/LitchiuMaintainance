package dev.litchiu.maintainance.core;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.nio.file.FileStore;
import java.nio.file.Files;
import java.nio.file.Path;

/** Loader-independent JVM and host metrics. Values are best-effort and may be unavailable. */
public final class ServerMetrics {
    private static volatile double tps = 20.0;
    private static volatile double mspt = 0.0;
    private static volatile long lastTickNanos;

    private ServerMetrics() {}

    public static void recordTick(long durationNanos) {
        mspt = durationNanos / 1_000_000.0;
        lastTickNanos = System.nanoTime();
        // Short-window approximation; the first release reports tick duration and a bounded estimate.
        tps = mspt <= 50.0 ? 20.0 : Math.max(0.0, 1000.0 / mspt);
    }

    public static double getTps() { return Math.min(20.0, tps); }
    public static double getMspt() { return mspt; }
    public static long getUsedMemoryMb() {
        Runtime r = Runtime.getRuntime();
        return (r.totalMemory() - r.freeMemory()) / (1024L * 1024L);
    }
    public static long getMaxMemoryMb() {
        return Runtime.getRuntime().maxMemory() / (1024L * 1024L);
    }
    public static double getCpuLoadPercent() {
        OperatingSystemMXBean bean = ManagementFactory.getOperatingSystemMXBean();
        try {
            // com.sun.management is available on common HotSpot/OpenJDK server runtimes.
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
