package io.sitoolkit.cv.core.domain.classdef.javaparser;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.github.javaparser.ast.expr.ClassExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodReferenceExpr;
import com.github.javaparser.ast.expr.ThisExpr;
import com.github.javaparser.ast.expr.TypeExpr;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.resolution.MethodUsage;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedReferenceTypeDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedTypeDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedValueDeclaration;
import com.github.javaparser.resolution.types.ResolvedReferenceType;
import com.github.javaparser.resolution.types.ResolvedType;
import com.github.javaparser.symbolsolver.javaparsermodel.JavaParserFacade;
import com.github.javaparser.symbolsolver.model.resolution.SymbolReference;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
class MethodReferenceResolver {

    private final JavaParserFacade jpf;

    public Optional<ResolvedMethodDeclaration> resolve(MethodReferenceExpr methodRefExpr) {
        Expression scope = methodRefExpr.getScope();
        String identifier = methodRefExpr.getIdentifier();
        Optional<ResolvedMethodDeclaration> result = findMethodDeclation(resolveMethodsFromTypeOf(scope), identifier);

        if (result.isPresent()) {
            log.debug("method reference solved: '{}' -> {} ", methodRefExpr, result.get());
        } else {
            log.debug("method reference unsolved: '{}' ", methodRefExpr);
        }
        return result;
    }

    List<ResolvedMethodDeclaration> resolveMethodsFromTypeOf(Expression exp) {

        if (exp instanceof TypeExpr) {
            return resolveType((TypeExpr) exp)
                    .map(ResolvedReferenceType::getAllMethods)
                    .orElse(Collections.emptyList());

        } else if (exp instanceof ThisExpr) {
            return resolveTypeDeclaration((ThisExpr) exp)
                    .map(declatrion -> declatrion.getAllMethods().stream()
                            .map(MethodUsage::getDeclaration)
                            .collect(Collectors.toList()))
                    .orElse(Collections.emptyList());

        } else if (exp instanceof ClassExpr) {

            //TODO resolving like 'AAA.class' to methods of java.lang.Class
            return Collections.emptyList();

        } else {
            return Collections.emptyList();
        }
    }

    Optional<ResolvedReferenceType> resolveType(TypeExpr exp) {

        //try to solve as static method reference
        Type type = exp.getType();
        try {
            ResolvedType rt = jpf.convertToUsage(type);
            if (rt.isReferenceType()) {

                log.debug("Solved as Type : {}", exp);
                return Optional.of(rt.asReferenceType());
            } else {
                log.debug("Unsolved as Type : {}", exp);
            }

        } catch (Exception e) {
            log.debug("Unsolved as Type : {}, {}", exp, e.getMessage());
        }

        //try to solve as instance method reference
        try {
            SymbolReference<? extends ResolvedValueDeclaration> sr = jpf.getSymbolSolver().solveSymbol(exp.toString(),
                    exp);

            if (sr.isSolved()) {
                log.debug("Solved as Symbol : {}", exp);
                ResolvedType rt = sr.getCorrespondingDeclaration().getType();
                if (rt.isReferenceType()) {
                    log.debug("type of Symbol '{}' : {}", exp, rt);
                    return Optional.of(rt.asReferenceType());
                }

            } else {
                log.debug("Unsolved as Symbol : {}", exp);
            }

        } catch (Exception e) {
            log.debug("Unsolved as Symbol : {}, {}", exp, e.getMessage());
        }

        return Optional.empty();
    }

    Optional<ResolvedReferenceTypeDeclaration> resolveTypeDeclaration(ThisExpr exp) {
        try {
            SymbolReference<ResolvedTypeDeclaration> sr = jpf.solve(exp);
            if (sr.isSolved()) {
                ResolvedReferenceTypeDeclaration rrtd = sr.getCorrespondingDeclaration().asReferenceType();
                log.debug("Solved thisExpr : {}", exp, rrtd);
                return Optional.of(rrtd);
            } else {
                log.debug("Unsolved thisExpr : {}", exp);
            }

        } catch (Exception e) {
            log.debug("Unsolved thisExpr : {}, {}", exp, e.getMessage());
        }
        return Optional.empty();

    }

    Optional<ResolvedMethodDeclaration> findMethodDeclation(List<ResolvedMethodDeclaration> methods,
            String methodIdentifier) {

        List<ResolvedMethodDeclaration> found = methods.stream()
                .filter(m -> m.getName().equals(methodIdentifier))
                .collect(Collectors.toList());

        if (found.size() == 1) {
            ResolvedMethodDeclaration rmd = found.get(0);
            return Optional.of(rmd);

        } else if (found.size() > 1) {

            //TODO finding from overloaded methods
            log.debug("Coudn't specify method reference:'{}' because type has overloaded methods: {}",
                    methodIdentifier, found);
            return Optional.empty();

        } else {
            return Optional.empty();
        }
    }
}
