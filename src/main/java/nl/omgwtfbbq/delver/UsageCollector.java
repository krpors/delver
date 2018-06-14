package nl.omgwtfbbq.delver;

import javassist.bytecode.Descriptor;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// TODO: do performance tests on the usage of ConcurrentHashmap.

/**
 * Singleton instance to collect usages of methods.
 */
public final class UsageCollector {

    /**
     * Only instance.
     */
    private static UsageCollector usageCollector = new UsageCollector();

    /**
     * The map with a key of signature, plus the amount of calls. The map is made
     * concurrent, because multiple threads can potentially modify the map. The add()
     * method is inserted in all transformed classes, therefore the add() can be
     * called from any number of threads.
     */
    private Map<String, Metric> calls = new ConcurrentHashMap<>();

    private UsageCollector() {
    }

    public static UsageCollector instance() {
        return usageCollector;
    }

    /**
     * Adds a signature, or ups the counter by one for that signature.
     *
     * @param signature The signature to add.
     */
    public void add(String signature) {
        if (calls.containsKey(signature)) {
            Metric m = calls.get(signature);
            m.update();
            calls.put(signature, m);
        } else {
            calls.put(signature, new Metric());
        }
    }

    /**
     * Adds a signature, or ups the counter by one for that signature.
     *
     * @param signature The signature to add.
     */
    public void add(String signature, long start, long end) {
        if (calls.containsKey(signature)) {
            Metric m = calls.get(signature);
            m.update(start, end);
            calls.put(signature, m);
        } else {
            calls.put(signature, new Metric());
        }
    }

    /**
     * Gets the call map as an unmodifiable map.
     *
     * @return The map.
     */
    public Map<String, Metric> getCallMap() {
        return Collections.unmodifiableMap(calls);
    }

    /**
     * Writes the contents of the map to the specified outputstream.
     *
     * @param os The outputstream to write to.
     * @throws IOException When something fails.
     */
    public void write(final OutputStream os) throws IOException {
        for (String signature : calls.keySet()) {
            Metric m = calls.get(signature);
            os.write((m.getCallCount() + ";").getBytes());
            os.write((m.getAverage() + ";").getBytes());
            os.write((signature + "\n").getBytes());
        }
        os.flush();
    }

    /**
     * Writes the contents of the map to the specified writer.
     *
     * @param w The writer to write to.
     * @throws IOException When something fails.
     */
    public void write(final Writer w) throws IOException {
        w.write("Call count;Max (ms);Average (ms);Total (ms);Modifiers;Returntype;Classname;Methodname\n");
        for (String signature : calls.keySet()) {
            Metric m = calls.get(signature);
            w.write(String.valueOf(m.getCallCount()));
            w.write(";");
            w.write(String.valueOf(m.getMax()));
            w.write(";");
            w.write(String.valueOf(m.getAverage()));
            w.write(";");
            w.write(String.valueOf(m.getTotal()));
            w.write(";");
            w.write(signature);
            w.write("\n");
        }
        w.flush();
    }
}
