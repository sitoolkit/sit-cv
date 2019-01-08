package io.sitoolkit.cv.tools;

import java.io.ByteArrayInputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;

public class RepositoryLogger {
    private static String REPOSITORY_CLASS_REGEXP = ".*Repository.*";
    private static String REPOSITORY_ANNOTATION = "@org.springframework.stereotype.Repository";

    private static ClassPool classPool;

    public static void premain(String agentArgs, Instrumentation instrumentation) {
        classPool = ClassPool.getDefault();

        instrumentation.addTransformer(new RepositoryClassTransformer());
    }

    private static class RepositoryClassTransformer implements ClassFileTransformer {
        @Override
        public byte[] transform(ClassLoader loader, String className,
                Class<?> classBeingRedefined, ProtectionDomain protectionDomain,
                byte[] classfileBuffer) throws IllegalClassFormatException {

            if (className.matches(REPOSITORY_CLASS_REGEXP)) {
                return transformRepositoryMethods(className, classfileBuffer);
            } else {
                return null;
            }

        }

        private byte[] transformRepositoryMethods(String className, byte[] classfileBuffer) {
            try {
                ByteArrayInputStream stream = new ByteArrayInputStream(classfileBuffer);
                CtClass ctClass = classPool.makeClass(stream);

                Optional<Object> annotation = Stream.of(ctClass.getAnnotations())
                        .filter((a) -> a.toString().equals(REPOSITORY_ANNOTATION)).findAny();

                if (annotation.isPresent()) {
//                    log.info("Find repository class: {}", className);

                    Arrays.asList(ctClass.getDeclaredMethods()).stream().forEach((ctMethod) -> {
//                        log.info("Find repository method: {}", ctMethod.getLongName());
                        try {
                            ctMethod.insertBefore("System.out.println(\"[RepositoryMethod]"
                                    + ctMethod.getLongName() + "\");");
                        } catch (CannotCompileException e) {
//                            log.warn("Method transform Failed: {}", ctMethod.getLongName(), e);
                        }
                    });
                    return ctClass.toBytecode();

                }

            } catch (Exception e) {
//                log.warn("Transform Failed: {}", className, e);
            }

            return null;
        }
    }

}