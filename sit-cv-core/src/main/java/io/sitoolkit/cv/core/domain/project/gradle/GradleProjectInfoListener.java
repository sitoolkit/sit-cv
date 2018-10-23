package io.sitoolkit.cv.core.domain.project.gradle;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import io.sitoolkit.util.buidtoolhelper.process.StdoutListener;
import lombok.Getter;

public class GradleProjectInfoListener implements StdoutListener {

    @Getter
    Set<String> javaSrcDirs = new HashSet<>();
    @Getter
    Set<String> classpaths = new HashSet<>();

    @Override
    public void nextLine(String line) {
        String javaSrcDir = StringUtils.substringAfter(line, "sitCvJavaSrcDir:");
        if (StringUtils.isNotEmpty(javaSrcDir)) {
            javaSrcDirs.add(javaSrcDir);
        }

        String classpath = StringUtils.substringAfter(line, "sitCvClasspath:");
        if (StringUtils.isNotEmpty(classpath)) {
            classpaths.add(classpath);
        }
    }

}
