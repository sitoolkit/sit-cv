package io.sitoolkit.cv.core.domain.project.maven;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.sitoolkit.cv.core.infra.util.CsvUtils;
import io.sitoolkit.util.buildtoolhelper.maven.MavenProject;
import io.sitoolkit.util.buildtoolhelper.maven.MavenUtils;

public class MavenTestLogCollector {

    private static final String AGENT_JAR_SUFFIX = "-jar-with-dependencies.jar";
    private static final String AGENT_JAR_NAME = "sit-cv-tools";
    private static final String AGENT_JAR_VERSION = "1.0.0-beta.4-SNAPSHOT";

    public static void main(String[] args) {

        Path projectDir = Paths.get("../../dddsample-core").toAbsolutePath().normalize();

        String jarPath = getAgentJarPath();

        MavenProject project = MavenProject.load(projectDir);

        SqlLogListener stdoutListener = new SqlLogListener();

        Map<String, String> agentArgsMap = new HashMap<>();
        agentArgsMap.put("repository.annotation", "@org.springframework.stereotype.Repository");
        agentArgsMap.put("repository.methodMarker", SqlLogListener.REPOSITORY_METHOD_MARKER);
        String agentArgs = agentArgsMap.entrySet().stream()
                .map((e) -> e.getKey() + "=" + e.getValue())
                .collect(Collectors.joining(";", "=", ""));

        project.mvnw("test", "-DargLine=-javaagent:" + jarPath + agentArgs).stdout(stdoutListener)
                .execute();

        CsvUtils.bean2csv(stdoutListener.getSqlLogs(),
                Paths.get("./target/sit-cv-repository-vs-sql.csv"));
    }

    private static String getAgentJarPath() {
        Path repositoryPath = MavenUtils.getLocalRepository().toPath();
        Path artifactPath = repositoryPath
                .resolve("io/sitoolkit/cv/" + AGENT_JAR_NAME + "/" + AGENT_JAR_VERSION);

        Optional<File> jar = Stream.of(artifactPath.toFile().listFiles())
                .filter((f) -> f.toString().endsWith(AGENT_JAR_SUFFIX)).findAny();

        if (jar.isPresent()) {
            return jar.get().toString();
        } else {
            return Paths
                    .get("../" + AGENT_JAR_NAME + "/target/" + AGENT_JAR_NAME + "-"
                            + AGENT_JAR_VERSION + AGENT_JAR_SUFFIX)
                    .toAbsolutePath().normalize().toString();
        }
    }

}
