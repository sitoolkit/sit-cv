package io.sitoolkit.cv.core.domain.classdef.javaparser;

import java.util.List;

import io.sitoolkit.cv.core.domain.classdef.BranchStatement;
import io.sitoolkit.cv.core.domain.classdef.ConditionalStatement;
import io.sitoolkit.cv.core.domain.classdef.CvStatement;
import io.sitoolkit.cv.core.domain.classdef.CvStatementDefaultImpl;
import io.sitoolkit.cv.core.domain.classdef.LoopStatement;
import io.sitoolkit.cv.core.domain.classdef.MethodDef;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class VisitContext<T extends CvStatement> {

    final List<T> statements;
    final CvStatement parent;

    public static VisitContext<CvStatement> childrenOf(CvStatementDefaultImpl statement) {
        return new VisitContext<CvStatement>(statement.getChildren(), statement);
    }

    public static VisitContext<ConditionalStatement> conditionsOf(BranchStatement statement) {
        return new VisitContext<ConditionalStatement>(statement.getConditions(), statement);
    }

    public static VisitContext<CvStatement> statementsOf(MethodDef methodDef) {
        return new VisitContext<CvStatement>(methodDef.getStatements(), methodDef);
    }

    void addStatement(T statement) {
        log.debug("Add statment : {}", statement);
        statements.add(statement);
    }

    boolean isInLoop() {
        return parent instanceof LoopStatement;
    }
}
