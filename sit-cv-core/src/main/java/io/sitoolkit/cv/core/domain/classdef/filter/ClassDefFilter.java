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
        return testType(type) || testAnnotation(type);
    }

    boolean testType(ClassDef type) {
        if (condition == null) {
            return true;
        } else {
            return condition.getTypes().stream().anyMatch(pattern -> testType(type, pattern));
        }
    }

    boolean testType(ClassDef type, String typePattern) {
        boolean result = testType(type.getPkg() + "." + type.getName(), typePattern);
        if (result) {
            log.debug("{}.{} matched to pattern '{}'", type.getPkg(), type.getName(), typePattern);
        }
        return result;
    }

    boolean testType(String targetType, String typePattern) {
        return targetType.matches(toRegex(typePattern));
    }

    String toRegex(String typePattern) {
        return typePattern
                .replace(".", "\\.")
                .replace("*", "[^.]*")
                .replace("\\.\\.", "\\.(.*\\.)?");
    }
    

    boolean testAnnotation(ClassDef type) {
        if (condition == null) {
            return true;
        } else {
            return condition.getAnnotations().stream().anyMatch(annotation -> testAnnotation(type, annotation));
        }
    }

    boolean testAnnotation(ClassDef type, String annotation) {
        boolean result = type.getAnnotations().contains(annotation);
        if (result) {
            log.debug("{}.{} matched to Annotation '{}'", type.getPkg(), type.getName(), annotation);
        }
        return result;
    }

}
