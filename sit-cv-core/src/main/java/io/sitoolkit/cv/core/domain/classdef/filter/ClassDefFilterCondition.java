package io.sitoolkit.cv.core.domain.classdef.filter;

import java.util.HashSet;
import java.util.Set;

import lombok.Data;

@Data
public class ClassDefFilterCondition {
    Set<String> types = new HashSet<>();
    Set<String> annotations = new HashSet<>();
}
