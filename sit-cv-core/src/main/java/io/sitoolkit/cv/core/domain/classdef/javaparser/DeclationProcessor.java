package io.sitoolkit.cv.core.domain.classdef.javaparser;

import java.util.Optional;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;

import io.sitoolkit.cv.core.domain.classdef.BranchStatement;
import io.sitoolkit.cv.core.domain.classdef.ConditionalStatement;
import io.sitoolkit.cv.core.domain.classdef.LoopStatement;
import io.sitoolkit.cv.core.domain.classdef.MethodCallDef;
import io.sitoolkit.cv.core.domain.classdef.TypeDef;

public class DeclationProcessor {

    public static LoopStatement createLoopStatement(Node n) {
        LoopStatement statement = new LoopStatement();
        statement.setBody(n.toString());
        return statement;
    }

    public static BranchStatement createBranchStatement(IfStmt n) {
        BranchStatement statement = new BranchStatement();
        statement.setBody(n.toString());
        return statement;
    }

    public static ConditionalStatement createConditionalStatement(Statement n, String condition) {
        ConditionalStatement statement = new ConditionalStatement();
        statement.setBody(n.toString());
        statement.setCondition(condition);
        return statement;
    }

    public static MethodCallDef createMethodCall(ResolvedMethodDeclaration rmd, Optional<Node> parentNode) {
        MethodCallDef methodCall = new MethodCallDef();
        methodCall.setSignature(rmd.getSignature());
        methodCall.setQualifiedSignature(rmd.getQualifiedSignature());
        methodCall.setName(rmd.getName());
        methodCall.setClassName(rmd.getClassName());
        methodCall.setPackageName(rmd.getPackageName());
        TypeDef returnType = TypeProcessor.createTypeDef(rmd.getReturnType());
        if(parentNode.isPresent() && parentNode.get() instanceof VariableDeclarator) {
            String variable = ((VariableDeclarator)parentNode.get()).getNameAsString();
            returnType.setVariable(variable);
        }
        methodCall.setReturnType(returnType);
        methodCall.setParamTypes(TypeProcessor.collectParamTypes(rmd));
        return methodCall;
    }

}
