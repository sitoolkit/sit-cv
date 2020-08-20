package io.sitoolkit.cv.core.domain.classdef;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MethodCallStack {

  @Getter private static final MethodCallStack blank = new MethodCallStack();

  private final List<MethodDef> methodList;

  private MethodCallStack() {
    this(Collections.emptyList());
  }

  public MethodCallStack push(MethodDef method) {
    List<MethodDef> newStack = new ArrayList<>(methodList);
    newStack.add(method);
    return new MethodCallStack(newStack);
  }

  public boolean contains(MethodDef method) {
    return methodList.stream().anyMatch(m -> equalsAsMethod(m, method));
  }

  boolean equalsAsMethod(MethodDef m1, MethodDef m2) {
    return StringUtils.equals(m1.getQualifiedSignature(), m2.getQualifiedSignature());
  }

  public Optional<MethodDef> findLastCalled() {
    if (methodList == null || methodList.isEmpty()) {
      return Optional.empty();
    } else {
      return Optional.of(methodList.get(methodList.size() - 1));
    }
  }
}
