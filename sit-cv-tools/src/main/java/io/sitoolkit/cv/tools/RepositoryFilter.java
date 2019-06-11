package io.sitoolkit.cv.tools;

import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;

import io.sitoolkit.cv.tools.config.RepositoryFilterCondition;
import io.sitoolkit.cv.tools.config.RepositoryFilterConditionGroup;
import javassist.CtClass;

public class RepositoryFilter {

    public static boolean match(CtClass ctClass, RepositoryFilterConditionGroup filterConditions) {

        return filterConditions.getInclude().stream()
                .anyMatch(filterCondition -> matchCondition(ctClass, filterCondition));

    }

    private static boolean matchCondition(CtClass ctClass,
            RepositoryFilterCondition filterCondition) {
        boolean matchClassName = filterCondition.matchName(ctClass.getName());

        Object[] annotations;
        try {
            annotations = ctClass.getAnnotations();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }

        if (StringUtils.isEmpty(filterCondition.getAnnotation())) {
            return matchClassName;
        }

        boolean matchAnnotation = Arrays.stream(annotations)
                .anyMatch(annotation -> filterCondition.matchAnnotation(annotation.toString()));

        return matchClassName && matchAnnotation;
    }
}
