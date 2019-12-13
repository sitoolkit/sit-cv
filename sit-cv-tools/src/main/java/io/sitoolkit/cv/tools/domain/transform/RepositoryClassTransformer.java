package io.sitoolkit.cv.tools.domain.transform;

import java.io.ByteArrayInputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.ProtectionDomain;
import java.util.Arrays;
import java.util.Optional;

import io.sitoolkit.cv.tools.infra.config.RepositoryLoggerConfig;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

public class RepositoryClassTransformer implements ClassFileTransformer {

  private static ClassPool classPool = ClassPool.getDefault();
  private static Path currentProjectPath = Paths.get("").toAbsolutePath();

  private RepositoryLoggerConfig config;

  public RepositoryClassTransformer(RepositoryLoggerConfig config) {
    this.config = config;
  }

  @Override
  public byte[] transform(
      ClassLoader loader,
      String className,
      Class<?> classBeingRedefined,
      ProtectionDomain protectionDomain,
      byte[] classfileBuffer)
      throws IllegalClassFormatException {

    if (!isProjectClass(className)) {
      return null;
    }

    Optional<CtClass> ctClass = createCtClass(classfileBuffer, className);

    if (ctClass.isPresent()) {
      if (RepositoryFilter.match(ctClass.get(), config.getRepositoryFilter())) {
        System.out.println("Repository class found: " + className);
        return transformRepositoryMethods(ctClass.get());
      } else if (RepositoryFilter.match(ctClass.get(), config.getEntrypointFilter())) {
        System.out.println("Entrypoint class found: " + className);
        return transformEntrypointMethods(ctClass.get());
      }
    }
    return null;
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

  private byte[] transformRepositoryMethods(CtClass ctClass) {
    Arrays.asList(ctClass.getDeclaredMethods())
        .stream()
        .forEach(
            (ctMethod) -> {
              if (ctMethod.isEmpty()) {
                return;
              }

              System.out.println("Repository method: " + ctMethod.getLongName());
              try {
                ctMethod.insertBefore(
                    "System.out.println(\""
                        + config.getRepositoryMethodMarker()
                        + ctMethod.getLongName()
                        + "\");");
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

  private byte[] transformEntrypointMethods(CtClass ctClass) {
    Arrays.asList(ctClass.getDeclaredMethods())
        .stream()
        .forEach(
            ctMethod -> {
              try {
                ctMethod.instrument(createAddMsgExprEditor(ctMethod));
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

  private ExprEditor createAddMsgExprEditor(CtMethod ctMethod) {
    return new ExprEditor() {
      @Override
      public void edit(MethodCall methodCall) throws CannotCompileException {
        try {
          CtClass calledClass = classPool.get(methodCall.getClassName());
          if (RepositoryFilter.match(calledClass, config.getRepositoryFilter())) {
            methodCall.replace(addMsgBeforeMethodCall(methodCall));
          }
        } catch (NotFoundException e) {
          System.out.println("Method transform Failed: " + ctMethod.getLongName());
          e.printStackTrace();
        }
      }

      private String addMsgBeforeMethodCall(MethodCall methodCall) throws NotFoundException {
        return "System.out.println(\""
            + config.getRepositoryMethodMarker()
            + methodCall.getMethod().getLongName()
            + "\");"
            + "$_ = $proceed($$);";
      }
    };
  }
}
