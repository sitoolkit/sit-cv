package io.sitoolkit.cv.core.domain.classdef;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(exclude = "classDef")
public class MethodDef {

    private String name;
    private String signature;
    private String qualifiedSignature;
    private boolean isPublic;
    private String actionPath;
    private ClassDef classDef;
    private Set<MethodCallDef> methodCalls = new HashSet<>();

    public Stream<MethodDef> getMethodCallsRecursively() {
        return Stream.concat(Stream.of(this),
                methodCalls.stream().flatMap(MethodDef::getMethodCallsRecursively));
    }

}
