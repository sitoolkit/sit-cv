package io.sitoolkit.cv.core.domain.classdef;

import org.apache.commons.lang3.StringUtils;

import io.sitoolkit.cv.core.infra.config.FilterConditionGroup;

public class ClassDefFilter {

    public static boolean match(ClassDef clazz, FilterConditionGroup filterConditions) {

        return filterConditions.getInclude().stream().anyMatch(filterCondition -> {

            boolean matchClassName = filterCondition.matchName(clazz.getFullyQualifiedName());

            if (StringUtils.isEmpty(filterCondition.getAnnotation())) {
                return matchClassName;
            }

            boolean matchAnnotation = clazz.getAnnotations().stream()
                    .anyMatch(annotation -> filterCondition.matchAnnotation(annotation));

            return matchClassName && matchAnnotation;
        });

    }

}
