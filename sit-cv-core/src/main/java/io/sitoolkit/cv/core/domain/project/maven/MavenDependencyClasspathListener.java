package io.sitoolkit.cv.core.domain.project.maven;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.sitoolkit.util.buildtoolhelper.process.StdoutListener;
import lombok.Getter;

public class MavenDependencyClasspathListener implements StdoutListener {

    @Getter
    private String classpath;
    private Pattern classpathPattern;

    public MavenDependencyClasspathListener(String artifactId) {
        this.classpathPattern = Pattern.compile("(^|.*;)([^;]+" + artifactId + ".*?\\.jar)");
    }

    @Override
    public void nextLine(String line) {
        if (classpath == null) {
            Matcher matcher = classpathPattern.matcher(line);
            if (matcher.find()) {
                classpath = matcher.group(2);
            }
        }
    }

}
