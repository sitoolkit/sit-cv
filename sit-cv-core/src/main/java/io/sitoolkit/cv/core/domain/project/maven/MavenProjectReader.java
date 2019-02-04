package io.sitoolkit.cv.core.domain.project.maven;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import io.sitoolkit.cv.core.domain.project.Project;
import io.sitoolkit.cv.core.domain.project.ProjectReader;
import io.sitoolkit.cv.core.infra.project.maven.MavenSitCvToolsManager;
import io.sitoolkit.cv.core.infra.util.JsonUtils;
import io.sitoolkit.cv.core.infra.util.SitFileUtils;
import io.sitoolkit.util.buildtoolhelper.maven.MavenProject;

public class MavenProjectReader implements ProjectReader {

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

        SitFileUtils.createDirectories(project.getSqlLogPath().getParent());

        mvnPrj.mvnw("test", "-DargLine=-javaagent:" + jarPath.toString() + agentArgs)
                .stdout(sqlLogListener).execute();

        Path sqlLogPath = project.getDir().resolve(project.getSqlLogPath());
        JsonUtils.obj2file(sqlLogListener.getSqlLogs(), sqlLogPath);

        return true;
    }
}
