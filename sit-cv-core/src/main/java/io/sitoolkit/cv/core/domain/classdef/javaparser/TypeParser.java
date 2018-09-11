package io.sitoolkit.cv.core.domain.classdef.javaparser;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedParameterDeclaration;
import com.github.javaparser.resolution.types.ResolvedReferenceType;
import com.github.javaparser.resolution.types.ResolvedType;

import io.sitoolkit.cv.core.domain.classdef.TypeDef;

public class TypeParser {

    public static List<TypeDef> getParamTypes(ResolvedMethodDeclaration declaredMethod) {
        return IntStream.range(0, declaredMethod.getNumberOfParams())
                .mapToObj(declaredMethod::getParam)
                .map(ResolvedParameterDeclaration::getType)
                .map(TypeParser::getTypeDef)
                .collect(Collectors.toList());
    }

    public static TypeDef getTypeDef(ResolvedType type) {
        TypeDef typeDef = new TypeDef();
        if (type.isPrimitive()) {
            typeDef.setName(type.asPrimitive().name().toLowerCase());
        } else if (type.isVoid()) {
            typeDef.setName("void");
        } else if (type.isArray()) {
            typeDef.setName(type.asArrayType().describe());
        } else if (type.isReference()) {
            ResolvedReferenceType rType = type.asReferenceType();
            typeDef.setName(rType.getQualifiedName());
            List<TypeDef> typeList = rType.getTypeParametersMap().stream()
                    .map(pair -> pair.b)
                    .map(TypeParser::getTypeDef)
                    .collect(Collectors.toList());
            typeDef.setTypeParamList(typeList);
        } else {
            typeDef.setName(type.toString());
        }
        return typeDef;
    }

}
