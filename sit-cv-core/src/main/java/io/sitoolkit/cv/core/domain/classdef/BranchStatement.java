package io.sitoolkit.cv.core.domain.classdef;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import lombok.Data;

@Data
public class BranchStatement implements CvStatement {

  private List<ConditionalStatement> conditions = new ArrayList<>();

  @Override
  public <T, C> Optional<T> process(StatementProcessor<T, C> processor) {
    return processor.process(this);
  }

  @Override
  public <T, C> Optional<T> process(StatementProcessor<T, C> processor, C context) {
    return processor.process(this, context);
  }
}
