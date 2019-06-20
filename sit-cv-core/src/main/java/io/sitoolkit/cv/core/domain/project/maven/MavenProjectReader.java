package io.sitoolkit.cv.core.domain.project.maven;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import io.sitoolkit.cv.core.app.config.ServiceFactory;
import io.sitoolkit.cv.core.domain.project.Project;
import io.sitoolkit.cv.core.domain.project.ProjectReader;
import io.sitoolkit.cv.core.domain.project.analyze.AnalyzeSqlParameterBuilder;
import io.sitoolkit.cv.core.domain.project.analyze.SqlLogListener;
import io.sitoolkit.cv.core.infra.config.SitCvConfig;
import io.sitoolkit.cv.core.infra.config.SitCvConfigReader;
import io.sitoolkit.cv.core.infra.project.maven.MavenSitCvToolsManager;
import io.sitoolkit.cv.core.infra.util.JsonUtils;
import io.sitoolkit.cv.core.infra.util.SitFileUtils;
import io.sitoolkit.util.buildtoolhelper.maven.MavenProject;

public class MavenProjectReader implements ProjectReader {

    public static void main(String[] args) {
        Path projectDir = Paths.get(args[0]);
        SitCvConfigReader configReader = new SitCvConfigReader();
        SitCvConfig config = configReader.read(projectDir);
        ServiceFactory factory = ServiceFactory.create(projectDir);
        Project project = factory.getProjectManager().getCurrentProject();
        new MavenProjectReader().generateSqlLog(project, config);
    }
    
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
    public boolean generateSqlLog(Project project, SitCvConfig sitCvConfig) {
        MavenProject mvnPrj = MavenProject.load(project.getDir());

        if (!mvnPrj.available()) {
            return false;
        }

        SqlLogListener sqlLogListener = new SqlLogListener(sitCvConfig.getSqlEnclosureFilter());

        MavenSitCvToolsManager.initialize(mvnPrj);
        Path agentJarPath = MavenSitCvToolsManager.getInstance().getJarPath();
        String javaAgentParameter = AnalyzeSqlParameterBuilder.build(agentJarPath, "maven",
                sitCvConfig.getSourceUrl());

        SitFileUtils.createDirectories(project.getSqlLogPath().getParent());

        mvnPrj.mvnw("test", "-DargLine=" + javaAgentParameter).stdout(sqlLogListener).execute();

        JsonUtils.obj2file(sqlLogListener.getSqlLogs(), project.getSqlLogPath());

        return true;
    }
}
