package io.sitoolkit.cv.core.domain.classdef;

import java.util.Optional;

public interface StatementProcessor<T, C> {

    Optional<T> process(CvStatement statement);
    Optional<T> process(CvStatement statement, C context);

    Optional<T> process(LoopStatement statement);
    Optional<T> process(LoopStatement statement, C context);

    Optional<T> process(BranchStatement statement);
    Optional<T> process(BranchStatement statement, C context);

    Optional<T> process(ConditionalStatement statement);
    Optional<T> process(ConditionalStatement statement, C context);

    Optional<T> process(TryStatement statement);
    Optional<T> process(TryStatement statement, C context);

    Optional<T> process(CatchStatement statement);
    Optional<T> process(CatchStatement statement, C context);

    Optional<T> process(FinallyStatement statement);
    Optional<T> process(FinallyStatement statement, C context);

    Optional<T> process(MethodCallDef statement);
    Optional<T> process(MethodCallDef statement, C context);
}
