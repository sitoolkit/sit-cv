package io.sitoolkit.cv.core.domain.classdef;

import io.sitoolkit.cv.core.domain.uml.RelationType;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class RelationDef {

    private ClassDef self;
    private ClassDef other;
    private RelationType type;

    @Builder.Default
    private String selfCardinality = "";

    @Builder.Default
    private String otherCardinality = "";

    @Builder.Default
    private String description = "";
}
