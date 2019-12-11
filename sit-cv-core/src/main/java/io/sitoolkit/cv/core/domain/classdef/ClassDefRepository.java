package io.sitoolkit.cv.core.domain.classdef;

import java.util.Collection;
import java.util.List;

public interface ClassDefRepository {

  void save(ClassDef classDef);

  void remove(String sourceId);

  Collection<ClassDef> getAllClassDefs();

  int countClassDefs();

  void solveReferences();

  List<String> getEntryPoints();

  List<ClassDef> getAllEntryPointClasses();

  ClassDef findClassByQualifiedName(String qalifiedName);

  MethodDef findMethodByQualifiedSignature(String qualifiedSignature);
}
