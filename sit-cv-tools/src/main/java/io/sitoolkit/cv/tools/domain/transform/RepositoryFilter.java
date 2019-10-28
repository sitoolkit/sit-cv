package io.sitoolkit.cv.tools.domain.transform;

import io.sitoolkit.cv.tools.infra.config.FilterCondition;
import io.sitoolkit.cv.tools.infra.config.FilterConditionGroup;
import java.util.Arrays;
import javassist.CtClass;

public class RepositoryFilter {

    public static boolean match(CtClass ctClass, FilterConditionGroup filterConditions) {

        boolean include = filterConditions.getInclude().stream()
            .anyMatch(filterCondition -> matchCondition(ctClass, filterCondition));

        boolean exclude = filterConditions.getExclude().stream()
            .anyMatch(filterCondition -> matchCondition(ctClass, filterCondition));

        return include && !exclude;
    }

    private static boolean matchCondition(CtClass ctClass, FilterCondition filterCondition) {
        boolean matchClassName = filterCondition.matchName(ctClass.getName())
            || filterCondition.matchName(ctClass.getSimpleName());

        if (filterCondition.getAnnotationPattern().isEmpty()) {
            return matchClassName;
        }

        Object[] annotations;
        try {
            annotations = ctClass.getAnnotations();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }

        boolean matchAnnotation = Arrays.stream(annotations)
                .anyMatch(annotation -> filterCondition.matchAnnotation(annotation.toString()));

        return matchClassName && matchAnnotation;
    }

}
