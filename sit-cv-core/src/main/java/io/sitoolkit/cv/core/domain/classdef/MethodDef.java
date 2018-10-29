package io.sitoolkit.cv.core.domain.classdef;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString(exclude = { "classDef", "methodCalls" })
@EqualsAndHashCode(of = "qualifiedSignature")
public class MethodDef {

    private String name;
    private String signature;
    private String qualifiedSignature;
    private boolean isPublic;
    private String actionPath;
    private ClassDef classDef;
    private List<TypeDef> paramTypes;
    private TypeDef returnType;
    private List<MethodCallDef> methodCalls = new ArrayList<>();
    private String comment = "";

}
