package nl.omgwtfbbq.delver;

import javassist.*;
import nl.omgwtfbbq.delver.conf.Config;

import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

/**
 * The classfile transformer.
 */
public class ClassTransformer implements ClassFileTransformer {

    /**
     * The loaded configuration. Must not be null.
     */
    private final Config config;

    public ClassTransformer(Config config) {
        this.config = config;
    }

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
                CtClass cc = cp.get(wut);
                CtMethod[] methods = cc.getDeclaredMethods();
                System.out.printf("Altering %d methods in %s\n", methods.length, wut);
                for (CtMethod m : methods) {
                    String returnType = m.getReturnType().getName();
                    String w = String.format("{ nl.omgwtfbbq.delver.UsageCollector.instance().add(\"%s %s\"); }", returnType, m.getLongName());
                    m.insertBefore(w);

                    System.out.printf("Inserted into method: %s\n", m.getName());
                }
                bytecode = cc.toBytecode();
                cc.detach();
            } catch (NotFoundException e) {
                // Silently swallow this. This may occur thanks to other generated classes,
                // Like what Spring does with cglib.
            } catch (CannotCompileException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        return bytecode;
    }

}
