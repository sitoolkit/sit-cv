package io.sitoolkit.cv.core.domain.classdef;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.Data;

@Data
public class TypeDef {
    private String name;
    private List<TypeDef> typeParamList = new ArrayList<>();
    private ClassDef classRef;

    public Stream<TypeDef> getTypeParamsRecursively(){
        return Stream.concat(Stream.of(this),
                typeParamList.stream().flatMap(TypeDef::getTypeParamsRecursively));
    }

    @Override
    public String toString() {
        if (getTypeParamList().isEmpty()) {
            return getName();
        } else {
            return getName() +
                    getTypeParamList().stream()
                            .map(TypeDef::toString)
                            .collect(Collectors.joining(",", "<", ">"));
        }
    }
}
