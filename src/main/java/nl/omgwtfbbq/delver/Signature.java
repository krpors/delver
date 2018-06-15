package nl.omgwtfbbq.delver;

import java.util.Objects;

/**
 * A signature is a very simple POJO for containing a complete method signature.
 */
public class Signature {

    private String modifiers;

    private String returnType;

    private String className;

    private String method;

    private String signature;

    public Signature() {
    }

    public Signature(String modifiers, String returnType, String classname, String method, String signature) {
        this.modifiers = modifiers;
        this.returnType = returnType;
        this.className = classname;
        this.method = method;
        this.signature = signature;
    }

    public String getModifiers() {
        return modifiers;
    }

    public void setModifiers(String modifiers) {
        this.modifiers = modifiers;
    }

    public String getReturnType() {
        return returnType;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Signature metric = (Signature) o;
        return Objects.equals(modifiers, metric.modifiers) &&
                Objects.equals(returnType, metric.returnType) &&
                Objects.equals(className, metric.className) &&
                Objects.equals(method, metric.method) &&
                Objects.equals(signature, metric.signature);
    }

    @Override
    public int hashCode() {

        return Objects.hash(modifiers, returnType, className, method, signature);
    }
}
