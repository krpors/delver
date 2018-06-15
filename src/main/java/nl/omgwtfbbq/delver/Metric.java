package nl.omgwtfbbq.delver;

/**
 * Performance metric class.
 */
public class Metric {

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
}
