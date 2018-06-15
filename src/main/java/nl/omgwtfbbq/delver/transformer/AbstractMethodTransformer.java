package nl.omgwtfbbq.delver.transformer;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.LoaderClassPath;
import javassist.NotFoundException;
import javassist.bytecode.Descriptor;
import nl.omgwtfbbq.delver.Logger;
import nl.omgwtfbbq.delver.conf.Config;

import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

/*
TODO: abstract methods/classes aren't done quite correctly.
See http://stackoverflow.com/questions/3291637/alternatives-to-java-lang-reflect-proxy-for-creating-proxies-of-abstract-classes
for some more information.
TODO: constructors?
 */

/**
 * This is the basic method transformer. One must override {@link #transform(CtClass, CtMethod)} with logic.
 *
 * @see PerformanceTransformer
 */
public abstract class AbstractMethodTransformer implements ClassFileTransformer {

    /**
     * The loaded configuration. Must not be null.
     */
    private final Config config;

    /**
     * Creates the PerformanceTransformer. The supplied config must not be null.
     *
     * @param config The Configuration.x
     */
    public AbstractMethodTransformer(Config config) {
        this.config = config;
    }

    /**
     * Transforms the bytecode of the given class. A statement is inserted which calls the <code>PerformanceCollector</code>
     * to add a signature. The inclusion and exclusion patterns from the <code>Config</code> object given in the constructor
     * is used to determine whether the declared methods in the class are transformed or not.
     *
     * @param loader              The ClassLoader used to load the class. The <code>LoaderClassPath</code> is used to
     *                            add the ClassPath of the ClassLoader. This is done to support application servers,
     *                            which usually have a hierarchy of ClassLoaders etc.
     * @param className           The classname which is about to be transformed (or not).
     * @param classBeingRedefined Always false.
     * @param protectionDomain    Unused.
     * @param classfileBuffer     The byte code which contains the class's bytecode.
     * @return The transformed bytecode, or the same bytecode if there was an explicit exclusion, or the class name
     * does not match the inclusion path in the given <code>Config</code>.
     * @throws IllegalClassFormatException
     * @see ClassFileTransformer
     * @see LoaderClassPath
     */
    public byte[] transform(ClassLoader loader, String className,
                            Class<?> classBeingRedefined, ProtectionDomain protectionDomain,
                            byte[] classfileBuffer) throws IllegalClassFormatException {
        if (config == null) {
            // Simply return the original bytecode. 'Fail' silently. The fact that the config
            // is null should be reported by the Main class.
            return classfileBuffer;
        }

        byte[] bytecode = classfileBuffer;

        // forceful exclusions
        if (config.isExcluded(className)) {
            Logger.debug("Excluded class '%s'", className);
            return bytecode;
        }

        // Explicit inclusions
        if (config.isIncluded(className)) {
            Logger.debug("Included class '%s'", className);

            String wut = Descriptor.toJavaName(className);

            try {
                ClassPool cp = ClassPool.getDefault();
                cp.childFirstLookup = true;
                // This seems to make it work for multiple class loader cruft in WebSphere...
                // TODO: check other app servers etc.
                cp.insertClassPath(new LoaderClassPath(loader));

                CtClass cc = cp.get(wut);
                Logger.debug("Checking class %s", wut);

                CtMethod[] methods = cc.getDeclaredMethods();
                Logger.debug("  Altering %d methods in %s", methods.length, wut);
                for (CtMethod m : methods) {
                    transform(cc, m);
                }
                bytecode = cc.toBytecode();
                cc.detach();
            } catch (NotFoundException e) {
                Logger.error("NotFoundException on class '%s': %s", className, e.getMessage());
                e.printStackTrace();
            } catch (CannotCompileException e) {
                Logger.error("Cannot compile class '%s': %s", className, e.getMessage());
                e.printStackTrace(System.out);
            } catch (IOException e) {
                Logger.error("IOException while transforming class '%s': %s", className, e.getMessage());
            } catch (Exception ex) {
                Logger.error("Generic exception occurred while transforming class '%s': %s", className, ex.getMessage());
                ex.printStackTrace(System.out);
            }
        }

        return bytecode;
    }

    public abstract void transform(CtClass cc, CtMethod m) throws NotFoundException, CannotCompileException;
}
