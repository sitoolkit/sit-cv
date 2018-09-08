package org.sitoolkit.cv.core.domain.uml;

import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.sitoolkit.cv.core.domain.classdef.ClassDef;
import org.sitoolkit.cv.core.domain.classdef.FieldDef;
import org.sitoolkit.cv.core.domain.classdef.MethodCallDef;
import org.sitoolkit.cv.core.domain.classdef.MethodDef;
import org.sitoolkit.cv.core.domain.classdef.RelationDef;

public class ClassDiagramProcessor {

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
        return entryPoint.getMethodCallsRecursively().map(MethodDef::getClassDef)
                .filter(Objects::nonNull).collect(Collectors.toSet());
    }

    private Stream<RelationDef> getDependencies(MethodDef method) {
        return method.getMethodCalls().stream().map(call -> getDependency(method, call));
    }

    private RelationDef getDependency(MethodDef method, MethodCallDef call) {
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
