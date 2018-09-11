package io.sitoolkit.cv.core.domain.classdef;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ClassDefRepositoryMemImpl implements ClassDefRepository {

    private Map<String, ClassDef> classDefMap = new HashMap<>();

    private Map<String, MethodDef> methodDefMap = new HashMap<>();

    private Map<String, Set<MethodCallDef>> methodCallMap = new HashMap<>();

    @Override
    public void save(ClassDef classDef) {
        classDefMap.put(classDef.getPkg() + "." + classDef.getName(), classDef);

        classDef.getMethods().stream().forEach(methodDef -> {
            methodDefMap.put(methodDef.getQualifiedSignature(), methodDef);
            methodCallMap.put(methodDef.getQualifiedSignature(), methodDef.getMethodCalls());
        });
    }

    @Override
    public Collection<ClassDef> getAllClassDefs() {
        return Collections.unmodifiableCollection(classDefMap.values());
    }

    @Override
    public int countClassDefs() {
        return classDefMap.size();
    }

    @Override
    public void solveMethodCalls() {
        classDefMap.values().stream().forEach(this::solveMethodCalls);
    }

    @Override
    public void solveMethodCalls(ClassDef classDef) {
        classDef.getMethods().stream().forEach(methodDef -> {
            solveMethodType(methodDef);
            methodDef.getMethodCalls().stream().forEach(methodCall -> {
                solveMethodCall(methodCall);
            });
        });

    }

    private void solveMethodCall(MethodCallDef methodCall) {
        solveMethodType(methodCall);
        soleveMethodCallClass(methodCall);

        if (methodCall.getMethodCalls().isEmpty()) {
            Set<MethodCallDef> calledMethods = methodCallMap
                    .get(methodCall.getQualifiedSignature());
            if (calledMethods != null) {
                methodCall.setMethodCalls(calledMethods);
                calledMethods.stream().forEach(calledMethod -> {
                    soleveMethodCallClass(calledMethod);
                });
            }
        }
    }

    private void solveMethodType(MethodDef methodDef) {
        methodDef.getParamTypes().forEach(this::solveClassRef);
        solveClassRef(methodDef.getReturnType());
    }

    private void solveClassRef(TypeDef type) {
        type.getTypeParamsRecursively().forEach(t -> {
            ClassDef refType = classDefMap.get(t.getName());
            if (refType != null) {
                t.setClassRef(refType);
            }
        });
    }

    private void soleveMethodCallClass(MethodCallDef calledMethod) {
        ClassDef calledMethodClass = classDefMap
                .get(calledMethod.getPackageName() + "." + calledMethod.getClassName());
        calledMethod.setClassDef(calledMethodClass);
    }

    @Override
    public Set<String> getEntryPoints() {
        return getAllClassDefs().stream().filter(clazz -> clazz.getName().endsWith("Controller"))
                .map(ClassDef::getMethods).flatMap(List::stream)
                .map(MethodDef::getQualifiedSignature).collect(Collectors.toSet());
    }

    @Override
    public MethodDef findMethodByQualifiedSignature(String qualifiedSignature) {
        return methodDefMap.get(qualifiedSignature);
    }

    @Override
    public ClassDef findClassByQualifiedName(String qualifiedName) {
        return classDefMap.get(qualifiedName);
    }

    @Override
    public void solveClassRefs() {
        classDefMap.values().stream().forEach(clazz -> clazz.getFields().stream().forEach(field -> {
            ClassDef refType = classDefMap.get(field.getType());
            if (refType != null) {
                field.setTypeRef(refType);
            }
        }));
    }

}
