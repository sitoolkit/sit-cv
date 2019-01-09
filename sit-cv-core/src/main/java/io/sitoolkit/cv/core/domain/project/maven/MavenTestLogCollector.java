package io.sitoolkit.cv.core.domain.project.maven;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import io.sitoolkit.cv.core.infra.util.CsvUtils;
import io.sitoolkit.util.buildtoolhelper.maven.MavenProject;

public class MavenTestLogCollector {

    public static void main(String[] args) {

        Path projectDir = Paths.get("../../dddsample-core").toAbsolutePath().normalize();

        String jarPath = "../sit-cv/sit-cv-tools/target/sit-cv-tools-1.0.0-beta.4-SNAPSHOT-jar-with-dependencies.jar";

        MavenProject project = MavenProject.load(projectDir);

        SqlLogListener stdoutListener = new SqlLogListener();

        Map<String, String> agentArgsMap = new HashMap<>();
        agentArgsMap.put("repository.annotation",
                "@org.springframework.stereotype\\.Repositor.*,.*sampleRepository.*");
        agentArgsMap.put("repository.methodMarker", SqlLogListener.REPOSITORY_METHOD_MARKER);
        String agentArgs = agentArgsMap.entrySet().stream()
                .map((e) -> e.getKey() + "=" + e.getValue())
                .collect(Collectors.joining(";", "=", ""));

        project.mvnw("test", "-DargLine=-javaagent:" + jarPath + agentArgs).stdout(stdoutListener)
                .execute();

        CsvUtils.bean2csv(stdoutListener.getSqlLogs(),
                Paths.get("./target/sit-cv-repository-vs-sql.csv"));
    }

}
