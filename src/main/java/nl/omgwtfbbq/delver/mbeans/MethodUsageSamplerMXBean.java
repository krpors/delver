package nl.omgwtfbbq.delver.mbeans;

import nl.omgwtfbbq.delver.Metric;
import nl.omgwtfbbq.delver.Signature;

import javax.management.MBeanException;
import java.util.Map;

/**
 * Created by av13ui on 11/2/2015.
 */
public interface MethodUsageSamplerMXBean {

    Map<Signature, Metric> getCallMap();

    void writeToFile(String file) throws MBeanException;

    int getMethodCount();

    int getTotalMethodUsageCount();
}
