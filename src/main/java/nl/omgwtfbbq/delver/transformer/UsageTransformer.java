package nl.omgwtfbbq.delver.transformer;

import javassist.*;
import javassist.bytecode.Descriptor;
import nl.omgwtfbbq.delver.Logger;
import nl.omgwtfbbq.delver.PerformanceCollector;
import nl.omgwtfbbq.delver.conf.Config;

/*
TODO: abstract methods/classes aren't done quite correctly.
See http://stackoverflow.com/questions/3291637/alternatives-to-java-lang-reflect-proxy-for-creating-proxies-of-abstract-classes
for some more information.

TODO: constructors?
 */

/**
 * The class file transformer.
 */
public class UsageTransformer extends AbstractMethodTransformer {

    public UsageTransformer(final Config config) {
        super(config);
    }

    @Override
    public void transform(CtClass cc, CtMethod m) throws NotFoundException, CannotCompileException {

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

        PerformanceCollector.instance().add(signature);

        Logger.debug("    Attempting to insert into: %s", m.getLongName());


        String w = String.format("{ nl.omgwtfbbq.delver.PerformanceCollector.instance().add(\"%s\", delver_pkg_start, System.currentTimeMillis()); }",
                signature);

        if (Modifier.isAbstract(m.getModifiers())) {
            Logger.debug("    Method is abstract, skipping %s", m.getLongName());
            return;
        }

        m.addLocalVariable("delver_pkg_start", CtClass.longType);
        m.insertBefore("{ delver_pkg_start = System.currentTimeMillis(); } ");
        m.insertAfter(w);

        Logger.debug("    Inserted into method: %s", m.getName());
    }
}
