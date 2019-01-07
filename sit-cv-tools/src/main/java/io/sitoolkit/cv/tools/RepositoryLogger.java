package io.sitoolkit.cv.tools;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringReader;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.nio.file.Paths;
import java.security.ProtectionDomain;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

import org.apache.commons.io.output.TeeOutputStream;

import io.sitoolkit.cv.core.domain.project.maven.SqlLogListener;
import io.sitoolkit.cv.core.infra.util.CsvUtils;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RepositoryLogger {
    private static String SQL_LOG_PATH = "./target/sit-cv-repository-vs-sql.csv";
    private static String REPOSITORY_CLASS_REGEXP = ".*Repository.*";
    private static String REPOSITORY_ANNOTATION = "@org.springframework.stereotype.Repository";

    private static ClassPool classPool;
    private static PrintStream originalSystemOut;
    private static PrintStream logOutputStream;
    private static ByteArrayOutputStream logByteArrayStream;

    public static void premain(String agentArgs, Instrumentation instrumentation) {
        classPool = ClassPool.getDefault();

        originalSystemOut = System.out;
        logOutputStream = createLogOutputStream();

        log.info("RepositoryLogger START");

        instrumentation.addTransformer(new RepositoryClassTransformer());

        Runtime.getRuntime().addShutdownHook(new SQLLogWriterThread());
    }

    private static PrintStream createLogOutputStream() {
        logByteArrayStream = new ByteArrayOutputStream();
        TeeOutputStream teeStream = new TeeOutputStream(new PrintStream(logByteArrayStream, true),
                System.out);
        return new PrintStream(teeStream);
    }

    private static String getLog() {
        try {
            logByteArrayStream.flush();
        } catch (IOException e) {
            log.warn("ByteArrayOutputStream flush failed", e);
        }
        return new String(logByteArrayStream.toByteArray());
    }

    private static class RepositoryClassTransformer implements ClassFileTransformer {
        @Override
        public byte[] transform(ClassLoader loader, String className,
                Class<?> classBeingRedefined, ProtectionDomain protectionDomain,
                byte[] classfileBuffer) throws IllegalClassFormatException {

            System.setOut(logOutputStream);

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
                    log.info("Find repository class: {}", className);

                    Arrays.asList(ctClass.getDeclaredMethods()).stream().forEach((ctMethod) -> {
                        log.info("Find repository method: {}", ctMethod.getLongName());
                        try {
                            ctMethod.insertBefore("System.out.println(\"[RepositoryMethod]"
                                    + ctMethod.getLongName() + "\");");
                        } catch (CannotCompileException e) {
                            log.warn("Method transform Failed: {}", ctMethod.getLongName(), e);
                        }
                    });
                    return ctClass.toBytecode();

                }

            } catch (Exception e) {
                log.warn("Transform Failed: {}", className, e);
            }

            return null;
        }
    }

    private static class SQLLogWriterThread extends Thread {
        @Override
        public void run() {
            System.setOut(originalSystemOut);

            log.info("RepositoryLogger ShutdownHook START");

            writeSqlLog(getLog());

            log.info("RepositoryLogger ShutdownHook END");
        }

        private void writeSqlLog(String logStr) {
            try (BufferedReader bufferedReader = new BufferedReader(new StringReader(logStr))) {
                SqlLogListener sqlLogListener = new SqlLogListener();

                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    sqlLogListener.nextLine(line);
                }

                log.info("RepositoryLogger SQL Count: {}", sqlLogListener.getSqlLogs().size());

                CsvUtils.bean2csv(sqlLogListener.getSqlLogs(), Paths.get(SQL_LOG_PATH));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
