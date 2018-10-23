package io.sitoolkit.cv.core.domain.project.maven;

import java.nio.file.Path;
import java.util.Optional;

import io.sitoolkit.cv.core.domain.project.Project;
import io.sitoolkit.cv.core.domain.project.ProjectReader;
import io.sitoolkit.util.buidtoolhelper.maven.MavenProject;

public class MavenProjectReader implements ProjectReader {

    @Override
    public Optional<Project> read(Path projectDir) {

        MavenProject mvnPrj = MavenProject.load(projectDir);

        if (mvnPrj == null) {
            return Optional.empty();
        }

        Project project = new Project(projectDir);
        MavenProjectInfoListener listener = new MavenProjectInfoListener();

        mvnPrj.mvnw("compile", "-X").stdout(listener).execute();

        project.setClasspaths(listener.getClasspaths());
        project.setSrcDirs(listener.getJavaSrcDirs());

        return Optional.of(project);
    }

}
