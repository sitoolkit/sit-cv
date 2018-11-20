package io.sitoolkit.cv.core.domain.project.maven;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import io.sitoolkit.cv.core.domain.project.Project;
import io.sitoolkit.util.buildtoolhelper.process.StdoutListener;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MavenProjectInfoListener implements StdoutListener {

    @Getter
    private final Project project;
    private Project recordingProject;

    public MavenProjectInfoListener(Path projectDir) {
        this.project = new Project(projectDir);
    }

    @Override
    public void nextLine(String line) {

        log.debug(line);

        String javaBaseDirStr = StringUtils.substringAfterLast(line,
                "[DEBUG]   (f) basedir = ");
        if (StringUtils.isNotEmpty(javaBaseDirStr)) {
            recordBaseDirStr(javaBaseDirStr);
        }

        String javaBuildDirStr = StringUtils.substringAfterLast(line,
                "[DEBUG]   (f) buildDirectory = ");
        if (StringUtils.isNotEmpty(javaBuildDirStr)) {
            recordBuildDirStr(javaBuildDirStr);
        }

        String javaSrcDirsStr = StringUtils.substringBetween(line,
                "[DEBUG]   (f) compileSourceRoots = [", "]");
        if (StringUtils.isNotEmpty(javaSrcDirsStr)) {
            recordSrcDirsStr(javaSrcDirsStr);
        }

        String classpathsStr = StringUtils.substringBetween(line,
                "[DEBUG]   (f) classpathElements = [", "]");
        if (StringUtils.isNotEmpty(classpathsStr)) {
            recordClasspathsStr(classpathsStr);
        }
    }

    void recordBaseDirStr(String javaBaseDirStr) {
        Path javaBaseDir = Paths.get(javaBaseDirStr);
        if (project.getDir().equals(javaBaseDir)) {
            recordingProject = project;

        } else {
            recordingProject = new Project(javaBaseDir);
            project.getSubProjects().add(recordingProject);
        }
    }

    void recordBuildDirStr(String javaBuildDirStr) {
        if (recordingProject != null) {
            recordingProject.setBuildDir(Paths.get(javaBuildDirStr));
        }
    }

    void recordSrcDirsStr(String javaSrcDirsStr) {
        if (recordingProject != null) {
            recordingProject.setSrcDirs(splitAndTrim(javaSrcDirsStr, false));
        }
    }

    void recordClasspathsStr(String classpathsStr) {
        if (recordingProject != null) {
            recordingProject.setClasspaths(splitAndTrim(classpathsStr, true));
        }
    }

    private Set<Path> splitAndTrim(String line, boolean jarOnly) {
        return Arrays.asList(line.split(",")).stream().map(String::trim)
                .filter(element -> jarOnly ? element.endsWith(".jar") : true).map(Paths::get)
                .collect(Collectors.toSet());
    }
}
