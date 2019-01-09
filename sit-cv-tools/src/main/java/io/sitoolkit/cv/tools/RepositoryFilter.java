package io.sitoolkit.cv.tools;

import java.util.List;
import java.util.stream.Stream;

import io.sitoolkit.cv.tools.config.RepositoryFilterCondition;

public class RepositoryFilter {

    public static boolean match(Object[] annotations,
            List<RepositoryFilterCondition> filterConditions) {
        return Stream.of(annotations).map(Object::toString)
                .filter((a) -> filterConditions.stream()
                        .anyMatch((condition) -> condition.matchAnnotation(a)))
                .findAny().isPresent();
    }
}
