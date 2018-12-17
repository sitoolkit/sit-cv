package io.sitoolkit.cv.core.domain.classdef;

import java.util.Optional;
import java.util.stream.Stream;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class MethodCallDef extends MethodDef {
    private String className;
    private String packageName;

    @Override
    public <T, C> Optional<T> process(StatementProcessor<T, C> processor) {
        return processor.process(this);
    }

    @Override
    public <T, C> Optional<T> process(StatementProcessor<T, C> processor, C context) {
        return processor.process(this, context);
    }

    @Override
    public Stream<MethodCallDef> getMethodCallsRecursively() {
        return Stream.of(this);
    }
}
