package nl.omgwtfbbq.delver;

import java.io.InputStream;

/**
 * Performance metric class.
 */
public class Metric implements Comparable<Metric> {

    /**
     * Reference to the signature.
     */
    private Signature signature;

    private int callCount = 0;

    private long average = 0;

    private long max = 0;

    private long total = 0;

    public void update() {
        this.callCount++;
    }

    public void update(long start, long end) {
        this.callCount++;
        long diff = end - start;
        this.total += diff;
        this.max = Math.max(this.max, diff);
        this.average = total / callCount;
    }

    public int getCallCount() {
        return callCount;
    }

    public void setCallCount(int callCount) {
        this.callCount = callCount;
    }

    public long getAverage() {
        return average;
    }

    public void setAverage(long average) {
        this.average = average;
    }

    public long getMax() {
        return max;
    }

    public void setMax(long max) {
        this.max = max;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public Signature getSignature() {
        return signature;
    }

    public void setSignature(Signature signature) {
        this.signature = signature;
    }

    @Override
    public int compareTo(Metric o) {
        int av = Long.compare(o.getAverage(), this.getAverage());
        int cc = Integer.compare(o.getCallCount(), this.getCallCount());

        if (av == 0) {
            return cc;
        }

        return av;
    }

    @Override
    public String toString() {
        return "Metric{" +
                "signature=" + signature +
                ", callCount=" + callCount +
                ", average=" + average +
                '}';
    }
}
