package io.sitoolkit.cv.core.domain.classdef;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class MethodCallDef extends MethodDef {
  private String className;
  private String packageName;
  private List<String> args = new ArrayList<>();

  @Override
  public <T, C> Optional<T> process(StatementProcessor<T, C> processor) {
    return processor.process(this);
  }

  @Override
  public <T, C> Optional<T> process(StatementProcessor<T, C> processor, C context) {
    return processor.process(this, context);
  }
}
