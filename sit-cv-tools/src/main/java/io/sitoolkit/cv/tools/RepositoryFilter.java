package io.sitoolkit.cv.tools;

import java.util.Arrays;

import io.sitoolkit.cv.tools.config.FilterCondition;
import io.sitoolkit.cv.tools.config.FilterConditionGroup;
import javassist.CtClass;

public class RepositoryFilter {

    public static boolean match(CtClass ctClass, FilterConditionGroup filterConditions) {

        return filterConditions.getInclude().stream()
                .anyMatch(filterCondition -> matchCondition(ctClass, filterCondition));

    }

    private static boolean matchCondition(CtClass ctClass,
            FilterCondition filterCondition) {
        boolean matchClassName = filterCondition.matchName(ctClass.getName());

        Object[] annotations;
        try {
            annotations = ctClass.getAnnotations();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }

        if (filterCondition.getAnnotationPattern().isEmpty()) {
            return matchClassName;
        }

        boolean matchAnnotation = Arrays.stream(annotations)
                .anyMatch(annotation -> filterCondition.matchAnnotation(annotation.toString()));

        return matchClassName && matchAnnotation;
    }
}
