package io.sitoolkit.cv.core.domain.project.gradle;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import io.sitoolkit.util.buildtoolhelper.process.StdoutListener;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GradleProjectInfoListener implements StdoutListener {

    @Getter
    Set<Path> javaSrcDirs = new HashSet<>();
    @Getter
    Set<Path> classpaths = new HashSet<>();

    @Override
    public void nextLine(String line) {

        log.debug(line);

        String javaSrcDir = StringUtils.substringAfter(line, "sitCvJavaSrcDir:");
        if (StringUtils.isNotEmpty(javaSrcDir)) {
            javaSrcDirs.add(Paths.get(javaSrcDir));
        }

        String classpath = StringUtils.substringAfter(line, "sitCvClasspath:");
        if (StringUtils.isNotEmpty(classpath)) {
            classpaths.add(Paths.get(classpath));
        }
    }

}
