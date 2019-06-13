package io.sitoolkit.cv.tools.app;

import java.lang.instrument.Instrumentation;

import io.sitoolkit.cv.tools.domain.transform.RepositoryClassTransformer;
import io.sitoolkit.cv.tools.infra.config.RepositoryLoggerArgumentParser;

public class RepositoryLogger {

    private static RepositoryLoggerArgumentParser argParser = new RepositoryLoggerArgumentParser();

    public static void premain(String agentArgs, Instrumentation instrumentation) {

        System.out.println("RepositoryLogger premain start. args: " + agentArgs);

        instrumentation.addTransformer(new RepositoryClassTransformer(argParser.parse(agentArgs)));
    }

}
