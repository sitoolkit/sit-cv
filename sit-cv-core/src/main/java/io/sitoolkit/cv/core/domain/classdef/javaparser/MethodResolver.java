package io.sitoolkit.cv.core.domain.classdef.javaparser;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.MethodReferenceExpr;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.github.javaparser.symbolsolver.javaparsermodel.JavaParserFacade;

public class MethodResolver {

  private final MethodCallResolver methodCallResolver;
  private final MethodReferenceResolver methodReferenceResolver;
  private Map<Node, Optional<ResolvedMethodDeclaration>> cache = new HashMap<>();

  public MethodResolver(JavaParserFacade jpf) {
    this.methodCallResolver = new MethodCallResolver(jpf);
    this.methodReferenceResolver = new MethodReferenceResolver(jpf);
  }

  Optional<ResolvedMethodDeclaration> resolve(Node n) {

    return cache.computeIfAbsent(
        n,
        node -> {
          if (node instanceof MethodCallExpr) {
            return methodCallResolver.resolve((MethodCallExpr) node);

          } else if (node instanceof MethodReferenceExpr) {
            return methodReferenceResolver.resolve((MethodReferenceExpr) node);

          } else {
            return Optional.empty();
          }
        });
  }

  public void clearCache() {
    cache.clear();
  }
}
