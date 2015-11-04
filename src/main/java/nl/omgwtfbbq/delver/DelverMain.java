package nl.omgwtfbbq.delver;

import nl.omgwtfbbq.delver.conf.Config;
import nl.omgwtfbbq.delver.mbeans.MethodUsageSampler;

import javax.management.JMException;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.xml.bind.JAXBException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.lang.management.ManagementFactory;

public class DelverMain {

    /**
     * Premain, entry for the Instrumentation API.
     *
     * @param agentArgs Any agent arguments.
     * @param inst      The Instrumentation.
     */
    public static void premain(String agentArgs, Instrumentation inst) {
        if (agentArgs == null || agentArgs.isEmpty()) {
            Logger.error("No configuration file specified. Instrumentation will not be done.");
            Logger.error("Hint: try `java -javaagent:delver.jar=<config file location>'");
            return;
        }

        Logger.debug("Delver initializing. Using configuration file '%s'", agentArgs);

        FileInputStream fis = null;
        try {
            fis = new FileInputStream(agentArgs);
            Config config = Config.read(fis);

            Logger.debug("Configuration file read successfully");
            ClassTransformer classTransformer = new ClassTransformer(config);
            inst.addTransformer(classTransformer);
        } catch (JAXBException e) {
            Logger.error("Configuration file '%s' found, but unable to read: %s", agentArgs, e.getMessage());
        } catch (FileNotFoundException e) {
            Logger.error("Unable to open configuration file '%s', disabling instrumentation.", agentArgs);
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    // swallow.
                }
            }
        }

        try {
            MBeanServer server = ManagementFactory.getPlatformMBeanServer();
            ObjectName on = new ObjectName("nl.omgwtfbbq.delver:type=MethodUsageSampler");
            server.registerMBean(new MethodUsageSampler(), on);
        } catch (JMException ex) {
            Logger.error("Unable to register MXBean with MBeanServer: %s", ex.getMessage());
        } catch (Exception ex) {
            Logger.error("Other exception: ", ex.getMessage());
            ex.printStackTrace();
        }

    }
}
