package io.sitoolkit.cv.core.infra.config;

import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class FilterCondition {

    private String name;
    private String annotation;
    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    private Pattern namePattern;
    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    private Pattern annotationPattern;

    public void compilePattern() {
        if (StringUtils.isNotEmpty(name)) {
            namePattern = Pattern.compile(name);
        }
        if (StringUtils.isNotEmpty(annotation)) {
            annotationPattern = Pattern.compile(annotation);
        }
    }

    public boolean matchName(String name) {
        return namePattern == null ? true : namePattern.matcher(name).matches();
    }

    public boolean matchAnnotation(String annotation) {
        return annotationPattern == null ? true : annotationPattern.matcher(annotation).matches();
    }
}
