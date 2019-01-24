package io.sitoolkit.cv.core.domain.classdef;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;

import io.sitoolkit.cv.core.infra.config.FilterConditionGroup;
import io.sitoolkit.cv.core.infra.config.SitCvConfig;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class ClassDefRepositoryMemImpl implements ClassDefRepository {

    private Map<String, ClassDef> classDefMap = new HashMap<>();

    private Map<String, MethodDef> methodDefMap = new HashMap<>();

    private Map<String, List<CvStatement>> statementMap = new HashMap<>();

    @NonNull
    private SitCvConfig config;

    @Override
    public void save(ClassDef classDef) {
        classDefMap.put(classDef.getFullyQualifiedName(), classDef);

        classDef.getMethods().stream().forEach(methodDef -> {
            methodDefMap.put(methodDef.getQualifiedSignature(), methodDef);
            statementMap.put(methodDef.getQualifiedSignature(), methodDef.getStatements());
        });
    }

    @Override
    public void remove(String sourceId) {

        Optional<ClassDef> removingClass = classDefMap.values().stream()
                .filter(clazz -> StringUtils.equals(clazz.getSourceId(), sourceId)).findFirst();

        removingClass.ifPresent(classDef -> {
            classDefMap.remove(classDef.getFullyQualifiedName());
            classDef.getMethods().stream().forEach(methodDef -> {
                methodDefMap.remove(methodDef.getQualifiedSignature());
                statementMap.remove(methodDef.getQualifiedSignature());
            });
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
    public void solveReferences() {
        solveMethodCalls();
        solveClassRefs();
    }

    public void solveMethodCalls() {
        classDefMap.values().stream().forEach(this::solveMethodCalls);
    }

    public void solveMethodCalls(ClassDef classDef) {
        classDef.getMethods().stream().forEach(methodDef -> {
            solveMethodType(methodDef);
            methodDef.getStatements().stream().forEach(this::solveMethodCall);
        });

    }

    private void solveMethodCall(CvStatement statement) {
        statement.getMethodCallsRecursively().forEach(this::solveMethodCall);
    }

    private void solveMethodCall(MethodCallDef methodCall) {
        solveMethodType(methodCall);
        soleveMethodCallClass(methodCall);

        if (methodCall.getStatements().isEmpty()) {
            List<CvStatement> statements = statementMap
                    .get(methodCall.getQualifiedSignature());
            if (statements != null) {
                methodCall.setStatements(statements);
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
    public List<String> getEntryPoints() {
        return getFilteredEntryPointClasses().map(ClassDef::getMethods).flatMap(List::stream)
                .map(MethodDef::getQualifiedSignature).sorted().collect(Collectors.toList());
    }

    @Override
    public List<ClassDef> getAllEntryPointClasses() {
        return getFilteredEntryPointClasses().collect(Collectors.toList());
    }

    private Stream<ClassDef> getFilteredEntryPointClasses() {
        FilterConditionGroup entryPointFilter = config.getEntryPointFilter();
        return getAllClassDefs().stream()
                .filter(classDef -> ClassDefFilter.match(classDef, entryPointFilter));
    }

    @Override
    public MethodDef findMethodByQualifiedSignature(String qualifiedSignature) {
        return methodDefMap.get(qualifiedSignature);
    }

    @Override
    public ClassDef findClassByQualifiedName(String qualifiedName) {
        return classDefMap.get(qualifiedName);
    }

    public void solveClassRefs() {
        classDefMap.values().stream().forEach(this::solveClassRefs);
    }

    void solveClassRefs(ClassDef clazz) {
        log.debug("solving class {}", clazz.getName());
        clazz.getFields().stream().map(FieldDef::getType).forEach(this::solveClassRef);

        if (clazz.isClass()) {
            clazz.getImplInterfaces().stream().forEach(ifName -> {
                ClassDef refType = classDefMap.get(ifName);
                if (refType != null) {
                    refType.getKnownImplClasses().remove(clazz); // remove to
                                                                 // replace
                                                                 // classdef to
                                                                 // newer
                    refType.getKnownImplClasses().add(clazz);
                    log.debug("{} is Known implementation of {}", clazz.getName(), ifName);
                }

            });
        }
    }
}
