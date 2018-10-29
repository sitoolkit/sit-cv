package io.sitoolkit.cv.core.domain.classdef.javaparser;

import java.util.List;
import java.util.regex.Pattern;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.LambdaExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.MethodReferenceExpr;
import com.github.javaparser.ast.expr.TypeExpr;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.ast.stmt.ForeachStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.WhileStmt;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.github.javaparser.symbolsolver.javaparsermodel.JavaParserFacade;
import com.github.javaparser.symbolsolver.model.resolution.SymbolReference;

import io.sitoolkit.cv.core.domain.classdef.CvStatement;
import io.sitoolkit.cv.core.domain.classdef.LoopStatement;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StatementVisitor extends VoidVisitorAdapter<List<CvStatement>> {

    private static final Pattern STREAM_METHOD_PATTERN = Pattern
            .compile("^java\\.util\\.stream\\.Stream\\..*");

    private JavaParserFacade jpf;
    private MethodCallVisitor methodCallVisitor;

    public static StatementVisitor build(JavaParserFacade jpf) {
        StatementVisitor statementVisitor = new StatementVisitor();
        statementVisitor.jpf = jpf;
        statementVisitor.methodCallVisitor = new MethodCallVisitor(jpf);

        return statementVisitor;
    }

    @Override
    public void visit(ForeachStmt n, List<CvStatement> statements) {
        findMethodCallInLoop(n, statements);
    }

    @Override
    public void visit(ForStmt n, List<CvStatement> statements) {
        findMethodCallInLoop(n, statements);
    }

    @Override
    public void visit(IfStmt n, List<CvStatement> arg) {
        // TODO Auto-generated method stub
        super.visit(n, arg);
    }

    @Override
    public void visit(MethodCallExpr n, List<CvStatement> statements) {

        if (matchesQualifiedName(n, STREAM_METHOD_PATTERN)) {
            visitStream(n, statements);
        } else {
            methodCallVisitor.visit(n, statements);
        }
    }

    @Override
    public void visit(WhileStmt n, List<CvStatement> arg) {
        // TODO Auto-generated method stub
        super.visit(n, arg);
    }

    void findMethodCallInLoop(Node n, List<CvStatement> statements) {
        LoopStatement statement = new LoopStatement();
        statements.add(statement);
        statement.setBody(n.toString());
        n.getChildNodes().stream().forEach(child -> child.accept(this, statement.getChildren()));
    }

    boolean matchesQualifiedName(MethodCallExpr n, Pattern pattern) {
        SymbolReference<ResolvedMethodDeclaration> ref = jpf.solve(n);

        if (!ref.isSolved()) {
            return false;
        }

        ResolvedMethodDeclaration rmd = ref.getCorrespondingDeclaration();
        return pattern.matcher(rmd.getQualifiedName()).matches();
    }

    void visitStream(MethodCallExpr n, List<CvStatement> statements) {

        n.getChildNodes().stream().forEach(childNode -> {
            if (childNode instanceof LambdaExpr) {
                System.out.println(jpf.solve((LambdaExpr) childNode));
            } else if (childNode instanceof MethodReferenceExpr) {
                MethodReferenceExpr m = (MethodReferenceExpr) childNode;
                TypeExpr t = (TypeExpr) m.getScope();
                System.out.println(jpf.convertToUsage(t.getType()));
                ;
            }
        });
    }

    boolean inLoop(Node node) {
        if (!node.getParentNode().isPresent()) {
            return false;
        }
        Node parent = node.getParentNode().get();

        if (parent instanceof ForStmt || parent instanceof ForeachStmt) {
            return true;
        } else if (parent instanceof MethodCallExpr) {
            return matchesQualifiedName((MethodCallExpr) parent, STREAM_METHOD_PATTERN);
        } else if (parent instanceof MethodDeclaration) {
            return false;
        } else {
            return inLoop(parent);
        }
    }

}
