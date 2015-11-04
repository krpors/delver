package nl.omgwtfbbq.delver;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class UsageCollector {

    private static UsageCollector usageCollector = new UsageCollector();

    private Map<String, Integer> calls = new HashMap<String, Integer>();

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
