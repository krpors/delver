package nl.omgwtfbbq.delver;

import javassist.*;
import javassist.bytecode.Descriptor;
import nl.omgwtfbbq.delver.conf.Config;

import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

/**
 * The class file transformer.
 */
public class ClassTransformer implements ClassFileTransformer {

    /**
     * The loaded configuration. Must not be null.
     */
    private final Config config;

    /**
     * Creates the ClassTransformer. The supplied config must not be null.
     *
     * @param config The Configuration.x
     */
    public ClassTransformer(Config config) {
        this.config = config;
    }

    /**
     * Transforms the bytecode of the given class. A statement is inserted which calls the <code>UsageCollector</code>
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
     * @see java.lang.instrument.ClassFileTransformer
     * @see javassist.LoaderClassPath
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

            String wut = className.replace("/", ".");

            try {
                ClassPool cp = ClassPool.getDefault();
                cp.childFirstLookup = true;
                // This seems to make it work for multiple class loader cruft in WebSphere...
                // TODO: check other app servers etc.
                cp.insertClassPath(new LoaderClassPath(loader));

                CtClass cc = cp.get(wut);
                CtMethod[] methods = cc.getDeclaredMethods();
                Logger.debug("Altering %d methods in %s", methods.length, wut);
                for (CtMethod m : methods) {
                    String modifiers = Modifier.toString(m.getModifiers());
                    String returnType = m.getReturnType().getName();

                    // Make a comma separated String
                    String signature = String.format("%s;%s;%s;%s%s",
                            modifiers,
                            returnType,
                            cc.getName(),
                            m.getName(),
                            Descriptor.toString(m.getSignature()));
                    // add initial usage, set it to 0 so we know it's found, but zero calls.
                    UsageCollector.instance().add(signature);

                    String w = String.format("{ nl.omgwtfbbq.delver.UsageCollector.instance().add(\"%s\"); }",
                            signature);
                    m.insertBefore(w);

                    Logger.debug("Inserted into method: %s", m.getName());
                }
                bytecode = cc.toBytecode();
                cc.detach();
            } catch (NotFoundException e) {
                Logger.error("NotFoundException on class '%s': %s", className, e.getMessage());
                e.printStackTrace();
            } catch (CannotCompileException e) {
                Logger.error("Cannot compile class '%s': %s", className, e.getMessage());
            } catch (IOException e) {
                Logger.error("IOException while transforming class '%s': %s", className, e.getMessage());
            } catch (Exception ex) {
                Logger.error("Generic exception occurred while transforming class '%s': %s", className, ex.getMessage());
            }
        }

        return bytecode;
    }

}
