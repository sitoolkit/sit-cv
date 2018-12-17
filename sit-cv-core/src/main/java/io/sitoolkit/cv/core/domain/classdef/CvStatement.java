package io.sitoolkit.cv.core.domain.classdef;

import java.util.Optional;
import java.util.stream.Stream;

public interface CvStatement {
    <T, C> Optional<T> process(StatementProcessor<T, C> processor);

    <T, C> Optional<T> process(StatementProcessor<T, C> processor, C context);

    Stream<MethodCallDef> getMethodCallsRecursively();
}
