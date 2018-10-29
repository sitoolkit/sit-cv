package io.sitoolkit.cv.core.domain.classdef;

import java.util.Optional;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class LoopStatement extends CvStatement {

    @Override
    public <T> Optional<T> process(StatementProcessor<T> processor) {
        return processor.process(this);
    }

}
