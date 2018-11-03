package io.sitoolkit.cv.core.domain.uml;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.sitoolkit.cv.core.domain.classdef.ClassDef;
import io.sitoolkit.cv.core.domain.classdef.FieldDef;
import io.sitoolkit.cv.core.domain.classdef.MethodCallStack;
import io.sitoolkit.cv.core.domain.classdef.MethodDef;
import io.sitoolkit.cv.core.domain.classdef.RelationDef;
import io.sitoolkit.cv.core.domain.classdef.TypeDef;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ClassDiagramProcessor {

    ImplementDetector implementDetector = new ImplementDetector();

    public ClassDiagram process(MethodDef entryPoint) {
        return process(entryPoint.getQualifiedSignature(),
                getSequenceMethodsRecursively(entryPoint).collect(Collectors.toSet()));
    }

    public ClassDiagram process(LifeLineDef lifeLine) {
        return process(lifeLine.getEntryMessage(),
                getSequenceMethodsRecursively(lifeLine).collect(Collectors.toSet()));
    }

    public ClassDiagram process(String signature, Set<MethodDef> sequenceMethods) {

        Set<ClassDef> pickedClasses = pickClasses(sequenceMethods);

        return process(signature + "(classDiagram)", pickedClasses,
                relation -> pickedClasses.contains(relation.getOther()));
    }

    public ClassDiagram process(String id, Set<ClassDef> classes,
            Predicate<RelationDef> relationFilter) {

        Set<RelationDef> relations = classes.stream().flatMap(this::getRelations)
                .filter(relationFilter).collect(Collectors.toSet());

        return ClassDiagram.builder().id(id).classes(classes).relations(relations).build();
    }

    private Stream<RelationDef> getRelations(ClassDef clazz) {
        Stream<RelationDef> instanceRels = clazz.getFields().stream()
                .map(field -> getInstanceRelation(clazz, field)).filter(Optional::isPresent)
                .map(Optional::get);

        Stream<RelationDef> classRels = getClassRelation(clazz).stream();
        Stream<RelationDef> dependencies = clazz.getMethods().stream()
                .flatMap(this::getDependencies);
        return Stream.of(instanceRels, classRels, dependencies).flatMap(Function.identity())
                .distinct();
    }

    private Set<ClassDef> pickClasses(Set<MethodDef> sequenceMethods) {

        Set<ClassDef> paramClasses = sequenceMethods.stream()
                .map(MethodDef::getParamTypes)
                .flatMap(List::stream)
                .flatMap(TypeDef::getTypeParamsRecursively)
                .map(TypeDef::getClassRef)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        paramClasses.forEach(c -> log.debug("Param class picked :{}", c.getName()));

        Set<ClassDef> resultClasses = sequenceMethods.stream()
                .map(MethodDef::getReturnType)
                .flatMap(TypeDef::getTypeParamsRecursively)
                .map(TypeDef::getClassRef)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        resultClasses.forEach(c -> log.debug("Result class picked :{}", c.getName()));

        Set<ClassDef> ret = Stream.of(paramClasses, resultClasses)
                .flatMap(Set::stream)
                .flatMap(this::getFieldClassesRecursively)
                .distinct()
                .collect(Collectors.toSet());

        ret.forEach(c -> log.debug("Field class picked :{}", c.getName()));

        return ret;
    }

    private Stream<MethodDef> getSequenceMethodsRecursively(MethodDef entryPoint) {
        return getSequenceMethodsRecursively(entryPoint, MethodCallStack.getBlank());
    }

    private Stream<MethodDef> getSequenceMethodsRecursively(MethodDef entryPoint, MethodCallStack callStack) {
        MethodDef methodImpl = implementDetector.detectImplMethod(entryPoint);
        if (callStack.contains(methodImpl)) {
            log.debug("method: {} is called recursively", methodImpl.getQualifiedSignature());
            return Stream.empty();
        }
        MethodCallStack pushedStack = callStack.push(methodImpl);
        return Stream.concat(Stream.of(methodImpl),
                methodImpl.getMethodCalls().stream()
                .flatMap(method -> getSequenceMethodsRecursively(method, pushedStack)));
    }

    private Stream<MethodDef> getSequenceMethodsRecursively(LifeLineDef lifeLine) {
        return lifeLine.getElements().stream()
                .filter(MessageDef.class::isInstance)
                .map(MessageDef.class::cast)
                .flatMap(message -> {
                    return Stream.concat(getSequenceMethodsRecursively(message.getTarget()),
                            Stream.of(message.getMethodCall()));
                });
    }

    Stream<ClassDef> getFieldClassesRecursively(ClassDef classDef) {
        return getFieldClassesRecursively(classDef, new HashSet<>());
    }

    Stream<ClassDef> getFieldClassesRecursively(ClassDef classDef, Set<ClassDef> visited) {
        visited.add(classDef);
        return Stream.concat(Stream.of(classDef),
                classDef.getFields().stream()
                        .map(FieldDef::getType)
                        .flatMap(TypeDef::getTypeParamsRecursively)
                        .map(TypeDef::getClassRef)
                        .filter(Objects::nonNull)
                        .filter(field -> !visited.contains(field))
                        .flatMap(field -> getFieldClassesRecursively(field, visited)))
                .distinct();
    }

    private Stream<RelationDef> getDependencies(MethodDef method) {
        return method.getMethodCalls().stream()
                .map(call -> getDependency(method, call))
                .filter(rel -> !rel.getSelf().equals(rel.getOther()));
    }

    private RelationDef getDependency(MethodDef method, MethodDef call) {
        return RelationDef.builder().self(method.getClassDef()).other(call.getClassDef())
                .type(RelationType.DEPENDENCY).description("use").build();
    }

    private Optional<RelationDef> getInstanceRelation(ClassDef clazz, FieldDef field) {

        TypeWithCardinality c = getTypeWithCardinality(field.getType());
        RelationDef relation = RelationDef.builder()
                .self(clazz)
                .other(c.getType().getClassRef())
                .otherCardinality(c.getCardinality())
                .type(RelationType.OWNERSHIP)
                .description("")
                .build();

        return Optional.ofNullable(relation);
    }

    TypeWithCardinality getTypeWithCardinality(TypeDef type){

        if (isCollection(type) && type.getTypeParamList().size() == 1) {
            return new TypeWithCardinality(type.getTypeParamList().get(0), "0..*");

        } else if (isOptional(type) && type.getTypeParamList().size() == 1) {
            return new TypeWithCardinality(type.getTypeParamList().get(0), "0..1");

        } else {
            return new TypeWithCardinality(type, "1");
        }
    }

    boolean isCollection(TypeDef type) {
        return Arrays.asList(
                "java.util.Set",
                "java.util.List",
                "java.util.Collection").contains(type.getName());
    }

    boolean isOptional(TypeDef type) {
        return Arrays.asList(
                "java.util.Optional").contains(type.getName());
    }


    private Set<RelationDef> getClassRelation(ClassDef clazz) {
        // TODO is-a 関係の抽出
        return Collections.emptySet();
    }


    @Value
    class TypeWithCardinality{
        TypeDef type;
        String cardinality;
    }
}
