package io.sitoolkit.cv.core.domain.classdef.javaparser;

import java.util.Optional;

import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.github.javaparser.symbolsolver.javaparsermodel.JavaParserFacade;
import com.github.javaparser.symbolsolver.model.resolution.SymbolReference;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
class MethodCallResolver {

    private final JavaParserFacade jpf;

    Optional<ResolvedMethodDeclaration> resolve(MethodCallExpr methodCallExpr) {
        try {
            SymbolReference<ResolvedMethodDeclaration> ref = jpf.solve(methodCallExpr);
            if (ref.isSolved()) {
                ResolvedMethodDeclaration rmd = ref.getCorrespondingDeclaration();
                return Optional.of(rmd);
            } else {
                log.debug("Unsolved method call: {}", methodCallExpr);
                return Optional.empty();
            }
        } catch (Exception e) {
            log.debug("Unsolved method call: {}, {}", methodCallExpr, e.getMessage());
            return Optional.empty();
        }
    }
}
