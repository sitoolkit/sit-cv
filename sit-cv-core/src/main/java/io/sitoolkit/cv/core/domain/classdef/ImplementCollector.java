package io.sitoolkit.cv.core.domain.classdef;

import java.util.List;
import java.util.stream.Stream;

public class ImplementCollector {

    private ImplementDetector implementDetector = new ImplementDetector();

    public Stream<MethodDef> collectMethodCallsRecursively(MethodDef method) {
        return collectMethodCallsRecursively(method.getMethodCalls());
    }

    private Stream<MethodDef> collectMethodCallsRecursively(List<MethodCallDef> methodCalls) {
        return methodCalls.stream().map(implementDetector::detectImplMethod).flatMap((method) -> {
            return Stream.concat(Stream.of(method), collectMethodCallsRecursively(method));
        });
    }
}
