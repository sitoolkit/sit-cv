package io.sitoolkit.cv.core.domain.classdef.javaparser;

import java.util.Set;

import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.github.javaparser.symbolsolver.javaparsermodel.JavaParserFacade;
import com.github.javaparser.symbolsolver.model.resolution.SymbolReference;

import io.sitoolkit.cv.core.domain.classdef.MethodCallDef;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@Slf4j
public class MethodCallVisitor extends VoidVisitorAdapter<Set<MethodCallDef>> {

    JavaParserFacade jpf;

    @Override
    public void visit(MethodCallExpr methodCallExpr, Set<MethodCallDef> methodCalls) {
        try {

            SymbolReference<ResolvedMethodDeclaration> ref = jpf.solve(methodCallExpr);

            if (ref.isSolved()) {
                ResolvedMethodDeclaration rmd = ref.getCorrespondingDeclaration();
                MethodCallDef methodCall = new MethodCallDef();
                methodCall.setSignature(rmd.getSignature());
                methodCall.setQualifiedSignature(rmd.getQualifiedSignature());
                methodCall.setName(rmd.getName());
                methodCall.setClassName(rmd.getClassName());
                methodCall.setPackageName(rmd.getPackageName());
                methodCall.setReturnType(TypeParser.getTypeDef(rmd.getReturnType()));
                methodCall.setParamTypes(TypeParser.getParamTypes(rmd));
                log.debug("Add method call : {}", methodCall);
                methodCalls.add(methodCall);
            } else {
                log.debug("Unsolved : {}", methodCallExpr);
            }
        } catch (Exception e) {
            log.debug("Unsolved:{}, {}", methodCallExpr, e);
        }

    }

}
