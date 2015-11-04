package nl.omgwtfbbq.delver.mbeans;

import nl.omgwtfbbq.delver.Logger;
import nl.omgwtfbbq.delver.UsageCollector;

import javax.management.MBeanException;
import javax.management.MXBean;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

/**
 * Created by av13ui on 11/2/2015.
 */
@MXBean
public class MethodUsageSampler implements MethodUsageSamplerMXBean {

    public Map<String, Integer> getCallMap() {
        return UsageCollector.instance().getCallMap();
    }

    public void writeToFile(String file) throws MBeanException {
        Map<String, Integer> calls = UsageCollector.instance().getCallMap();
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);

            for (String c : calls.keySet()) {
                fos.write(String.format("%d;%s\n", calls.get(c), c).getBytes());
            }
            Logger.debug("Written %d entries to file '%s'", calls.size(), file);
        } catch (IOException ex) {
            String msg = String.format("Unable to write to file '%s': %s", file, ex.getMessage());
            Logger.error(msg);
            ex.printStackTrace();
            throw new MBeanException(ex, msg);
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    // swallow.
                }
            }
        }

    }

    public int getMethodCount() {
        return UsageCollector.instance().getCallMap().size();
    }

    public int getTotalMethodUsageCount() {
        Map<String, Integer> m = UsageCollector.instance().getCallMap();
        int total = 0;
        for (String s : m.keySet()) {
            total += m.get(s);
        }

        return total;
    }
}
