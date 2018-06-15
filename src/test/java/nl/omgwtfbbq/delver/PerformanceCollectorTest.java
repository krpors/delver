package nl.omgwtfbbq.delver;

import org.junit.Test;

import java.io.IOException;

public class PerformanceCollectorTest {

    @Test
    public void test() throws IOException {
        PerformanceCollector c = PerformanceCollector.instance();
        c.add(new Signature("public", "void", "Object", "toString", "()"), 0, 0);

        c.write(System.out);
    }
}
