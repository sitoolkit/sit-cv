package io.sitoolkit.cv.core.domain.project.maven;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.type.TypeReference;

import io.sitoolkit.cv.core.domain.crud.SqlPerMethod;
import io.sitoolkit.cv.core.domain.project.Project;
import io.sitoolkit.cv.core.domain.project.ProjectReader;
import io.sitoolkit.cv.core.infra.project.maven.MavenSitCvToolsManager;
import io.sitoolkit.cv.core.infra.util.JsonUtils;
import io.sitoolkit.cv.core.infra.util.SitFileUtils;
import io.sitoolkit.util.buildtoolhelper.maven.MavenProject;

public class MavenProjectReader implements ProjectReader {

    private static final String LOG_DIR = "./target/sit-cv";
    private static final String TEST_LOG_FILE = LOG_DIR + "/sit-cv-unit-test.log";
    private static final String SQL_LOG_FILE = LOG_DIR + "/sit-cv-repository-vs-sql.json";

    @Override
    public Optional<Project> read(Path projectDir) {

        MavenProject mvnPrj = MavenProject.load(projectDir);

        if (!mvnPrj.available()) {
            return Optional.empty();
        }

        MavenProjectInfoListener listener = new MavenProjectInfoListener(projectDir);

        mvnPrj.mvnw("compile", "-X").stdout(listener).execute();

        return Optional.of(listener.getProject());
    }

    @Override
    public List<SqlPerMethod> getSqlLog(Project project) {

        MavenProject mvnPrj = MavenProject.load(project.getDir());

        if (!mvnPrj.available()) {
            return Collections.emptyList();
        }

        return JsonUtils.file2obj(project.getDir().resolve(SQL_LOG_FILE),
                new TypeReference<List<SqlPerMethod>>() {
                });
    }

    @Override
    public boolean generateSqlLog(Project project) {
        MavenProject mvnPrj = MavenProject.load(project.getDir());

        if (!mvnPrj.available()) {
            return false;
        }

        SqlLogListener sqlLogListener = new SqlLogListener();

        MavenSitCvToolsManager.initialize(mvnPrj);
        Path jarPath = MavenSitCvToolsManager.getInstance().getJarPath();

        Map<String, String> agentArgsMap = new HashMap<>();
        agentArgsMap.put("repository.annotation", "@org.springframework.stereotype.Repository");
        agentArgsMap.put("repository.methodMarker", SqlLogListener.REPOSITORY_METHOD_MARKER);
        String agentArgs = agentArgsMap.entrySet().stream()
                .map((e) -> e.getKey() + "=" + e.getValue())
                .collect(Collectors.joining(";", "=", ""));

        Path testLogPath = project.getDir().resolve(TEST_LOG_FILE);
        SitFileUtils.createDirectories(testLogPath.getParent());

        try (FileWriter fw = new FileWriter(testLogPath.toFile());
                BufferedWriter bw = new BufferedWriter(fw)) {

            TestLogListener testLogListener = new TestLogListener(bw);

            mvnPrj.mvnw("test", "-DargLine=-javaagent:" + jarPath.toString() + agentArgs)
                    .stdout(sqlLogListener).stdout(testLogListener).execute();

        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        JsonUtils.obj2file(sqlLogListener.getSqlLogs(), project.getDir().resolve(SQL_LOG_FILE));

        return true;
    }
}
