package io.sitoolkit.cv.core.domain.project.maven;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import io.sitoolkit.util.buidtoolhelper.process.StdoutListener;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MavenProjectInfoListener implements StdoutListener {

    @Getter
    private Set<Path> javaSrcDirs = new HashSet<>();
    @Getter
    private Set<Path> classpaths = new HashSet<>();

    @Override
    public void nextLine(String line) {

        log.debug(line);

        String javaSrcDirStr = StringUtils.substringBetween(line,
                "[DEBUG]   (f) compileSourceRoots = [", "]");
        if (StringUtils.isNotEmpty(javaSrcDirStr)) {
            javaSrcDirs.addAll(splitAndTrim(javaSrcDirStr, false));
        }

        String classpathStr = StringUtils.substringBetween(line,
                "[DEBUG]   (f) classpathElements = [", "]");
        if (StringUtils.isNotEmpty(classpathStr)) {
            classpaths.addAll(splitAndTrim(classpathStr, true));
        }

    }

    private Set<Path> splitAndTrim(String line, boolean jarOnly) {
        return Arrays.asList(line.split(",")).stream().map(String::trim)
                .filter(element -> jarOnly ? element.endsWith(".jar") : true).map(Paths::get)
                .collect(Collectors.toSet());
    }
}
