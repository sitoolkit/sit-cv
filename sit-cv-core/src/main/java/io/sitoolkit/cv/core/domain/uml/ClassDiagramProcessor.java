package io.sitoolkit.cv.core.domain.uml;

import java.util.Collections;
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
import io.sitoolkit.cv.core.domain.classdef.MethodDef;
import io.sitoolkit.cv.core.domain.classdef.RelationDef;
import io.sitoolkit.cv.core.domain.classdef.TypeDef;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ClassDiagramProcessor {

    ImplementDetector implementDetector = new ImplementDetector();

    public ClassDiagram process(MethodDef entryPoint) {

        Set<ClassDef> pickedClasses = pickClasses(entryPoint);
        return process(entryPoint.getQualifiedSignature() + "(classDiagram)", pickedClasses,
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

    private Set<ClassDef> pickClasses(MethodDef entryPoint) {

        Set<MethodDef> sequenceMethods = getSequenceMethodsRecursively(entryPoint)
                .collect(Collectors.toSet());

        Set<ClassDef> sequenceClasses = sequenceMethods.stream()
                .map(MethodDef::getClassDef)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        sequenceClasses.forEach(c -> log.debug("Sequence class picked :{}", c.getName()));

        Set<ClassDef> paramClasses = sequenceMethods.stream()
                .map(MethodDef::getParamTypes)
                .flatMap(List::stream)
                .flatMap(TypeDef::getTypeParamsRecursively)
                .map(TypeDef::getClassRef)
                .map(implementDetector::detectImplClass)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        paramClasses.forEach(c -> log.debug("Param class picked :{}", c.getName()));

        Set<ClassDef> resultClasses = sequenceMethods.stream()
                .map(MethodDef::getReturnType)
                .flatMap(TypeDef::getTypeParamsRecursively)
                .map(TypeDef::getClassRef)
                .map(implementDetector::detectImplClass)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        resultClasses.forEach(c -> log.debug("Result class picked :{}", c.getName()));

        Set<ClassDef> fieldClasses = Stream.of(sequenceClasses, paramClasses, resultClasses)
                .flatMap(Set::stream)
                .map(ClassDef::getFields)
                .flatMap(List::stream)
                .map(FieldDef::getTypeRef)
                .map(implementDetector::detectImplClass)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        fieldClasses.forEach(c -> log.debug("Field class picked :{}", c.getName()));

        Set<ClassDef> ret = Stream.of(sequenceClasses, paramClasses, resultClasses, fieldClasses)
                .flatMap(Set::stream)
                .collect(Collectors.toSet());

        return ret;
    }


    private Stream<MethodDef> getSequenceMethodsRecursively(MethodDef entryPoint) {
        MethodDef methodImpl = implementDetector.detectImplMethod(entryPoint);
        return Stream.concat(Stream.of(methodImpl),
                methodImpl.getMethodCalls().stream().flatMap(this::getSequenceMethodsRecursively));
    }

    private Stream<RelationDef> getDependencies(MethodDef method) {
        return method.getMethodCalls().stream()
                .map(implementDetector::detectImplMethod)
                .map(call -> getDependency(method, call))
                .filter(rel -> !rel.getSelf().equals(rel.getOther()));
    }

    private RelationDef getDependency(MethodDef method, MethodDef call) {
        return RelationDef.builder().self(method.getClassDef()).other(call.getClassDef())
                .type(RelationType.DEPENDENCY).description("use").build();
    }

    private Optional<RelationDef> getInstanceRelation(ClassDef clazz, FieldDef field) {
        // TODO has-a, part-of 関係の抽出
        return Optional.empty();
    }

    private Set<RelationDef> getClassRelation(ClassDef clazz) {
        // TODO is-a 関係の抽出
        return Collections.emptySet();
    }

}
