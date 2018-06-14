package nl.omgwtfbbq.delver;

import org.junit.Test;

import java.io.IOException;

public class PerformanceCollectorTest {

    @Test
    public void test() throws IOException {
        PerformanceCollector c = PerformanceCollector.instance();
        c.add("public static void thing");
        c.add("public static void asd");
        c.add("public static void asd");
        c.add("public static void asd");

        c.write(System.out);
    }
}
