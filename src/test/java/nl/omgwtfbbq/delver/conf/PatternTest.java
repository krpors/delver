package nl.omgwtfbbq.delver.conf;

import org.junit.Assert;
import org.junit.Test;

public class PatternTest {

    @Test
    public void endMatching() {
        Pattern p = new Pattern("nl/omgwtfbbq/delver/.*");

        Assert.assertTrue(p.matches("nl/omgwtfbbq/delver/MyClass"));
        Assert.assertTrue(p.matches("nl/omgwtfbbq/delver/subpkg/AlsoMatches"));

        Assert.assertFalse(p.matches("nl/omgwtfbbq/other/Clazz"));
    }

    @Test
    public void inbetweenMatching() {
        Pattern p = new Pattern("nl/.*/delver/.*");

        Assert.assertTrue(p.matches("nl/anythinghere/delver/MainClass"));
        Assert.assertTrue(p.matches("nl/anythinghere/andmoreinbetween/delver/MainClass"));
    }

    @Test
    public void inbetweenMatchingNonGreedy() {
        Pattern p = new Pattern("nl/\\p{Alnum}+?/delver/.*");

        Assert.assertTrue(p.matches("nl/anythinghere/delver/MainClass"));
        Assert.assertTrue(p.matches("nl/othercrap/delver/MainClass"));

        Assert.assertFalse(p.matches("nl/anythinghere/failhere/delver/MainClass"));
        Assert.assertFalse(p.matches("nl/anythinghere/cruft/delver/MainClass"));
        Assert.assertFalse(p.matches("nl/anythinghere/fail/crap/delver/MainClass"));
    }

}
