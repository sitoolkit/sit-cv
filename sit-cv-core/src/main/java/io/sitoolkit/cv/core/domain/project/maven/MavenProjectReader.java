package io.sitoolkit.cv.core.domain.project.maven;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import io.sitoolkit.cv.core.app.config.ServiceFactory;
import io.sitoolkit.cv.core.domain.project.Project;
import io.sitoolkit.cv.core.domain.project.ProjectReader;
import io.sitoolkit.cv.core.domain.project.analyze.SqlLogProcessor;
import io.sitoolkit.cv.core.infra.config.SitCvConfig;
import io.sitoolkit.cv.core.infra.config.SitCvConfigReader;
import io.sitoolkit.cv.core.infra.project.SitCvToolsManager;
import io.sitoolkit.util.buildtoolhelper.maven.MavenProject;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MavenProjectReader implements ProjectReader {

    @NonNull
    private SqlLogProcessor sqlLogProcessor;

    public static void main(String[] args) {
        Path projectDir = Paths.get(args[0]);
        SitCvConfigReader configReader = new SitCvConfigReader();
        SitCvConfig config = configReader.read(projectDir);
        ServiceFactory factory = ServiceFactory.create(projectDir);
        Project project = factory.getProjectManager().getCurrentProject();
        new MavenProjectReader(new SqlLogProcessor()).generateSqlLog(project, config);
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

        SitCvToolsManager.initialize(project.getWorkDir());
        Path agentJar = SitCvToolsManager.getInstance().getJarPath();

        sqlLogProcessor.process("maven", sitCvConfig, agentJar, project, (String agentParam) -> {
            return mvnPrj.mvnw("test", "-DargLine=" + agentParam);
        });
        return true;
    }

}
