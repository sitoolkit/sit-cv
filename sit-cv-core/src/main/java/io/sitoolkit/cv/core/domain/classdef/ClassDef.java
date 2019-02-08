package io.sitoolkit.cv.core.domain.classdef;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
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

    public ClassDef findImplementation() {
        if (!isInterface()) {
            return this;
        }

        Set<ClassDef> knownImplClasses = getKnownImplClasses();
        log.debug("Interface {} has KnownImplements : {}", getName(), knownImplClasses);

        if (knownImplClasses.size() == 1) {
            ClassDef onlyImpl = knownImplClasses.iterator().next();
            log.debug("{}'s the only known impl found : {} ", getName(), onlyImpl.getName());
            return onlyImpl;
        }

        return this;
    }

    public Optional<MethodDef> findMethodBySignature(String signature) {
        log.debug("Finding '{}' from '{}'", signature, getName());
        Optional<MethodDef> foundMethod = getMethods().stream()
                .filter(m -> StringUtils.equals(signature, m.getSignature())).findFirst();

        if (foundMethod.isPresent()) {
            log.debug("Method found : {}", foundMethod.get().getQualifiedSignature());
        } else {
            log.debug("'{}' not found from '{}'", signature, getName());
        }
        return foundMethod;
    }
}
