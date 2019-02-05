package io.sitoolkit.cv.core.domain.classdef;

import java.util.Set;
import java.util.stream.Stream;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class ImplementCollector {

    @NonNull
    private ImplementDetector implementDetector;

    public Stream<MethodDef> collectMethodCallsRecursively(MethodDef method) {
        return Stream.concat(Stream.of(method),
                collectMethodCallsRecursively(method.getMethodCalls(), MethodCallStack.getBlank()));
    }

    private Stream<MethodDef> collectMethodCallsRecursively(MethodDef method,
            MethodCallStack callStack) {

        MethodDef methodImpl = implementDetector.detectImplMethod(method);

        if (callStack.contains(methodImpl)) {
            log.debug("method: {} is called recursively", methodImpl.getQualifiedSignature());
            return Stream.empty();
        }
        MethodCallStack pushedStack = callStack.push(methodImpl);

        return Stream.concat(Stream.of(methodImpl),
                collectMethodCallsRecursively(methodImpl.getMethodCalls(), pushedStack));
    }

    private Stream<MethodDef> collectMethodCallsRecursively(Set<MethodCallDef> methodCalls,
            MethodCallStack callStack) {
        return methodCalls.stream().flatMap((method) -> {
            return collectMethodCallsRecursively(method, callStack);
        });
    }
}
