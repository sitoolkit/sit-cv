package io.sitoolkit.cv.core.domain.project.gradle;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import io.sitoolkit.cv.core.domain.project.Project;
import io.sitoolkit.util.buidtoolhelper.process.StdoutListener;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GradleProjectInfoListener implements StdoutListener {

    @Getter
    private List<Project> projects = new ArrayList<>();
    private Project currentProject;

    @Override
    public void nextLine(String line) {

        log.debug(line);

        String javaBaseDirStr = StringUtils.substringAfter(line, "sitCvProjectDir:");
        if (StringUtils.isNotEmpty(javaBaseDirStr)) {
            currentProject = new Project(Paths.get(javaBaseDirStr));
            projects.add(currentProject);
        }

        if (currentProject != null) {
            String javaBuildDirStr = StringUtils.substringAfter(line, "sitCvBuildDir:");
            if (StringUtils.isNotEmpty(javaBuildDirStr)) {
                currentProject.setBuildDir(Paths.get(javaBuildDirStr));
            }

            String javaSrcDir = StringUtils.substringAfter(line, "sitCvJavaSrcDir:");
            if (StringUtils.isNotEmpty(javaSrcDir)) {
                currentProject.getSrcDirs().add(Paths.get(javaSrcDir));
            }

            String classpath = StringUtils.substringAfter(line, "sitCvClasspath:");
            if (StringUtils.isNotEmpty(classpath)) {
                currentProject.getClasspaths().add(Paths.get(classpath));
            }
        }
    }

}
