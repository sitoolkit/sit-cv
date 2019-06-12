package io.sitoolkit.cv.core.infra.config;

import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import lombok.Value;

@Value
public class FilterPattern {

    private boolean empty;
    private Pattern pattern;

    public FilterPattern(String patternString) {
        empty = StringUtils.isEmpty(patternString);
        if (empty) {
            pattern = null;
        } else {
            pattern = Pattern.compile(patternString);
        }
    }

    public boolean match(String value) {
        if (isEmpty()) {
            return false;
        }

        return pattern.matcher(value).matches();
    }

}
