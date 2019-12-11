package io.sitoolkit.cv.core.domain.classdef;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApiDocDef {
  private String qualifiedClassName;
  private List<String> annotations;
  private String methodDeclaration;
  private List<String> contents;
}
