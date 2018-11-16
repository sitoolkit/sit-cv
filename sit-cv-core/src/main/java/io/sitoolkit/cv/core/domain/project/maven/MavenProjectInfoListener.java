package io.sitoolkit.cv.core.domain.project.maven;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import io.sitoolkit.cv.core.domain.project.Project;
import io.sitoolkit.util.buidtoolhelper.process.StdoutListener;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MavenProjectInfoListener implements StdoutListener {

    @Getter
    private List<Project> projects = new ArrayList<>();
    private Project currentProject;

    @Override
    public void nextLine(String line) {

        log.debug(line);

        String javaBaseDirStr = StringUtils.substringAfterLast(line,
                "[DEBUG]   (f) basedir = ");

        if (StringUtils.isNotEmpty(javaBaseDirStr)) {
            currentProject = new Project(Paths.get(javaBaseDirStr));
            projects.add(currentProject);
        }

        if (currentProject != null) {

            String javaBuildDirStr = StringUtils.substringAfterLast(line,
                    "[DEBUG]   (f) buildDirectory = ");
            if (StringUtils.isNotEmpty(javaBuildDirStr)) {
                currentProject.setBuildDir(Paths.get(javaBuildDirStr));
            }

            String javaSrcDirStr = StringUtils.substringBetween(line,
                    "[DEBUG]   (f) compileSourceRoots = [", "]");
            if (StringUtils.isNotEmpty(javaSrcDirStr)) {
                currentProject.setSrcDirs(splitAndTrim(javaSrcDirStr, false));
            }

            String classpathStr = StringUtils.substringBetween(line,
                    "[DEBUG]   (f) classpathElements = [", "]");
            if (StringUtils.isNotEmpty(classpathStr)) {
                currentProject.setClasspaths(splitAndTrim(classpathStr, true));
            }
        }
    }

    private Set<Path> splitAndTrim(String line, boolean jarOnly) {
        return Arrays.asList(line.split(",")).stream().map(String::trim)
                .filter(element -> jarOnly ? element.endsWith(".jar") : true).map(Paths::get)
                .collect(Collectors.toSet());
    }
}
