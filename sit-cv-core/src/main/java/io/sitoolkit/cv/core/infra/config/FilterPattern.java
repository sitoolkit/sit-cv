package io.sitoolkit.cv.core.infra.config;

import java.util.regex.Pattern;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FilterPattern {

    @NonNull
    private String patternString;
    private Pattern pattern;

    public boolean match(String value) {
        if (pattern == null) {
            pattern = Pattern.compile(patternString);
        }

        return pattern.matcher(value).matches();
    }

}
