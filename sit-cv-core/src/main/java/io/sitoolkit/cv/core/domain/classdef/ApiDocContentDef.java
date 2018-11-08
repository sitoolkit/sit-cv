package io.sitoolkit.cv.core.domain.classdef;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class ApiDocContentDef {
    String name;
    String label;
    List<String> items = new ArrayList<>();
}
