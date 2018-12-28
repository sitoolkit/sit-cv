package io.sitoolkit.cv.tools;

import java.io.ByteArrayInputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;
import java.util.Arrays;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;

public class RepositoryLogger {
    private static ClassPool classPool;

    public static void premain(String agentArgs, Instrumentation instrumentation) {
        classPool = ClassPool.getDefault();

        instrumentation.addTransformer(new ClassFileTransformer() {
            @Override
            public byte[] transform(ClassLoader loader, String className,
                    Class<?> classBeingRedefined, ProtectionDomain protectionDomain,
                    byte[] classfileBuffer) throws IllegalClassFormatException {

                if (className.matches(".*Repository.*")) {

                    try {
                        ByteArrayInputStream stream = new ByteArrayInputStream(classfileBuffer);
                        CtClass ctClass = classPool.makeClass(stream);

                        Object[] as = ctClass.getAnnotations();
                        boolean find = false;
                        for (Object a : as) {
                            if (a.toString().equals("@org.springframework.stereotype.Repository")) {
                                find = true;
                                break;
                            }
                        }

                        if (find) {
                            System.out.println("REPO_CLASS => " + className);

                            Arrays.asList(ctClass.getDeclaredMethods()).stream()
                                    .forEach((ctMethod) -> {
                                        System.out.println(
                                                "METHOD_NAME => " + ctMethod.getLongName());
                                        try {
                                            ctMethod.insertBefore(
                                                    "System.out.println(\"[RepositoryMethod]"
                                                            + ctMethod.getLongName() + "\");");
                                        } catch (CannotCompileException e) {
                                            // TODO Auto-generated catch block
                                            e.printStackTrace();
                                        }
                                    });
                            return ctClass.toBytecode();

                        }
                        return null;

                    } catch (Exception ex) {
                        IllegalClassFormatException e = new IllegalClassFormatException();
                        e.initCause(ex);
                        throw e;
                    }
                } else {
                    return null;
                }
            }
        });
    }
}
