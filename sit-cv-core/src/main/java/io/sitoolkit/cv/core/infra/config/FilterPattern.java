package io.sitoolkit.cv.core.infra.config;

import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

public class FilterPattern {

    private Pattern pattern;

    public FilterPattern(String patternString) {
        if (!StringUtils.isEmpty(patternString)) {
            pattern = Pattern.compile(patternString);
        }
    }

    public boolean match(String value) {
        if (pattern == null) {
            return false;
        }

        return pattern.matcher(value).matches();
    }

}
