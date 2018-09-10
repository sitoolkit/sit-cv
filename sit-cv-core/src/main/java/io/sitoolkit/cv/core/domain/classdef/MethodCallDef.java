package io.sitoolkit.cv.core.domain.classdef;

import lombok.Data;

@Data
public class MethodCallDef extends MethodDef {
    private String className;
    private String packageName;
}
