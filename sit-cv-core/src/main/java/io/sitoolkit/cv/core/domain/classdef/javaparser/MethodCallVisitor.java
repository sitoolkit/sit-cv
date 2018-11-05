package io.sitoolkit.cv.core.domain.classdef.javaparser;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.LambdaExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.MethodReferenceExpr;
import com.github.javaparser.ast.expr.ThisExpr;
import com.github.javaparser.ast.expr.TypeExpr;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.resolution.MethodUsage;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedReferenceTypeDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedTypeDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedValueDeclaration;
import com.github.javaparser.resolution.types.ResolvedReferenceType;
import com.github.javaparser.resolution.types.ResolvedType;
import com.github.javaparser.symbolsolver.javaparsermodel.JavaParserFacade;
import com.github.javaparser.symbolsolver.model.resolution.SymbolReference;

import io.sitoolkit.cv.core.domain.classdef.CvStatement;
import io.sitoolkit.cv.core.domain.classdef.MethodCallDef;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class MethodCallVisitor extends VoidVisitorAdapter<List<CvStatement>> {

    private JavaParserFacade jpf;

    @Override
    public void visit(MethodCallExpr methodCallExpr, List<CvStatement> statements) {

        methodCallExpr.getScope().ifPresent(l -> l.accept(this, statements));
        methodCallExpr.getArguments().forEach(p -> p.accept(this, statements));

        getMethodCall(methodCallExpr).ifPresent(methodCall -> {
            log.debug("Add method call : {}", methodCall);
            statements.add(methodCall);
        });
    }

    Optional<MethodCallDef> getMethodCall(MethodCallExpr methodCallExpr) {
        return getResolvedMethod(methodCallExpr).map(this::createMethodCall);
    }

    Optional<ResolvedMethodDeclaration> getResolvedMethod(MethodCallExpr methodCallExpr) {
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

    MethodCallDef createMethodCall(ResolvedMethodDeclaration rmd) {
        MethodCallDef methodCall = new MethodCallDef();
        methodCall.setSignature(rmd.getSignature());
        methodCall.setQualifiedSignature(rmd.getQualifiedSignature());
        methodCall.setName(rmd.getName());
        methodCall.setClassName(rmd.getClassName());
        methodCall.setPackageName(rmd.getPackageName());
        methodCall.setReturnType(TypeParser.getTypeDef(rmd.getReturnType()));
        methodCall.setParamTypes(TypeParser.getParamTypes(rmd));
        return methodCall;
    }

    @Override
    public void visit(MethodReferenceExpr methodRefExpr, List<CvStatement> statements) {
        super.visit(methodRefExpr, statements);
        getMethodCall(methodRefExpr).ifPresent(methodCall -> {
            log.debug("Add method call : {}", methodCall);
            statements.add(methodCall);
        });
    }

    Optional<MethodCallDef> getMethodCall(MethodReferenceExpr methodRefExpr) {
        return getResolvedMethod(methodRefExpr).map(this::createMethodCall);
    }

    Optional<ResolvedMethodDeclaration> getResolvedMethod(MethodReferenceExpr methodRefExpr) {
        Expression scope = methodRefExpr.getScope();
        String identifier = methodRefExpr.getIdentifier();
        log.debug("scope type of '{}' is {}", methodRefExpr, scope.getClass());

        return resolveType(scope)
                .flatMap(type -> findMethodDeclation(type, identifier));
    }

    Optional<Supplier<List<ResolvedMethodDeclaration>>> resolveType(Expression exp) {

        if (exp instanceof TypeExpr) {
            return resolveType((TypeExpr) exp)
                    .map(type -> () -> getResolvedMethods(type));

        } else if (exp instanceof ThisExpr) {
            return resolveTypeDeclaration((ThisExpr) exp)
                    .map(type -> () -> getResolvedMethods(type));

        } else {
            return Optional.empty();
        }
    }

    List<ResolvedMethodDeclaration> getResolvedMethods(ResolvedReferenceType rrt) {
        return rrt.getAllMethods();
    }

    List<ResolvedMethodDeclaration> getResolvedMethods(ResolvedReferenceTypeDeclaration rrtd) {
        return rrtd.getAllMethods().stream()
                .map(MethodUsage::getDeclaration)
                .collect(Collectors.toList());
    }

    Optional<ResolvedReferenceType> resolveType(TypeExpr exp) {

        log.debug("resolveType - type : {}, {}", exp, exp.getClass());

        //try to solve as static method reference
        Type type = exp.getType();
        try {
            ResolvedType rt = jpf.convertToUsage(type);
            if (rt.isReferenceType()) {
                return Optional.of(rt.asReferenceType());
            }

        } catch (Exception e) {
            log.debug("Unsolved Type : {}, {}", exp, e.getMessage());
        }

        //try to solve as instance method reference
        try {
            SymbolReference<? extends ResolvedValueDeclaration> sr = jpf.getSymbolSolver().solveSymbol(exp.toString(),
                    exp);

            if (sr.isSolved()) {
                ResolvedType rt = sr.getCorrespondingDeclaration().getType();
                if (rt.isReferenceType()) {
                    return Optional.of(rt.asReferenceType());
                }

            } else {
                log.debug("Unsolved Symbol : {}", exp);
            }

        } catch (Exception e) {
            log.debug("Unsolved Symbol : {}, {}", exp, e.getMessage());
        }

        return Optional.empty();
    }

    Optional<ResolvedReferenceTypeDeclaration> resolveTypeDeclaration(ThisExpr exp) {
        log.debug("resolveType - this : {}, {}", exp, exp.getClass());

        try {
            SymbolReference<ResolvedTypeDeclaration> sr = jpf.solve(exp);
            if (sr.isSolved()) {
                ResolvedReferenceTypeDeclaration rrtd = sr.getCorrespondingDeclaration().asReferenceType();
                return Optional.of(rrtd);
            } else {
                log.debug("Unsolved Symbol : {}", exp);
            }

        } catch (Exception e) {
            log.debug("Unsolved Symbol : {}, {}", exp, e.getMessage());
        }
        return Optional.empty();

    }

    Optional<ResolvedMethodDeclaration> findMethodDeclation(Supplier<List<ResolvedMethodDeclaration>> fromType,
            String methodIdentifier) {

        List<ResolvedMethodDeclaration> methods = fromType.get().stream()
                .filter(m -> m.getName().equals(methodIdentifier))
                .collect(Collectors.toList());

        if (methods.size() == 1) {
            ResolvedMethodDeclaration rmd = methods.get(0);
            log.debug("method found: {}", rmd);
            return Optional.of(rmd);

        } else if (methods.size() > 1) {

            //TODO finding from overloaded methods
            log.debug("Coudn't specify method reference:'{}' because type has overloaded methods",
                    methodIdentifier);
            return Optional.empty();

        } else {
            log.debug("method: '{}' not found from '{}'", methodIdentifier);
            return Optional.empty();
        }
    }

    @Override
    public void visit(LambdaExpr lambdaExpr, List<CvStatement> statements) {
    }

}
