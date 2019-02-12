package io.sitoolkit.cv.core.domain.classdef;

import java.util.Optional;

public interface CvStatement {
    <T, C> Optional<T> process(StatementProcessor<T, C> processor);

    <T, C> Optional<T> process(StatementProcessor<T, C> processor, C context);
}
