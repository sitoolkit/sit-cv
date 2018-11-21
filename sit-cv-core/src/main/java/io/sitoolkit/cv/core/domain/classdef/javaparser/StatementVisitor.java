package io.sitoolkit.cv.core.domain.classdef.javaparser;

import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.LambdaExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.MethodReferenceExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.ast.stmt.ForeachStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.stmt.WhileStmt;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.symbolsolver.javaparsermodel.JavaParserFacade;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class StatementVisitor extends VoidVisitorAdapter<VisitContext> {

    private static final Pattern STREAM_METHOD_PATTERN = Pattern
            .compile("java\\.util\\.stream\\.Stream\\..*");

    private final MethodResolver methodResolver;

    public static StatementVisitor build(JavaParserFacade jpf) {
        MethodResolver methodResolver = new MethodResolver(jpf);
        return new StatementVisitor(methodResolver);
    }

    @Override
    public void visit(ForeachStmt foreachStmt, VisitContext context) {
        String scope = buildForLoopScope(foreachStmt, " : ", "for (", ")");

        context.startLoopContext(foreachStmt, scope);
        super.visit(foreachStmt, context);
        context.endContext();
    }

    @Override
    public void visit(ForStmt forStmt, VisitContext context) {
        String scope = buildForLoopScope(forStmt, "; ", "for (", ")");

        context.startLoopContext(forStmt, scope);
        super.visit(forStmt, context);
        context.endContext();
    }

    @Override
    public void visit(IfStmt ifStmt, VisitContext context) {
        log.trace("{}Visiting IfStmt:{}", context.getLogLeftPadding(), ifStmt);

        if (context.isInBranch()) {
            super.visit(ifStmt, context);
        } else {
            context.startBranchContext(ifStmt);
            super.visit(ifStmt, context);
            context.endContext();
        }
    }

    @Override
    public void visit(WhileStmt whileStmt, VisitContext context) {
        super.visit(whileStmt, context);
    }

    @Override
    public void visit(MethodCallExpr methodCallExpr, VisitContext context) {

        log.trace("{}Visiting MethodCallExpr:{}", context.getLogLeftPadding(), methodCallExpr);

        if (context.isInBranch()) {
            super.visit(methodCallExpr, context);

        } else if (isStreamMethod(methodCallExpr)) {
            String scope = buildStreamLoopScope(methodCallExpr);
            context.startLoopContext(methodCallExpr, scope);
            super.visit(methodCallExpr, context);
            context.endContext();

        } else {
            super.visit(methodCallExpr, context);
            if (!isStreamStartMethod(methodCallExpr)) {
                methodResolver.resolve(methodCallExpr)
                        .map(resolvedMethodDeclaration -> DeclationProcessor.createMethodCall(
                                resolvedMethodDeclaration, methodCallExpr.getParentNode()))
                        .ifPresent(context::addStatement);
            }
        }
    }

    @Override
    public void visit(LambdaExpr lambdaExpr, VisitContext context) {
        if (context.isInLoop()) {
            super.visit(lambdaExpr, context);
        }
    }

    @Override
    public void visit(MethodReferenceExpr methodReferenceExpr, VisitContext context) {
        if (context.isInLoop()) {
            super.visit(methodReferenceExpr, context);
            methodResolver.resolve(methodReferenceExpr)
                    .map((m) -> DeclationProcessor.createMethodCall(m, Optional.empty()))
                    .ifPresent(context::addStatement);
        }
    }

    @Override
    public void visit(BlockStmt blockStmt, VisitContext context) {
        visitWithCheckPartOfIf(blockStmt, context, () -> super.visit(blockStmt, context));
    }

    @Override
    public void visit(ExpressionStmt expressionStmt, VisitContext context) {
        visitWithCheckPartOfIf(expressionStmt, context, () -> super.visit(expressionStmt, context));
    }

    void visitWithCheckPartOfIf(Statement stmt, VisitContext context, Runnable visitMethod) {

        if (context.isInBranch()) {
            IfStmt parentIf = (IfStmt) stmt.getParentNode().get();
            String condition = "";
            if (stmt.equals(parentIf.getThenStmt())) {
                condition = parentIf.getCondition().getTokenRange().map(Object::toString)
                        .orElse("");
            } else if (stmt.equals(parentIf.getElseStmt().get())) {
                condition = "else";
            }
            context.addConditionalContext(stmt, condition);
            visitMethod.run();
            context.endContext();
        } else {
            visitMethod.run();
        }
    }

    boolean isStreamMethod(MethodCallExpr n) {
        return methodResolver.resolve(n)
                .map(method -> STREAM_METHOD_PATTERN.matcher(method.getQualifiedName()).matches())
                .orElse(false);
    }

    boolean isStreamStartMethod(MethodCallExpr n) {
        return methodResolver.resolve(n)
                .map(method -> "java.util.Collection.stream".equals(method.getQualifiedName()))
                .orElse(false);
    }

    String buildStreamLoopScope(MethodCallExpr n) {
        return n.getScope().filter((s) -> s.isMethodCallExpr() && isStreamMethod(n))
                .map((scope) -> {
                    return buildStreamLoopScope(scope.asMethodCallExpr());
                }).orElseGet(() -> n.getTokenRange().map(Object::toString).orElse(""));
    }

    String buildForLoopScope(Node node, String delimiter, String prefix, String suffix) {
        return node.getChildNodes().stream().filter((c) -> !(c instanceof BlockStmt))
                .map(Object::toString).collect(Collectors.joining(delimiter, prefix, suffix));

    }
}
