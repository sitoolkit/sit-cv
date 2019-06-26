package io.sitoolkit.cv.tools.app;

import java.lang.instrument.Instrumentation;

import io.sitoolkit.cv.tools.domain.transform.RepositoryClassTransformer;
import io.sitoolkit.cv.tools.infra.config.RepositoryLoggerArgumentParser;
import io.sitoolkit.cv.tools.infra.config.RepositoryLoggerConfig;

public class RepositoryLogger {

    private static RepositoryLoggerArgumentParser argParser = new RepositoryLoggerArgumentParser();

    public static void premain(String agentArgs, Instrumentation instrumentation) {

        System.out.println("RepositoryLogger premain start. args: " + agentArgs);

        RepositoryLoggerConfig config = argParser.parse(agentArgs);

        if (config.getProjectType().equals("gradle")
                && !System.getProperty("sun.java.command").contains("GradleWorkerMain")) {
            System.out.println(
                    "RepositoryLogger premain exit. Because it was executed by a command other than GradleWorkerMain.");
            return;
        }

        instrumentation.addTransformer(new RepositoryClassTransformer(argParser.parse(agentArgs)));
    }

}
