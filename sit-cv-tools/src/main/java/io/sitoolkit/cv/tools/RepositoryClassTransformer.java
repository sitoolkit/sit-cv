package io.sitoolkit.cv.tools;

import java.io.ByteArrayInputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.ProtectionDomain;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;

public class RepositoryClassTransformer implements ClassFileTransformer {

    private static String REPOSITORY_ANNOTATION = "@org.springframework.stereotype.Repository";
    private static String REPOSITORY_METHOD_MARKER = "[RepositoryMethod]";

    private static ClassPool classPool = ClassPool.getDefault();
    private static Path currentProjectPath = Paths.get("").toAbsolutePath();

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
            ProtectionDomain protectionDomain, byte[] classfileBuffer)
            throws IllegalClassFormatException {

        if (!isProjectClass(className)) {
            return null;
        }

        Optional<CtClass> ctClass = createCtClass(classfileBuffer, className);

        if (ctClass.isPresent() && isRepositoryClass(ctClass.get())) {
            System.out.println("Repository class found: " + className);
            return transformRepositoryMethods(ctClass.get());
        } else {
            return null;
        }

    }

    private Optional<CtClass> createCtClass(byte[] classfileBuffer, String className) {
        ByteArrayInputStream stream = new ByteArrayInputStream(classfileBuffer);
        try {
            return Optional.of(classPool.makeClass(stream));
        } catch (Exception e) {
            System.out.println("Create CtClass failed: " + className);
            e.printStackTrace();
            return Optional.empty();
        }
    }

    private boolean isProjectClass(String className) {
        Path path = null;
        try {
            path = Paths.get(classPool.find(className).toURI());
            return path.startsWith(currentProjectPath);
        } catch (URISyntaxException e) {
            System.out.println("Class check failed: " + className);
            e.printStackTrace();
            return false;
        }
    }

    private boolean isRepositoryClass(CtClass ctClass) {
        try {
            return Stream.of(ctClass.getAnnotations())
                    .filter((a) -> a.toString().equals(REPOSITORY_ANNOTATION)).findAny()
                    .isPresent();
        } catch (ClassNotFoundException e) {
            System.out.println("Get annotations failed: " + ctClass.getName());
            e.printStackTrace();
            return false;
        }
    }

    private byte[] transformRepositoryMethods(CtClass ctClass) {
        Arrays.asList(ctClass.getDeclaredMethods()).stream().forEach((ctMethod) -> {
            System.out.println("Repository method: " + ctMethod.getLongName());
            try {
                ctMethod.insertBefore("System.out.println(\"" + REPOSITORY_METHOD_MARKER
                        + ctMethod.getLongName() + "\");");
            } catch (CannotCompileException e) {
                System.out.println("Method transform Failed: " + ctMethod.getLongName());
                e.printStackTrace();
            }
        });

        try {
            return ctClass.toBytecode();
        } catch (Exception e) {
            System.out.println("Class transform failed: " + ctClass.getName());
            e.printStackTrace();
            return null;
        }
    }

}