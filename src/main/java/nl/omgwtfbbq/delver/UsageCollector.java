package nl.omgwtfbbq.delver;

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
    private Map<String, Integer> calls = new ConcurrentHashMap<String, Integer>();

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
            calls.put(signature, calls.get(signature) + 1);
        } else {
            calls.put(signature, 0);
        }
    }

    /**
     * Gets the call map as an unmodifiable map.
     *
     * @return The map.
     */
    public Map<String, Integer> getCallMap() {
        return Collections.unmodifiableMap(calls);
    }
}
