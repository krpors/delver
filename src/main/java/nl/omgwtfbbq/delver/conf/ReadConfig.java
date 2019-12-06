package nl.omgwtfbbq.delver.conf;

import nl.omgwtfbbq.delver.Logger;

import javax.xml.bind.JAXBException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * @author praveesingh created on 12/5/19
 */
public class ReadConfig {

    private final Config config;
    private static ReadConfig readConfig = null;

    private ReadConfig(Config config) {
        this.config = config;
    }

    public static ReadConfig getConfigInstance(String agentArgs) throws IOException, JAXBException {
        if (readConfig == null) {
            try (FileInputStream fis = new FileInputStream(agentArgs)) {
                Config config = Config.read(fis);
                Logger.verbose = config.isVerbose();
                readConfig = new ReadConfig(config);
            } catch (FileNotFoundException | JAXBException e) {
                throw e;
            }
        }
        return readConfig;
    }

    public Config getConfig() {
        return config;
    }
}
