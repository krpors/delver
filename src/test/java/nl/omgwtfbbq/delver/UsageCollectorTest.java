package nl.omgwtfbbq.delver;

import org.junit.Test;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;

public class UsageCollectorTest {

    @Test
    public void test() throws IOException {
        UsageCollector c = UsageCollector.instance();
        c.add("public static void thing");
        c.add("public static void asd");
        c.add("public static void asd");
        c.add("public static void asd");

        c.write(System.out);
    }
}
