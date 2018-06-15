package nl.omgwtfbbq.delver.transformer;

import javassist.*;
import javassist.bytecode.Descriptor;
import nl.omgwtfbbq.delver.Logger;
import nl.omgwtfbbq.delver.PerformanceCollector;
import nl.omgwtfbbq.delver.Signature;
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
public class PerformanceTransformer extends AbstractMethodTransformer {

    public PerformanceTransformer(final Config config) {
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

        Logger.debug("    Attempting to insert into: %s", m.getLongName());

        String code = "{";
        code += "nl.omgwtfbbq.delver.Signature s = new nl.omgwtfbbq.delver.Signature();";
        code += String.format("s.setModifiers(\"%s\");", modifiers);
        code += String.format("s.setReturnType(\"%s\");", returnType);
        code += String.format("s.setClassName(\"%s\");", cc.getName());
        code += String.format("s.setMethod(\"%s\");", m.getName());
        code += String.format("s.setSignature(\"%s\");", Descriptor.toString(m.getSignature()));
        code += String.format("nl.omgwtfbbq.delver.PerformanceCollector.instance().add(s, delver_pkg_start, System.currentTimeMillis());");
        code += "}";

        if (Modifier.isAbstract(m.getModifiers())) {
            Logger.debug("    Method is abstract, skipping %s", m.getLongName());
            return;
        }

        m.addLocalVariable("delver_pkg_start", CtClass.longType);
        m.insertBefore("{ delver_pkg_start = System.currentTimeMillis(); } ");
        m.insertAfter(code);

        // add initial usage, set it to 0 so we know it's found, but zero calls.
        Signature s = new Signature();
        s.setModifiers(modifiers);
        s.setReturnType(returnType);
        s.setClassName(cc.getName());
        s.setMethod(m.getName());
        s.setSignature(Descriptor.toString(m.getSignature()));
        PerformanceCollector.instance().add(s, 0, 0);

        Logger.debug("    Inserted into method: %s", m.getName());
    }
}
