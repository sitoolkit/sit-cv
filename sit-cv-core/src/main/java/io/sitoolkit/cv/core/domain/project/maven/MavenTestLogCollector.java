package io.sitoolkit.cv.core.domain.project.maven;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import io.sitoolkit.cv.core.infra.util.CsvUtils;
import io.sitoolkit.cv.core.infra.util.JarUtils;
import io.sitoolkit.cv.core.infra.util.PackageUtils;
import io.sitoolkit.util.buildtoolhelper.maven.MavenProject;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MavenTestLogCollector {

    private static final String AGENT_JAR_NAME = "sit-cv-tools";

    public static void main(String[] args) {

        Path projectDir = Paths.get("../../dddsample-core").toAbsolutePath().normalize();

        MavenProject project = MavenProject.load(projectDir);

        Optional<String> jarPath = resolveAgentJar(project);

        if (!jarPath.isPresent()) {
            return;
        }

        SqlLogListener stdoutListener = new SqlLogListener();

         Map<String, String> agentArgsMap = new HashMap<>();
         agentArgsMap.put("repository.annotation",
         "@org.springframework.stereotype.Repository");
         agentArgsMap.put("repository.methodMarker",
         SqlLogListener.REPOSITORY_METHOD_MARKER);
         String agentArgs = agentArgsMap.entrySet().stream()
         .map((e) -> e.getKey() + "=" + e.getValue())
         .collect(Collectors.joining(";", "=", ""));

         project.mvnw("test", "-DargLine=-javaagent:" + jarPath.get() + agentArgs)
         .stdout(stdoutListener).execute();

         CsvUtils.bean2csv(stdoutListener.getSqlLogs(),
         Paths.get("./target/sit-cv-repository-vs-sql.csv"));
    }

    private static Optional<String> resolveAgentJar(MavenProject project) {
        String packageVersion = PackageUtils.getVersion();
        MavenDependencyClasspathListener listener = new MavenDependencyClasspathListener(
                AGENT_JAR_NAME);

        project.mvnw("dependency:build-classpath").stdout(listener).execute();

        String jarPath = listener.getClasspath();
        if (jarPath == null) {
            log.error("Dependency not found. Please add dependency to '{}:{}'", AGENT_JAR_NAME, packageVersion);
            return Optional.empty();
        }

        log.info("Agent jar found: {}", jarPath);

        String agentVersion = JarUtils.getImplementationVersion(jarPath);
        if (!packageVersion.equals(agentVersion)) {
            log.error("Invalid dependency version. Please change dependency '{}:{}' to '{}:{}'",
                    AGENT_JAR_NAME, agentVersion, AGENT_JAR_NAME, packageVersion);
            return Optional.empty();
        }

        return Optional.of(jarPath);
    }

}
