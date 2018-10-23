package io.sitoolkit.cv.core.domain.project.maven;

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
    private Set<String> javaSrcDirs = new HashSet<>();
    @Getter
    private Set<String> classpaths = new HashSet<>();

    @Override
    public void nextLine(String line) {

        log.debug(line);

        String javaSrcDirStr = StringUtils.substringBetween(line,
                "[DEBUG]   (f) compileSourceRoots = [", "]");
        if (StringUtils.isNotEmpty(javaSrcDirStr)) {
            javaSrcDirs.addAll(splitAndTrim(javaSrcDirStr));
        }

        String classpathStr = StringUtils.substringBetween(line,
                "[DEBUG]   (f) classpathElements = [", "]");
        if (StringUtils.isNotEmpty(classpathStr)) {
            classpaths.addAll(splitAndTrim(classpathStr));
        }

    }

    private Set<String> splitAndTrim(String line) {
        return Arrays.asList(line.split(",")).stream().map(String::trim)
                .collect(Collectors.toSet());
    }
}
