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

    /**
     * Sets the pattern, attempts to compile it. If the pattern could not be compiled, a warning
     * mesage is logged, and the <code>valid</code> flag is set to false.
     *
     * @param pattern The regular expression pattern to compile.
     */
    @XmlValue
    public void setPattern(String pattern) {
        this.pattern = pattern;

        try {
            regex = java.util.regex.Pattern.compile(pattern);
        } catch (PatternSyntaxException ex) {
            Logger.warn(
                    "Pattern '%s' can't be compiled and will be ignored ('%s' at index %d)",
                    pattern,
                    ex.getDescription(),
                    ex.getIndex());
            valid = false;
        }
    }

    /**
     * Returns the boolean whether the pattern was compiled successfully.
     *
     * @return true if the pattern was compiled successfully, false if otherwise.
     */
    public boolean isValid() {
        return valid;
    }

    /**
     * Returns true if the given class name matches with the given pattern.
     *
     * @param className The class name, e.g. <code>"java/util/List"</code>.
     * @return true if the pattern matches with the class name.
     */
    public boolean matches(String className) {
        // If the pattern could not be compiled, ignore it and return false.
        if (!valid || className == null) {
            return false;
        }

        Matcher m = regex.matcher(className);
        return m.matches();
    }

    /**
     * Formats the <code>Pattern</code> as a <code>String</code>.
     *
     * @return String format of this pattern.
     */
    @Override
    public String toString() {
        return String.format("%s (pattern: %s)", getClass().getName(), getPattern());
    }
}
