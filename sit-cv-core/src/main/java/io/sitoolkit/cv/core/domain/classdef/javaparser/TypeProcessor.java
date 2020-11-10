package io.sitoolkit.cv.core.domain.classdef.javaparser;

import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedParameterDeclaration;
import com.github.javaparser.resolution.types.ResolvedReferenceType;
import com.github.javaparser.resolution.types.ResolvedType;
import io.sitoolkit.cv.core.domain.classdef.TypeDef;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TypeProcessor {

  public static List<TypeDef> collectParamTypes(ResolvedMethodDeclaration declaredMethod) {
    return IntStream.range(0, declaredMethod.getNumberOfParams())
        .mapToObj(declaredMethod::getParam)
        .map(TypeProcessor::createTypeDef)
        .collect(Collectors.toList());
  }

  public static Set<String> collectThrowTypeNames(ResolvedMethodDeclaration declaredMethod) {
    return IntStream.range(0, declaredMethod.getNumberOfSpecifiedExceptions())
        .mapToObj(declaredMethod::getSpecifiedException)
        .map(TypeProcessor::createTypeDef)
        .map(TypeDef::getName)
        .collect(Collectors.toSet());
  }

  public static TypeDef createTypeDef(ResolvedParameterDeclaration param) {
    TypeDef typeDef = createTypeDef(param.getType());
    typeDef.setVariable(param.getName());
    return typeDef;
  }

  public static TypeDef createTypeDef(ResolvedType type) {
    TypeDef typeDef = new TypeDef();
    if (type.isPrimitive()) {
      typeDef.setName(type.asPrimitive().name().toLowerCase());
    } else if (type.isVoid()) {
      typeDef.setName("void");
    } else if (type.isArray()) {
      typeDef.setName(type.asArrayType().describe());
    } else if (type.isTypeVariable()) {
      typeDef.setName(type.asTypeVariable().qualifiedName());
    } else if (type.isReference()) {
      try {
        ResolvedReferenceType rType = type.asReferenceType();
        typeDef.setName(rType.getQualifiedName());
        List<TypeDef> typeList =
            rType.getTypeParametersMap().stream()
                .map(pair -> pair.b)
                .map(TypeProcessor::createTypeDef)
                .collect(Collectors.toList());
        typeDef.setTypeParamList(typeList);
      } catch (UnsupportedOperationException e) {
        log.debug("Unsolved type:{}, {}", type, e.getMessage());
        typeDef.setName(type.toString());
      }
    } else {
      typeDef.setName(type.toString());
    }
    return typeDef;
  }
}
