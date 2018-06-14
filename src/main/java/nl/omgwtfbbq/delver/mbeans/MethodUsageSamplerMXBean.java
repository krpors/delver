package nl.omgwtfbbq.delver.mbeans;

import nl.omgwtfbbq.delver.Metric;

import javax.management.MBeanException;
import java.util.Map;

/**
 * Created by av13ui on 11/2/2015.
 */
public interface MethodUsageSamplerMXBean {

    Map<String, Metric> getCallMap();

    void writeToFile(String file) throws MBeanException;

    int getMethodCount();

    int getTotalMethodUsageCount();
}
