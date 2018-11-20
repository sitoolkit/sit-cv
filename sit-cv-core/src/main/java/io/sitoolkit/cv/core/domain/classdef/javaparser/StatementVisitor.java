package io.sitoolkit.cv.core.domain.classdef.javaparser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.LambdaExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.MethodReferenceExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.ast.stmt.ForeachStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.WhileStmt;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.symbolsolver.javaparsermodel.JavaParserFacade;

import io.sitoolkit.cv.core.domain.classdef.BranchStatement;
import io.sitoolkit.cv.core.domain.classdef.ConditionalStatement;
import io.sitoolkit.cv.core.domain.classdef.CvStatement;
import io.sitoolkit.cv.core.domain.classdef.CvStatementDefaultImpl;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class StatementVisitor extends VoidVisitorAdapter<VisitContext<CvStatement>> {

    private static final Pattern STREAM_METHOD_PATTERN = Pattern
            .compile("^java\\.util\\.stream\\.Stream\\..*");

    private final MethodResolver methodResolver;

    public static StatementVisitor build(JavaParserFacade jpf) {
        MethodResolver methodResolver = new MethodResolver(jpf);
        return new StatementVisitor(methodResolver);
    }

    @Override
    public void visit(ForeachStmt n, VisitContext<CvStatement> context) {
        String condition = n.getChildNodes().stream().filter((c) -> !(c instanceof BlockStmt))
                .map(Object::toString).collect(Collectors.joining(" : ", "for (", ")"));
        CvStatementDefaultImpl statement = DeclationProcessor.createLoopStatement(n, condition);
        context.addStatement(statement);
        super.visit(n, VisitContext.childrenOf(statement));
    }

    @Override
    public void visit(ForStmt n, VisitContext<CvStatement> context) {
        String condition = n.getChildNodes().stream().filter((c) -> !(c instanceof BlockStmt))
                .map(Object::toString).collect(Collectors.joining("; ", "for (", ")"));
        CvStatementDefaultImpl statement = DeclationProcessor.createLoopStatement(n, condition);
        context.addStatement(statement);
        super.visit(n, VisitContext.childrenOf(statement));
    }

    @Override
    public void visit(IfStmt n, VisitContext<CvStatement> context) {
        BranchStatement statement = DeclationProcessor.createBranchStatement(n);
        context.addStatement(statement);
        visitIfStmt(n, VisitContext.conditionsOf(statement));
    }

    private void visitIfStmt(IfStmt n, VisitContext<ConditionalStatement> context) {
        ConditionalStatement thenStatement = DeclationProcessor.createConditionalStatement(n,
                n.getCondition().toString());
        if (context.statements.isEmpty()) {
            thenStatement.setFirst(true);
        }
        context.addStatement(thenStatement);

        n.getThenStmt().accept(this, VisitContext.childrenOf(thenStatement));
        n.getElseStmt().ifPresent(l -> {
            if (l.isIfStmt()) {
                visitIfStmt((IfStmt)l, context);
            } else {
                ConditionalStatement elseStatement = DeclationProcessor
                        .createConditionalStatement(l, "else");
                context.addStatement(elseStatement);
                l.accept(this, VisitContext.childrenOf(elseStatement));
            }
        });
    }

    @Override
    public void visit(WhileStmt n, VisitContext<CvStatement> context) {
        super.visit(n, context);
    }

    @Override
    public void visit(MethodCallExpr n, VisitContext<CvStatement> context) {

        if (isStreamMethod(n)) {
            findNonStreamMethod(n).ifPresent(l -> l.accept(this, context));
            String condition = getStreamLoopCondition(n);
            CvStatementDefaultImpl statement = DeclationProcessor.createLoopStatement(n, condition);
            context.addStatement(statement);
            collectStreamMethodArguments(n).forEach(p -> p.accept(this, VisitContext.childrenOf(statement)));

        } else {
            n.getScope().ifPresent(l -> l.accept(this, context));
            n.getArguments().forEach(p -> p.accept(this, context));
            methodResolver.resolve(n)
                    .map((m) -> DeclationProcessor.createMethodCall(m, n.getParentNode()))
                    .ifPresent(context::addStatement);
        }
    }

    @Override
    public void visit(LambdaExpr n, VisitContext<CvStatement> context) {
        if (context.isInLoop()) {
            super.visit(n, context);
        }
    }
    @Override
    public void visit(MethodReferenceExpr n, VisitContext<CvStatement> context) {
        if (context.isInLoop()) {
            super.visit(n, context);
            methodResolver.resolve(n)
                    .map((m) -> DeclationProcessor.createMethodCall(m, Optional.empty()))
                    .ifPresent(context::addStatement);
        }
    }

    boolean isStreamMethod(MethodCallExpr n) {
        return methodResolver.resolve(n)
                .map(method -> STREAM_METHOD_PATTERN.matcher(method.getQualifiedName()).matches())
                .orElse(false);
    }

    Optional<MethodCallExpr> findNonStreamMethod(MethodCallExpr n) {
        if (isStreamMethod(n)) {
            return n.getScope()
                    .filter(MethodCallExpr.class::isInstance)
                    .map(MethodCallExpr.class::cast)
                    .flatMap(this::findNonStreamMethod);
        } else {
            return Optional.of(n);
        }
    }

    List<Expression> collectStreamMethodArguments(MethodCallExpr n) {
        if (isStreamMethod(n)) {
            List<Expression> result = new ArrayList<>();
            n.getScope().filter(MethodCallExpr.class::isInstance)
                    .map(MethodCallExpr.class::cast)
                    .map(this::collectStreamMethodArguments)
                    .ifPresent(result::addAll);

            result.addAll(n.getArguments());
            return result;

        } else {
            return Collections.emptyList();
        }
    }

    String getStreamLoopCondition(MethodCallExpr n) {
        return n.getScope().map((scope) -> {
            if (scope.isMethodCallExpr()) {
                if (isStreamMethod(n)) {
                    return getStreamLoopCondition((MethodCallExpr) scope);
                } else {
                    return getStreamLoopCondition((MethodCallExpr) scope) + "." + methodCall2Str(n);
                }
            } else {
                return scope.toString() + "." + methodCall2Str(n);
            }
        }).orElseGet(() -> methodCall2Str(n));
    }

    String methodCall2Str(MethodCallExpr n) {
        return n.getName() + n.getArguments().stream().map(Object::toString)
                .collect(Collectors.joining(", ", "(", ")"));
    }

}
