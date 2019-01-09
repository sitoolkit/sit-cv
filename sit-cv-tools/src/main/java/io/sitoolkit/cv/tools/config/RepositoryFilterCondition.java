package io.sitoolkit.cv.tools.config;

import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

public class RepositoryFilterCondition {

    private String annotation;
    private Pattern annotationPattern;

    public RepositoryFilterCondition(String annotation) {
        this.annotation = annotation;
    }

    public boolean matchAnnotation(String annotation) {
        if (StringUtils.isEmpty(this.annotation)) {
            return true;
        }

        if (annotationPattern == null) {
            annotationPattern = Pattern.compile(this.annotation);
        }

        return annotationPattern.matcher(annotation).matches();
    }
}
