package io.sitoolkit.cv.core.domain.classdef.javaparser;

import java.util.List;
import java.util.Optional;

import com.github.javaparser.ast.expr.LambdaExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.MethodReferenceExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.github.javaparser.symbolsolver.javaparsermodel.JavaParserFacade;
import com.github.javaparser.symbolsolver.model.resolution.SymbolReference;

import io.sitoolkit.cv.core.domain.classdef.CvStatement;
import io.sitoolkit.cv.core.domain.classdef.MethodCallDef;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MethodCallVisitor extends VoidVisitorAdapter<List<CvStatement>> {

    private final JavaParserFacade jpf;
    private final MethodReferenceSolver methodRefSolver;

    public MethodCallVisitor(JavaParserFacade jpf) {
        super();
        this.jpf = jpf;
        this.methodRefSolver = new MethodReferenceSolver(jpf);
    }

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
    public void visit(LambdaExpr lambdaExpr, List<CvStatement> statements) {
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
        return methodRefSolver.solve(methodRefExpr).map(this::createMethodCall);
    }

}
