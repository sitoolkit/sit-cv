package io.sitoolkit.cv.core.domain.classdef.filter;

import java.util.function.Predicate;

import io.sitoolkit.cv.core.domain.classdef.ClassDef;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ClassDefFilter implements Predicate<ClassDef> {

    @Setter
    ClassDefFilterCondition condition;

    @Override
    public boolean test(ClassDef type) {
        if (condition == null) {
            return true;

        } else {
            return condition.getTypes().stream().anyMatch(pattern -> test(type, pattern));
        }
    }

    boolean test(ClassDef type, String typePattern) {
        boolean result = test(type.getPkg() + "." + type.getName(), typePattern);
        if (result) {
            log.debug("{}.{} matched to pattern '{}'", type.getPkg(), type.getName(), typePattern);
        }
        return result;
    }

    boolean test(String targetType, String typePattern) {
        return targetType.matches(toRegex(typePattern));
    }

    String toRegex(String typePattern) {
        return typePattern
                .replace(".", "\\.")
                .replace("*", "[^.]*")
                .replace("\\.\\.", "\\.(.*\\.)?");
    }
}
