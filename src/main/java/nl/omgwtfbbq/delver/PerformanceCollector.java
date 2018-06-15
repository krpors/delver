package nl.omgwtfbbq.delver;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

// TODO: do performance tests on the usage of ConcurrentHashmap.

/**
 * Singleton instance to collect usages of methods.
 */
public final class PerformanceCollector {

    /**
     * Only instance.
     */
    private static PerformanceCollector performanceCollector = new PerformanceCollector();

    /**
     * The map with a key of signature, plus the amount of calls. The map is made
     * concurrent, because multiple threads can potentially modify the map. The add()
     * method is inserted in all transformed classes, therefore the add() can be
     * called from any number of threads.
     */
    private Map<Signature, Metric> calls = new ConcurrentHashMap<>();

    private PerformanceCollector() {
    }

    public static PerformanceCollector instance() {
        return performanceCollector;
    }

    /**
     * Adds a signature, or ups the counter by one for that signature.
     *
     * @param signature The signature to add.
     */
    public void add(final Signature signature, long start, long end) {
        if (calls.containsKey(signature)) {
            Metric m = calls.get(signature);
            m.update(start, end);
            calls.put(signature, m);
        } else {
            Metric m = new Metric();
            m.setSignature(signature);
            calls.put(signature, m);
        }
    }

    /**
     * Gets the call map as an unmodifiable map.
     *
     * @return The map.
     */
    public Map<Signature, Metric> getCallMap() {
        return Collections.unmodifiableMap(calls);
    }

    /**
     * Gets the total amount of calls.
     *
     * @return The tiotal amount of calls.
     */
    public long totalCallCount() {
        return calls.values().stream().mapToInt(Metric::getCallCount).sum();
    }

    /**
     * Writes the contents of the map to the specified outputstream.
     *
     * @param os The outputstream to write to.
     * @throws IOException When something fails.
     */
    public void write(final OutputStream os) throws IOException {
        for (Signature signature : calls.keySet()) {
            Metric m = calls.get(signature);
            os.write((m.getCallCount() + ";").getBytes());
            os.write((m.getAverage() + ";").getBytes());
            os.write(SignatureFormatter.format(signature).getBytes());
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

        List<Metric> metricList = new ArrayList<>(calls.values());
        Collections.sort(metricList);
        for (Metric m : metricList) {
            Signature signature = m.getSignature();;
            w.write(String.valueOf(m.getCallCount()));
            w.write(";");
            w.write(String.valueOf(m.getMax()));
            w.write(";");
            w.write(String.valueOf(m.getAverage()));
            w.write(";");
            w.write(String.valueOf(m.getTotal()));
            w.write(";");
            w.write(SignatureFormatter.format(signature));
            w.write("\n");
        }
//        int sum = calls.values().stream().mapToInt(Metric::getCallCount).sum();
//        System.out.println("sum of all calls: " + sum);
        w.flush();
    }
}
