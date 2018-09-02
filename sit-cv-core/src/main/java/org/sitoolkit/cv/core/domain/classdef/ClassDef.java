package org.sitoolkit.cv.core.domain.classdef;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = { "pkg", "name" })
public class ClassDef {

    private String pkg;
    private String name;
    private String sourceId;
    private List<MethodDef> methods = new ArrayList<>();
    private List<FieldDef> fields = new ArrayList<>();
}
