package io.sitoolkit.cv.core.domain.classdef;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import lombok.Data;

@Data
public abstract class CvStatementDefaultImpl implements CvStatement{
    private String body;
    private List<CvStatement> children = new ArrayList<>();

    @Override
    public Stream<MethodCallDef> getMethodCallsRecursively() {
        return children.stream().flatMap(CvStatement::getMethodCallsRecursively);
    }
}
