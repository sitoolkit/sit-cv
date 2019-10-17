package io.sitoolkit.cv.core.domain.classdef;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Stream;

@Slf4j
@Data
@EqualsAndHashCode(of = "qualifiedSignature")
@ToString(exclude = { "classDef", "methodCalls", "statements" })
public class MethodDef implements CvStatement {

    private String name;
    private String signature;
    private String qualifiedSignature;
    private boolean isPublic;
    private boolean isAsync;
    private String actionPath;
    private ClassDef classDef;
    private List<TypeDef> paramTypes;
    private List<String> exceptions;
    private TypeDef returnType;
    private Set<MethodCallDef> methodCalls = new HashSet<>();
    private List<CvStatement> statements = new ArrayList<>();
    private ApiDocDef apiDoc;

    @Override
    public <T, C> Optional<T> process(StatementProcessor<T, C> processor) {
        return processor.process(this);
    }

    @Override
    public <T, C> Optional<T> process(StatementProcessor<T, C> processor, C context) {
        return processor.process(this, context);
    }

    public MethodDef findImplementation() {
        ClassDef clazz = getClassDef();

        if (clazz == null) {
            return this;
        }

        ClassDef impleClass = clazz.findImplementation();
        Optional<MethodDef> methodImpl = impleClass.findMethodBySignature(getSignature());
        if (methodImpl.isPresent()) {
            return methodImpl.get();
        }

        return this;
    }

    public Stream<MethodDef> collectCalledMethodsRecursively() {
        return Stream.concat(Stream.of(this),
                collectCalledMethodsRecursively(getMethodCalls(), MethodCallStack.getBlank()));
    }

    private Stream<MethodDef> collectCalledMethodsRecursively(MethodCallDef method,
            MethodCallStack callStack) {

        MethodDef methodImpl = method.findImplementation();

        if (callStack.contains(methodImpl)) {
            log.debug("method: {} is called recursively", methodImpl.getQualifiedSignature());
            return Stream.empty();
        }
        MethodCallStack pushedStack = callStack.push(methodImpl);

        return Stream.concat(Stream.of(methodImpl),
                collectCalledMethodsRecursively(methodImpl.getMethodCalls(), pushedStack));
    }

    private Stream<MethodDef> collectCalledMethodsRecursively(Set<MethodCallDef> methodCalls,
            MethodCallStack callStack) {
        return methodCalls.stream().flatMap((method) -> {
            return collectCalledMethodsRecursively(method, callStack);
        });
    }
}
