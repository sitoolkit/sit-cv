package io.sitoolkit.cv.core.domain.project;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import io.sitoolkit.cv.core.domain.classdef.javaparser.preprocess.DelombokProcessor;
import io.sitoolkit.cv.core.domain.classdef.javaparser.preprocess.PreProcessor;
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
            currentProject.getProjectsIncludeSubs()
                    .forEach(proj -> {
                        PreProcessor pp = DelombokProcessor.of(proj).orElse(PreProcessor.DO_NOTHING);
                        proj.setPreProcessor(pp);
                    });

        } else {
            throw new IllegalArgumentException("Project is not supported " + projectDir);
        }

    }

}
