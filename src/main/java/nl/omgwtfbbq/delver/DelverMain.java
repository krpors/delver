package nl.omgwtfbbq.delver;

import nl.omgwtfbbq.delver.conf.Config;
import nl.omgwtfbbq.delver.mbeans.MethodUsageSampler;

import javax.management.JMException;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.xml.bind.JAXBException;
import java.io.InputStream;
import java.lang.instrument.Instrumentation;
import java.lang.management.ManagementFactory;

public class DelverMain {

    /**
     * Attempt to look this up in the classpath.
     */
    private final static String CFG_FILE = "/delver-conf.xml";

    /**
     * Premain, entry for the Instrumentation API.
     *
     * @param agentArgs Any agent arguments.
     * @param inst      The Instrumentation.
     */
    public static void premain(String agentArgs, Instrumentation inst) {
        Logger.debug("Delver initializing");

        try {
            Config config = null;
            InputStream is = DelverMain.class.getResourceAsStream(CFG_FILE);
            if (is != null) {
                config = Config.read(is);
            } else {
                Logger.warn("Unable to find '%s' on the classpath, disabling instrumentation", CFG_FILE);
                return;
            }

            Logger.debug("Configuration file read successfully");
            ClassTransformer classTransformer = new ClassTransformer(config);
            inst.addTransformer(classTransformer);
        } catch (JAXBException e) {
            Logger.error("Configuration file '%s'found, but unable to read: %s", CFG_FILE, e.getMessage());
        }

        try {
            MBeanServer server = ManagementFactory.getPlatformMBeanServer();
            ObjectName on = new ObjectName("nl.omgwtfbbq.delver:type=MethodUsageSampler");
            server.registerMBean(new MethodUsageSampler(), on);
        } catch (JMException ex) {
            Logger.error("Unable to do stuff with MBeanServer: %s", ex.getMessage());
        }

    }
}
