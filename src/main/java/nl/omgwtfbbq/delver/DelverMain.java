package nl.omgwtfbbq.delver;

import nl.omgwtfbbq.delver.conf.Config;

import javax.xml.bind.JAXBException;
import java.io.InputStream;
import java.lang.instrument.Instrumentation;
import java.util.logging.Level;

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
        Logger.debug("Delver Java Agent initializing");

        try {
            Config config = null;
            InputStream is = DelverMain.class.getResourceAsStream(CFG_FILE);
            if (is != null) {
                config = Config.read(is);
            } else {
                Logger.error("Unable to find '%s' on the classpath, disabling instrumentation", CFG_FILE);
                return;
            }

            Logger.debug("Configuration file read successfully");
            ClassTransformer classTransformer = new ClassTransformer(config);
            inst.addTransformer(classTransformer);
        } catch (JAXBException e) {
            Logger.error("Configuration file '%s'found, but unable to read: %s", CFG_FILE, e.getMessage());
        }
    }
}
