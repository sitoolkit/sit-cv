package org.sitoolkit.cv.core.domain.classdef;

import java.util.Collection;

import org.apache.commons.lang3.StringUtils;

public class MethodCallWriter {

    public String write(Collection<ClassDef> classDefs) {
        StringBuilder sb = new StringBuilder();

        classDefs.stream().forEach(classDef -> {
            sb.append("\n");
            sb.append(classDef.getName());

            classDef.getMethods().stream().forEach(methodDef -> {
                sb.append(write(methodDef, 1));
            });
        });

        return sb.toString();
    }

    String write(MethodDef methodDef, int indent) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n");
        sb.append(StringUtils.repeat(" ", indent * 2));
        sb.append(methodDef.getSignature());

        methodDef.getMethodCalls().stream().forEach(m -> sb.append(write(m, indent + 1)));

        return sb.toString();
    }
}
