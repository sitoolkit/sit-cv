package io.sitoolkit.cv.core.domain.classdef.javaparser;

import com.github.javaparser.ast.Node;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;

import io.sitoolkit.cv.core.domain.classdef.LoopStatement;
import io.sitoolkit.cv.core.domain.classdef.MethodCallDef;

public class DeclationProcessor {

    public static LoopStatement createLoopStatement(Node n) {
        LoopStatement statement = new LoopStatement();
        statement.setBody(n.toString());
        return statement;
    }

    public static MethodCallDef createMethodCall(ResolvedMethodDeclaration rmd) {
        MethodCallDef methodCall = new MethodCallDef();
        methodCall.setSignature(rmd.getSignature());
        methodCall.setQualifiedSignature(rmd.getQualifiedSignature());
        methodCall.setName(rmd.getName());
        methodCall.setClassName(rmd.getClassName());
        methodCall.setPackageName(rmd.getPackageName());
        methodCall.setReturnType(TypeProcessor.createTypeDef(rmd.getReturnType()));
        methodCall.setParamTypes(TypeProcessor.collectParamTypes(rmd));
        return methodCall;
    }

}
