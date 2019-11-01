package io.sitoolkit.cv.core.domain.uml;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static org.apache.commons.lang3.StringUtils.substringAfter;
import static org.apache.commons.lang3.StringUtils.uncapitalize;

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
import io.sitoolkit.cv.core.domain.classdef.MethodDef;
import io.sitoolkit.cv.core.domain.classdef.RelationDef;
import io.sitoolkit.cv.core.domain.classdef.TypeDef;
import io.sitoolkit.cv.core.infra.config.CvConfig;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class ClassDiagramProcessor {

    @NonNull
    private CvConfig config;
    
    public ClassDiagram process(LifeLineDef lifeLine) {
        return process(lifeLine.getEntryMessage().getRequestQualifiedSignature(),
                lifeLine.getSequenceMethodsRecursively().collect(Collectors.toSet()));
    }

    public ClassDiagram process(String signature, Set<MethodDef> sequenceMethods) {

        Set<ClassDef> pickedClasses = pickClasses(sequenceMethods);
        Set<ClassDef> processedClasses = pickedClasses.stream().map(this::processClass).collect(toSet());

        return process(signature + "(classDiagram)", processedClasses,
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
    
    private ClassDef processClass(ClassDef classDef) {
        
        if (!config.isAccessorMethod()) {
            return filterAccessor(classDef);
            
         } else {
            return classDef;
         }
    }

    private ClassDef filterAccessor(ClassDef classDef) {
        List<MethodDef> filteredMethods = classDef.getMethods().stream()
                .filter(method -> !isMethodAccesor(method, classDef))
                .collect(toList());
        ClassDef filtered = newInstance(classDef);
        filtered.setMethods(filteredMethods);
        return filtered;
    }

    private boolean isMethodAccesor(MethodDef method, ClassDef classDef) {
        return findFieldFromSetter(method)
                .or(() -> findFieldFromGetter(method))
                .filter(classDef.getFields()::contains)
                .isPresent();
    }

  private Optional<FieldDef> findFieldFromGetter(MethodDef method) {

    Optional<String> fieldname = findFieldNameFromGetter(method.getName());
    Optional<TypeDef> type = findFieldTypeFromGetter(method);

    if (fieldname.isPresent() && type.isPresent()) {
      return Optional.of(createFieldDef(type.get(), fieldname.get()));
    } else {
      return Optional.empty();
    }
  }
  
  private Optional<String> findFieldNameFromGetter(String name) {
      String prefix = "get";
      if(!name.startsWith(prefix)) {
          return Optional.empty();
      }
      return Optional.ofNullable(uncapitalize(substringAfter(name, prefix)));
  }
  private Optional<TypeDef> findFieldTypeFromGetter(MethodDef method) {
      if (method.getParamTypes() != null && !method.getParamTypes().isEmpty()) {
        return Optional.empty();
      }
      return Optional.ofNullable(method.getReturnType());
    }
  
  private Optional<FieldDef> findFieldFromSetter(MethodDef method){
      Optional<String> fieldname = findFieldNameFromSetter(method.getName());
      Optional<TypeDef> type = findFieldTypeFromSetter(method);

      if (fieldname.isPresent() && type.isPresent()) {
        return Optional.of(createFieldDef(type.get(), fieldname.get()));
      } else {
        return Optional.empty();
      }
    }

  private Optional<String> findFieldNameFromSetter(String name) {
      String prefix = "set";
      if(!name.startsWith(prefix)) {
          return Optional.empty();
      }
      return Optional.ofNullable(uncapitalize(substringAfter(name, prefix)));
  }
  
  private Optional<TypeDef> findFieldTypeFromSetter(MethodDef method) {
      
    if (method.getReturnType().getName().equals("void")
            && method.getParamTypes().size() == 1) {
      return Optional.ofNullable(method.getParamTypes().get(0));
    } else {
      return Optional.empty();
    }
  }

private FieldDef createFieldDef(TypeDef type, String name) {
      FieldDef field = new FieldDef();
      TypeDef typeDef = new TypeDef();
      typeDef.setClassRef(type.getClassRef());
      typeDef.setName(type.getName());
      typeDef.setTypeParamList(type.getTypeParamList());
      typeDef.setVariable(null); // fieldDef's type don't have variable
      field.setType(typeDef);
      field.setName(name);
      return field;
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
        // TODO extract is-a relation
        return Collections.emptySet();
    }


    @Value
    class TypeWithCardinality{
        TypeDef type;
        String cardinality;
    }
    
    
    ClassDef newInstance(ClassDef original){
        ClassDef instance = new ClassDef();
        instance.setAnnotations(original.getAnnotations());
        instance.setFields(original.getFields());
        instance.setImplInterfaces(original.getImplInterfaces());
        instance.setKnownImplClasses(original.getKnownImplClasses());
        instance.setMethods(original.getMethods());
        instance.setName(original.getName());
        instance.setPkg(original.getPkg());
        instance.setSourceId(original.getSourceId());
        instance.setType(original.getType());
        return instance;
    }
}
