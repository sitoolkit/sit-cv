package io.sitoolkit.cv.core.domain.classdef;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(exclude = "classDef", callSuper = true)
public class MethodDef extends CvStatement {

    private String name;
    private String signature;
    private String qualifiedSignature;
    private boolean isPublic;
    private String actionPath;
    private ClassDef classDef;
    private List<TypeDef> paramTypes;
    private TypeDef returnType;
    private List<MethodCallDef> methodCalls = new ArrayList<>();
    private List<CvStatement> statements = new ArrayList<>();
    private String comment = "";

    public Stream<MethodDef> getMethodCallsRecursively() {
        return Stream.concat(Stream.of(this),
                methodCalls.stream().flatMap(MethodDef::getMethodCallsRecursively));
    }

    @Override
    public <T> Optional<T> process(StatementProcessor<T> processor) {
        return processor.process(this);
    }

}
