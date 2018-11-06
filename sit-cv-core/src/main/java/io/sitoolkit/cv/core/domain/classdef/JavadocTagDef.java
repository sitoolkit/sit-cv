package io.sitoolkit.cv.core.domain.classdef;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public abstract class JavadocTagDef {
    String name;
    String label;
    List<String> contents = new ArrayList<>();
}
