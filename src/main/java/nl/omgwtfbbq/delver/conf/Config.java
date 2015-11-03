package nl.omgwtfbbq.delver.conf;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.*;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "delver")
@XmlAccessorType(XmlAccessType.FIELD)
public class Config {

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
}
