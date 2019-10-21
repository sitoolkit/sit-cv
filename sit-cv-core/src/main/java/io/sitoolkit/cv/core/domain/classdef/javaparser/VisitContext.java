package io.sitoolkit.cv.core.domain.classdef.javaparser;

import java.util.Stack;

import org.apache.commons.lang3.StringUtils;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.stmt.CatchClause;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.stmt.TryStmt;

import io.sitoolkit.cv.core.domain.classdef.BranchStatement;
import io.sitoolkit.cv.core.domain.classdef.CatchStatement;
import io.sitoolkit.cv.core.domain.classdef.ConditionalStatement;
import io.sitoolkit.cv.core.domain.classdef.CvStatement;
import io.sitoolkit.cv.core.domain.classdef.CvStatementDefaultImpl;
import io.sitoolkit.cv.core.domain.classdef.FinallyStatement;
import io.sitoolkit.cv.core.domain.classdef.LoopStatement;
import io.sitoolkit.cv.core.domain.classdef.MethodCallDef;
import io.sitoolkit.cv.core.domain.classdef.MethodDef;
import io.sitoolkit.cv.core.domain.classdef.TryStatement;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class VisitContext {

    private Stack<CvStatement> stack = new Stack<>();

    public static VisitContext of(CvStatement current) {
        VisitContext context = new VisitContext();
        context.startContext(current);
        return context;
    }

    public void startLoopContext(Node node, String scope) {
        LoopStatement loopStatement = DeclationProcessor.createLoopStatement(node, scope);
        if (!stack.isEmpty()) {
            addChild(getCurrent(), loopStatement);
        }
        startContext(loopStatement);
    }

    public void startBranchContext(IfStmt ifStmt) {
        BranchStatement branchStatement = DeclationProcessor.createBranchStatement(ifStmt);
        if (!stack.isEmpty()) {
            addChild(getCurrent(), branchStatement);
        }
        startContext(branchStatement);
    }

    public void addConditionalContext(Statement statement, String condition, boolean isFirst) {
        ConditionalStatement conditionalStatement = DeclationProcessor
                .createConditionalStatement(statement, condition, isFirst);
        addChild(getCurrentBranch(), conditionalStatement);
        startContext(conditionalStatement);
    }

    public void startTryContext(TryStmt tryStmt) {
        TryStatement tryStatement = DeclationProcessor.createTryStatement(tryStmt);
        if (!stack.isEmpty()) {
            addChild(getCurrent(), tryStatement);
        }
        startContext(tryStatement);
    }

    public void startCatchContext(CatchClause catchClause, String parameter) {
        CatchStatement catchStatement = DeclationProcessor.createCatchStatement(catchClause, parameter);
        if (!stack.isEmpty()) {
            addChild(getCurrent(), catchStatement);
        }
        startContext(catchStatement);
    }

    public void startFinallyContext(Statement stmt) {
        FinallyStatement finallyStatement = DeclationProcessor.createFinallyStatement(stmt);
        if (!stack.isEmpty()) {
            addChild(getCurrent(), finallyStatement);
        }
        startContext(finallyStatement);
    }

    public void startContext(CvStatement startingStatement) {
        stack.push(startingStatement);
        log.debug("{}Start context : {}", getLogLeftPadding(), startingStatement);
    }

    public void endContext() {
        CvStatement endingStatement = stack.pop();
        log.debug("{}End context : {}", getLogLeftPadding(), endingStatement);
    }

    public CvStatement getCurrent() {
        return stack.peek();
    }

    public CvStatement getCurrentBranch() {
        return stack.stream().filter(BranchStatement.class::isInstance)
                .reduce((first, second) -> second).get();
    }

    public CvStatement getCurrentMethod() {
        return stack.stream().filter(MethodDef.class::isInstance)
                .reduce((first, second) -> second).get();
    }

    public void addMethodCall(MethodCallDef methodCallDef) {
        log.debug("{}Add MethodCall {} to {}", getLogLeftPadding(), methodCallDef, getCurrent());
        CvStatement currentMethod = getCurrentMethod();
        if(currentMethod != null) {
            ((MethodDef) currentMethod).getMethodCalls().add(methodCallDef);
        }
        addChild(getCurrent(), methodCallDef);
    }

    public void addThrowExpression(String throwExpr) {
        log.debug("{}Add ThrowExpression {} to {}", getLogLeftPadding(), throwExpr, getCurrent());
        CvStatement currentMethod = getCurrentMethod();
        if (currentMethod != null) {
            ((MethodDef) currentMethod).getExceptions().add(throwExpr);
        }
    }

    public boolean isInLoop() {
        return getCurrent() instanceof LoopStatement;
    }

    public String getLogLeftPadding() {
        return StringUtils.repeat("-", stack.size()) + " ";
    }

    private void addChild(CvStatement parent, CvStatement child) {
        if (parent instanceof TryStatement && child instanceof CatchStatement) {
            ((TryStatement) parent).getCatchStatements().add((CatchStatement) child);
        } else if (parent instanceof TryStatement && child instanceof FinallyStatement) {
            ((TryStatement) parent).setFinallyStatement((FinallyStatement) child);
        } else if (parent instanceof CvStatementDefaultImpl) {
            ((CvStatementDefaultImpl) parent).getChildren().add(child);
        } else if (parent instanceof MethodDef) {
            ((MethodDef) parent).getStatements().add(child);
        } else if (parent instanceof BranchStatement && child instanceof ConditionalStatement) {
            ((BranchStatement) parent).getConditions().add((ConditionalStatement) child);
        } else {
            log.warn("Illegal operation for {}", parent);
        }
    }

}
