package nl.omgwtfbbq.delver;

/**
 * Formats a {@link Signature}
 */
public final class SignatureFormatter {

    /**
     * Formats a {@link Signature} with semicolon separators.
     *
     * @param signature The signature  to format.
     * @return The Signature formatted as a String.
     */
    public static String format(final Signature signature) {
        StringBuilder w = new StringBuilder();
        w.append(signature.getModifiers());
        w.append(";");
        w.append(signature.getReturnType());
        w.append(";");
        w.append(signature.getClassName());
        w.append(";");
        w.append(signature.getMethod());
        w.append(signature.getSignature());
        return w.toString();
    }
}
