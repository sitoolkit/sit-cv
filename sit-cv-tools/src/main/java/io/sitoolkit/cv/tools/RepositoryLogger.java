package io.sitoolkit.cv.tools;

import java.lang.instrument.Instrumentation;

public class RepositoryLogger {

    public static void premain(String agentArgs, Instrumentation instrumentation) {

        System.out.println("RepositoryLogger premain start: " + agentArgs);

        instrumentation.addTransformer(new RepositoryClassTransformer());
    }

}
