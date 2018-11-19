package io.sitoolkit.cv.core.domain.classdef.javaparser;

import java.util.List;

import io.sitoolkit.cv.core.domain.classdef.CvStatement;
import io.sitoolkit.cv.core.domain.classdef.LoopStatement;
import io.sitoolkit.cv.core.domain.classdef.MethodDef;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class VisitContext {

    final List<CvStatement> statements;
    final CvStatement parent;

    public static VisitContext childrenOf(CvStatement statement) {
        return new VisitContext(statement.getChildren(), statement);
    }

    public static VisitContext statementsOf(MethodDef methodDef) {
        return new VisitContext(methodDef.getStatements(), methodDef);
    }

    void addStatement(CvStatement statement) {
        log.debug("Add statment : {}", statement);
        statements.add(statement);
    }

    boolean isInLoop() {
        return parent instanceof LoopStatement;
    }
}
