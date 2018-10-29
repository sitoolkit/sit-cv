package io.sitoolkit.cv.core.domain.classdef;

import java.util.Optional;

public interface StatementProcessor<T> {

    Optional<T> process(CvStatement statement);

    Optional<T> process(LoopStatement statement);

    Optional<T> process(MethodCallDef statement);
}
