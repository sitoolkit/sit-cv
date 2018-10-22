package io.sitoolkit.cv.core.domain.classdef;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = { "pkg", "name" })
public class ClassDef {

    private String pkg;
    private String name;
    private String sourceId;
    private ClassType type;
    private List<MethodDef> methods = new ArrayList<>();
    private List<FieldDef> fields = new ArrayList<>();
    private Set<String> implInterfaces = new HashSet<>();
    private Set<ClassDef> knownImplClasses = new HashSet<>();
    private Set<String> annotations = new HashSet<>();

    public boolean isInterface() {
        return ClassType.INTERFACE.equals(type);
    }

    public boolean isClass() {
        return ClassType.CLASS.equals(type);
    }

    public String getFullyQualifiedName() {
        return pkg + "." + name;
    }
}
