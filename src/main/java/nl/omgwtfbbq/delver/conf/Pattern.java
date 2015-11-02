package nl.omgwtfbbq.delver.conf;

import nl.omgwtfbbq.delver.Logger;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;
import java.util.regex.Matcher;
import java.util.regex.PatternSyntaxException;

@XmlRootElement(name = "pattern")
@XmlAccessorType(XmlAccessType.NONE)
public class Pattern {

    private java.util.regex.Pattern regex;

    private boolean valid = true;

    private String pattern;

    public Pattern() {
    }

    public Pattern(String p) {
        setPattern(p);
    }

    public String getPattern() {
        return pattern;
    }

    @XmlValue
    public void setPattern(String pattern) {
        this.pattern = pattern;

        try {
            regex = java.util.regex.Pattern.compile(pattern);
        } catch (PatternSyntaxException ex) {
            Logger.error("Pattern '%s' can't be compiled and will be ignored (%s at index %d)",
                    pattern,
                    ex.getDescription(),
                    ex.getIndex());
            valid = false;
        }
    }

    public boolean isValid() {
        return valid;
    }

    /**
     * Returns true if the given classname matches with the given pattern.
     *
     * @param className The classname.
     * @return
     */
    public boolean matches(String className) {
        // If the pattern could not be compiled, ignore it and return false.
        if (!valid || className == null) {
            return false;
        }

        Matcher m = regex.matcher(className);
        return m.matches();
    }

    @Override
    public String toString() {
        return String.format("%s (pattern: %s)", getClass().getName(), getPattern());
    }
}
