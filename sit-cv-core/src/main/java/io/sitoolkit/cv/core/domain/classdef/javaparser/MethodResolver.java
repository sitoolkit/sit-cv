package io.sitoolkit.cv.core.domain.classdef.javaparser;

import java.util.Optional;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.MethodReferenceExpr;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.github.javaparser.symbolsolver.javaparsermodel.JavaParserFacade;

public class MethodResolver {

    private final MethodCallResolver methodCallResolver;
    private final MethodReferenceResolver methodReferenceResolver;

    public MethodResolver(JavaParserFacade jpf) {
        this.methodCallResolver = new MethodCallResolver(jpf);
        this.methodReferenceResolver = new MethodReferenceResolver(jpf);
    }

    Optional<ResolvedMethodDeclaration> resolve(Node n) {
        if (n instanceof MethodCallExpr) {
            return methodCallResolver.resolve((MethodCallExpr) n);

        } else if (n instanceof MethodReferenceExpr) {
            return methodReferenceResolver.resolve((MethodReferenceExpr)n);

        } else {
            return Optional.empty();
        }
    }
}
