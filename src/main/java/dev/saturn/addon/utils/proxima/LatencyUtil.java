package dev.saturn.addon.utils.proxima;

public class LatencyUtil {
    private static long real_latency = 0L;
    public static long GetRealLatency() {

        return real_latency;
    }

}