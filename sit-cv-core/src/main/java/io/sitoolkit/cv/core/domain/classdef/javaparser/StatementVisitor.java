package io.sitoolkit.cv.core.domain.classdef.javaparser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

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
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class StatementVisitor extends VoidVisitorAdapter<VisitContext> {

    private static final Pattern STREAM_METHOD_PATTERN = Pattern
            .compile("^java\\.util\\.stream\\.Stream\\..*");

    private final MethodResolver methodResolver;

    public static StatementVisitor build(JavaParserFacade jpf) {
        MethodResolver methodResolver = new MethodResolver(jpf);
        return new StatementVisitor(methodResolver);
    }

    @Override
    public void visit(ForeachStmt n, VisitContext context) {
        CvStatement statement = DeclationProcessor.createLoopStatement(n);
        context.addStatement(statement);
        super.visit(n, VisitContext.childrenOf(statement));
    }

    @Override
    public void visit(ForStmt n, VisitContext context) {
        CvStatement statement = DeclationProcessor.createLoopStatement(n);
        context.addStatement(statement);
        super.visit(n, VisitContext.childrenOf(statement));
    }

    @Override
    public void visit(IfStmt n, VisitContext context) {
        VisitContext childContext;
        if (context.parent instanceof BranchStatement) {
            childContext = context;
        } else {
            CvStatement statement = DeclationProcessor.createBranchStatement(n);
            context.addStatement(statement);
            childContext = VisitContext.childrenOf(statement);
        }
        visitIfStmt(n, childContext);
    }

    public void visitIfStmt(IfStmt n, VisitContext context) {
        ConditionalStatement statement = DeclationProcessor.createConditionalStatement(n);
        if (context.statements.size() == 0) {
            statement.setStart(true);
        }
        context.addStatement(statement);
        VisitContext childContext = VisitContext.childrenOf(statement);
        n.getThenStmt().accept(this, childContext);
        n.getElseStmt().ifPresent(l -> {
            if (l instanceof IfStmt) {
                l.accept(this, context);
            } else {
                ConditionalStatement elseStatement = DeclationProcessor.createConditionalStatement(l);
                context.addStatement(elseStatement);
                VisitContext elseContext = VisitContext.childrenOf(elseStatement);
                l.accept(this, elseContext);
            }
        });
    }

    @Override
    public void visit(WhileStmt n, VisitContext context) {
        super.visit(n, context);
    }

    @Override
    public void visit(MethodCallExpr n, VisitContext context) {

        if (isStreamMethod(n)) {
            findNonStreamMethod(n).ifPresent(l -> l.accept(this, context));
            CvStatement statement = DeclationProcessor.createLoopStatement(n);
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
    public void visit(LambdaExpr n, VisitContext context) {
        if (context.isInLoop()) {
            super.visit(n, context);
        }
    }
    @Override
    public void visit(MethodReferenceExpr n, VisitContext context) {
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

}
