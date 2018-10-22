package io.sitoolkit.cv.core.domain.classdef;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class MethodCallDef extends MethodDef {
    private String className;
    private String packageName;
}
