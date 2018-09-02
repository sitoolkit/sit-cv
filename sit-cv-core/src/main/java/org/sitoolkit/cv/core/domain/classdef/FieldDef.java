package org.sitoolkit.cv.core.domain.classdef;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class FieldDef {

    private String name;
    private String type;
    private List<String> typeParams = new ArrayList<>();
    private ClassDef typeRef;

}
