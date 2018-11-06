package io.sitoolkit.cv.core.domain.classdef.javaparser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.MethodReferenceExpr;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.ast.stmt.ForeachStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.WhileStmt;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.symbolsolver.javaparsermodel.JavaParserFacade;

import io.sitoolkit.cv.core.domain.classdef.CvStatement;
import io.sitoolkit.cv.core.domain.classdef.LoopStatement;
import io.sitoolkit.cv.core.domain.classdef.MethodCallDef;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StatementVisitor extends VoidVisitorAdapter<List<CvStatement>> {

    private static final Pattern STREAM_METHOD_PATTERN = Pattern
            .compile("^java\\.util\\.stream\\.Stream\\..*");

    private MethodResolver methodResolver;

    public static StatementVisitor build(JavaParserFacade jpf) {
        StatementVisitor statementVisitor = new StatementVisitor();
        statementVisitor.methodResolver = new MethodResolver(jpf);
        return statementVisitor;
    }

    @Override
    public void visit(ForeachStmt n, List<CvStatement> statements) {
        LoopStatement loop = addLoopStatement(n, statements);
        super.visit(n, loop.getChildren());
    }

    @Override
    public void visit(ForStmt n, List<CvStatement> statements) {
        LoopStatement loop = addLoopStatement(n, statements);
        super.visit(n, loop.getChildren());
    }

    @Override
    public void visit(IfStmt n, List<CvStatement> arg) {
        super.visit(n, arg);
    }

    @Override
    public void visit(WhileStmt n, List<CvStatement> arg) {
        super.visit(n, arg);
    }

    @Override
    public void visit(MethodCallExpr n, List<CvStatement> statements) {

        if (isStreamMethod(n)) {
            findNonStreamMethod(n).ifPresent(l -> l.accept(this, statements));
            LoopStatement loop = addLoopStatement(n, statements);
            getStreamMethodArguments(n).forEach(p -> p.accept(this, loop.getChildren()));

        } else {
            n.getScope().ifPresent(l -> l.accept(this, statements));
            n.getArguments().forEach(p -> p.accept(this, statements));
            addMethodCall(n, statements);
        }
    }

    @Override
    public void visit(MethodReferenceExpr n, List<CvStatement> statements) {
        super.visit(n, statements);
        addMethodCall(n, statements);
    }

    LoopStatement addLoopStatement(Node n, List<CvStatement> statements) {
        LoopStatement loop = DeclationProcessor.getLoopStatement(n);
        statements.add(loop);
        return loop;
    }

    Optional<MethodCallDef> addMethodCall(Node n, List<CvStatement> statements) {
        Optional<MethodCallDef> result = methodResolver.resolve(n).map(DeclationProcessor::getMethodCall);
        result.ifPresent(methodCall -> {
            log.debug("Add method call : {}", methodCall);
            statements.add(methodCall);
        });
        return result;
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

    List<Expression> getStreamMethodArguments(MethodCallExpr n) {
        if (isStreamMethod(n)) {
            List<Expression> result = new ArrayList<>();
            n.getScope().filter(MethodCallExpr.class::isInstance)
                    .map(MethodCallExpr.class::cast)
                    .map(this::getStreamMethodArguments)
                    .ifPresent(result::addAll);

            result.addAll(n.getArguments());
            return result;

        } else {
            return Collections.emptyList();
        }
    }
}
