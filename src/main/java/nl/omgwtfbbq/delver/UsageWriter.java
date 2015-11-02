package nl.omgwtfbbq.delver;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public final class UsageWriter {

    private static final String FILE = "delver.log";

    private static UsageWriter usageWriter;

    public static final int THRESHOLD_DEFAULT = 100;

    private int counter = 0;

    private Map<String, Integer> calls = new HashMap<String, Integer>();

    private int threshold = THRESHOLD_DEFAULT;

    /**
     * The output file.
     */
    private String outputFile = FILE;

    private boolean writeErrorReported = false;

    private UsageWriter() {
    }

    public static UsageWriter instance() {
        if (usageWriter == null) {
            usageWriter = new UsageWriter();
        }

        return usageWriter;
    }

    /**
     * Sets threshold. Can't be smaller than 100 as a sort of safeguard.
     *
     * @param threshold The threshold.
     */
    public void setThreshold(int threshold) {
        this.threshold = Math.max(threshold, THRESHOLD_DEFAULT);
        Logger.debug("Setting threshold to '%d'", this.threshold);
    }

    public void setOutputFile(String outputFile) {
        if (outputFile == null || outputFile.trim().equals("")) {
            this.outputFile = FILE;
        } else {
            this.outputFile = outputFile;
        }

        Logger.debug("Output file is '%s'", this.outputFile);
    }

    private void output() {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(outputFile);

            for (String c : calls.keySet()) {
                fos.write(String.format("%d;%s\n", calls.get(c), c).getBytes());
            }
        } catch (IOException e) {
            if (!writeErrorReported) {
                Logger.error("Unable to write to file '%s' due to '%s'. " +
                        "This error will be reported only once!", outputFile, e.getMessage());
                writeErrorReported = true;
            }
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    // swallow.
                }
            }
        }

    }

    /**
     * Adds a signature.
     *
     * @param signature The signature to add.
     */
    public void add(String signature) {
        if (calls.containsKey(signature)) {
            calls.put(signature, calls.get(signature) + 1);
        } else {
            calls.put(signature, 1);
        }

        if (++counter >= threshold) {
            counter = 0;
            output();
        }
    }
}
