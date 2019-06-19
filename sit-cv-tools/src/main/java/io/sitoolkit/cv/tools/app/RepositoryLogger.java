package io.sitoolkit.cv.tools.app;

import java.lang.instrument.Instrumentation;

import io.sitoolkit.cv.tools.domain.transform.RepositoryClassTransformer;
import io.sitoolkit.cv.tools.infra.config.RepositoryLoggerArgumentParser;
import io.sitoolkit.cv.tools.infra.config.RepositoryLoggerConfig;

public class RepositoryLogger {

    private static RepositoryLoggerArgumentParser argParser = new RepositoryLoggerArgumentParser();

    public static void premain(String agentArgs, Instrumentation instrumentation) {

        RepositoryLoggerConfig config = argParser.parse(agentArgs);

        if (isGradleWorkerMainProcess(config.getProjectType())) {
            return;
        }

        System.out.println("RepositoryLogger premain start. args: " + agentArgs);

        instrumentation.addTransformer(new RepositoryClassTransformer(argParser.parse(agentArgs)));
    }

    private static boolean isGradleWorkerMainProcess(String projectType) {
        return projectType.equals("gradle")
                && !System.getProperty("sun.java.command").contains("GradleWorkerMain");
    }

}
