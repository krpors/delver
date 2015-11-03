package nl.omgwtfbbq.delver.mbeans;

import javax.management.MBeanException;
import java.io.IOException;
import java.util.Map;

/**
 * Created by av13ui on 11/2/2015.
 */
public interface MethodUsageSamplerMXBean {

    Map<String, Integer> getCallMap();

    void writeToFile(String file) throws MBeanException;

    int getMethodCount();
}
