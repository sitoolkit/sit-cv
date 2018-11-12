package io.sitoolkit.cv.core.domain.project;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import io.sitoolkit.cv.core.domain.project.gradle.GradleProjectReader;
import io.sitoolkit.cv.core.domain.project.maven.MavenProjectReader;
import lombok.Getter;

public class ProjectManager {

    private List<ProjectReader> readers = Arrays.asList(new MavenProjectReader(),
            new GradleProjectReader());

    @Getter
    private Project currentProject;

    public void load(Path projectDir) {

        Optional<Project> project = readers.stream().map(reader -> reader.read(projectDir))
                .filter(Optional::isPresent).map(Optional::get).findFirst();

        if (project.isPresent()) {
            currentProject = project.get();
        } else {
            throw new IllegalArgumentException("Project is not supported " + projectDir);
        }

    }

}
