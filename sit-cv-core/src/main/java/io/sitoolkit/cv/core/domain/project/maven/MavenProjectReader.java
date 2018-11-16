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

        if (!mvnPrj.available()) {
            return Optional.empty();
        }

        MavenProjectInfoListener listener = new MavenProjectInfoListener();

        mvnPrj.mvnw("compile", "-X").stdout(listener).execute();

        Project project = new Project(projectDir);

        for (Project listenedProj : listener.getProjects()) {

            if (listenedProj.getDir().equals(projectDir)) {
                project.setClasspaths(listenedProj.getClasspaths());
                project.setSrcDirs(listenedProj.getSrcDirs());
                project.setBuildDir(listenedProj.getBuildDir());

            } else {
                project.getSubProjects().add(listenedProj);
            }
        }

        return Optional.of(project);
    }

}
