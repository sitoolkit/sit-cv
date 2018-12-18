package io.sitoolkit.cv.core.domain.classdef;

import org.apache.commons.lang3.StringUtils;

import io.sitoolkit.cv.core.infra.config.FilterCondition;
import io.sitoolkit.cv.core.infra.config.FilterConditionGroup;

public class ClassDefFilter {

    public static boolean match(ClassDef clazz, FilterConditionGroup filterConditions) {

        return filterConditions.getInclude().stream()
                .anyMatch(filterCondition -> matchCondition(clazz, filterCondition));

    }

    public static boolean needsDetail(ClassDef clazz, FilterConditionGroup filterConditions) {

        return filterConditions.getInclude().stream()
                .filter(filterCondition -> matchCondition(clazz, filterCondition))
                .filter(FilterCondition::isWithDetail).findAny().isPresent();

    }

    private static boolean matchCondition(ClassDef clazz, FilterCondition filterCondition) {
        boolean matchClassName = filterCondition.matchName(clazz.getFullyQualifiedName());

        if (StringUtils.isEmpty(filterCondition.getAnnotation())) {
            return matchClassName;
        }

        boolean matchAnnotation = clazz.getAnnotations().stream()
                .anyMatch(annotation -> filterCondition.matchAnnotation(annotation));

        return matchClassName && matchAnnotation;
    }

}
