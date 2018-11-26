package io.sitoolkit.cv.core.domain.classdef.javaparser;

import java.util.Stack;

import org.apache.commons.lang3.StringUtils;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.Statement;

import io.sitoolkit.cv.core.domain.classdef.BranchStatement;
import io.sitoolkit.cv.core.domain.classdef.ConditionalStatement;
import io.sitoolkit.cv.core.domain.classdef.CvStatement;
import io.sitoolkit.cv.core.domain.classdef.CvStatementDefaultImpl;
import io.sitoolkit.cv.core.domain.classdef.LoopStatement;
import io.sitoolkit.cv.core.domain.classdef.MethodDef;
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

    public void addStatement(CvStatement statement) {
        log.debug("{}Add statment {} to {}", getLogLeftPadding(), statement, getCurrent());
        addChild(getCurrent(), statement);
    }

    public boolean isInLoop() {
        return getCurrent() instanceof LoopStatement;
    }

    public String getLogLeftPadding() {
        return StringUtils.repeat("-", stack.size()) + " ";
    }

    private void addChild(CvStatement parent, CvStatement child) {
        if (parent instanceof CvStatementDefaultImpl) {
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
