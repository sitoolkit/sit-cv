package io.sitoolkit.cv.core.domain.classdef.javaparser;

import java.util.List;

import com.github.javaparser.ast.expr.LambdaExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.MethodReferenceExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.github.javaparser.symbolsolver.javaparsermodel.JavaParserFacade;
import com.github.javaparser.symbolsolver.model.resolution.SymbolReference;

import io.sitoolkit.cv.core.domain.classdef.CvStatement;
import io.sitoolkit.cv.core.domain.classdef.MethodCallDef;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class MethodCallVisitor2 extends VoidVisitorAdapter<List<CvStatement>> {

    private JavaParserFacade jpf;

    @Override
    public void visit(MethodCallExpr methodCallExpr, List<CvStatement> statements) {
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
                statements.add(methodCall);
            } else {
                log.debug("Unsolved method call: {}", methodCallExpr);
            }
        } catch (Exception e) {
            log.debug("Unsolved method call: {}, {}", methodCallExpr, e.getMessage());
        }

    }

    @Override
    public void visit(LambdaExpr lambdaExpr, List<CvStatement> statements) {
    }

    @Override
    public void visit(MethodReferenceExpr methodRefExpr, List<CvStatement> statements) {
    }

}
