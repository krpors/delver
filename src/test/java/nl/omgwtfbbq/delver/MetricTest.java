package nl.omgwtfbbq.delver;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MetricTest {

    @Test
    public void comparator() {
        List<Metric> list = new ArrayList<>();
        {
            Metric metric = new Metric();
            metric.setAverage(5000);
            metric.setCallCount(1000);
            list.add(metric);
        }
        {
            Metric metric = new Metric();
            metric.setAverage(7000);
            metric.setCallCount(1000);
            list.add(metric);
        }
        {
            Metric metric = new Metric();
            metric.setAverage(1000);
            metric.setCallCount(1000);
            list.add(metric);
        }
        {
            Metric metric = new Metric();
            metric.setAverage(5000);
            metric.setCallCount(7000);
            list.add(metric);
        }

        list.forEach(System.out::println);
        Collections.sort(list);
        System.out.println("--------- AFTER SORT ---------");
        list.forEach(System.out::println);
    }
}