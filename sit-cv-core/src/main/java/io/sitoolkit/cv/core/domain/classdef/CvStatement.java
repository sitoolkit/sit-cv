package io.sitoolkit.cv.core.domain.classdef;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import lombok.Data;

@Data
public abstract class CvStatement {
    private String body;
    private List<CvStatement> children = new ArrayList<>();

    public abstract <T, C> Optional<T> process(StatementProcessor<T, C> processor);

    public abstract <T, C> Optional<T> process(StatementProcessor<T, C> processor, C context);

}
