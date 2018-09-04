package org.sitoolkit.cv.core.domain.uml;

import java.util.Set;

import org.sitoolkit.cv.core.domain.classdef.ClassDef;
import org.sitoolkit.cv.core.domain.classdef.RelationDef;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ClassDiagram {
    private Set<ClassDef> classes;
    private Set<RelationDef> relations;
}
