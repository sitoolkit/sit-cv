package org.sitoolkit.cv.core.domain.classdef;

import java.util.Collection;
import java.util.Set;

public interface ClassDefRepository {

    void save(ClassDef classDef);

    Collection<ClassDef> getAllClassDefs();

    int countClassDefs();

    void solveMethodCalls();

    void solveClassRefs();

    void solveMethodCalls(ClassDef classDef);

    Set<String> getEntryPoints();

    ClassDef findClassByQualifiedName(String qalifiedName);

    MethodDef findMethodByQualifiedSignature(String qualifiedSignature);

}
