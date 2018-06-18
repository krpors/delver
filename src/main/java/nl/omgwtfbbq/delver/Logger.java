package nl.omgwtfbbq.delver;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Simple "logger" for the Delver instrumentation without using fancy frameworks. We don't want to rely
 * on too many dependencies, and the basic java.util.Logging requires more configuration than necessary.
 * The log entries are only used to write to stdout en stderr.
 */
public final class Logger {

    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static boolean verbose = true;

    public static void debug(String s, Object... args) {
        if (!verbose) {
            return;
        }

        System.out.printf("[DELVER] DEBUG [%s]: %s\n",
                SDF.format(new Date()),
                String.format(s, args));
    }

    public static void warn(String s, Object... args) {
        System.out.printf("[DELVER] WARN  [%s]: %s\n",
                SDF.format(new Date()),
                String.format(s, args));
    }

    public static void error(String s, Object... args) {
        System.err.printf("[DELVER] ERROR [%s]: %s\n",
                SDF.format(new Date()),
                String.format(s, args));
    }
}
