package nl.omgwtfbbq.delver.conf;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "delver")
@XmlAccessorType(XmlAccessType.FIELD)
public class Config {

    @XmlAttribute
    private boolean verbose;

    @XmlElement(name = "http")
    private HttpConfig httpConfig;

    @XmlElementWrapper(name = "include")
    @XmlElement(name = "pattern")
    private List<Pattern> listIncludes = new ArrayList<Pattern>();

    @XmlElementWrapper(name = "exclude")
    @XmlElement(name = "pattern")
    private List<Pattern> listExcludes = new ArrayList<Pattern>();

    /**
     * Creates a JAXBContext used for marshalling and unmarshalling.
     *
     * @return The context.
     * @throws JAXBException When the context cannot be created.
     */
    private static JAXBContext createContext() throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(Config.class);
        return context;
    }

    /**
     * Reads stuff.
     *
     * @param is The input stream.
     * @return
     */
    public static Config read(InputStream is) throws JAXBException {
        JAXBContext context = createContext();
        Unmarshaller um = context.createUnmarshaller();
        Config cfg = (Config) um.unmarshal(is);
        return cfg;
    }

    public static void write(Config cfg, OutputStream os) throws JAXBException {
        JAXBContext context = createContext();
        Marshaller m = context.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        m.marshal(cfg, os);
    }

    public List<Pattern> getIncludes() {
        return listIncludes;
    }

    public void setIncludes(List<Pattern> patterns) {
        this.listIncludes = patterns;
    }

    public void addInclude(Pattern pkg) {
        this.listIncludes.add(pkg);
    }

    public List<Pattern> getExcludes() {
        return listExcludes;
    }

    public boolean isVerbose() {
        return verbose;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    public HttpConfig getHttpConfig() {
        return httpConfig;
    }

    public void setHttpConfig(HttpConfig config) {
        this.httpConfig = config;
    }

    public void setExcludes(List<Pattern> listExcludes) {
        this.listExcludes = listExcludes;
    }

    public void addExclude(Pattern pkg) {
        this.listExcludes.add(pkg);
    }

    public boolean isExcluded(String classname) {
        for (Pattern p : getExcludes()) {
            if (p.matches(classname)) {
                return true;
            }
        }

        return false;
    }

    public boolean isIncluded(String classname) {
        for (Pattern p : getIncludes()) {
            if (p.matches(classname)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Http server configuration.
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class HttpConfig {

        @XmlElement(name = "enabled")
        private boolean httpEnabled;

        @XmlElement(name = "port")
        private int httpPort;

        public boolean isHttpEnabled() {
            return httpEnabled;
        }

        public void setHttpEnabled(boolean httpEnabled) {
            this.httpEnabled = httpEnabled;
        }

        public int getHttpPort() {
            return httpPort;
        }

        public void setHttpPort(int httpPort) {
            this.httpPort = httpPort;
        }
    }
}
