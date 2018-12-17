package io.sitoolkit.cv.core.domain.uml;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.sitoolkit.cv.core.domain.classdef.BranchStatement;
import io.sitoolkit.cv.core.domain.classdef.CatchStatement;
import io.sitoolkit.cv.core.domain.classdef.ClassDef;
import io.sitoolkit.cv.core.domain.classdef.ClassDefFilter;
import io.sitoolkit.cv.core.domain.classdef.ConditionalStatement;
import io.sitoolkit.cv.core.domain.classdef.CvStatement;
import io.sitoolkit.cv.core.domain.classdef.FinallyStatement;
import io.sitoolkit.cv.core.domain.classdef.LoopStatement;
import io.sitoolkit.cv.core.domain.classdef.MethodCallDef;
import io.sitoolkit.cv.core.domain.classdef.MethodCallStack;
import io.sitoolkit.cv.core.domain.classdef.MethodDef;
import io.sitoolkit.cv.core.domain.classdef.StatementProcessor;
import io.sitoolkit.cv.core.domain.classdef.TryStatement;
import io.sitoolkit.cv.core.infra.config.FilterConditionGroup;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SequenceDiagramProcessor implements StatementProcessor<SequenceElement, MethodCallStack> {

    ImplementDetector implementDetector = new ImplementDetector();

    private FilterConditionGroup classFilterGroup;

    public SequenceDiagramProcessor(FilterConditionGroup classFilterGroup) {
        this.classFilterGroup = classFilterGroup;
    }

    public LifeLineDef process(ClassDef clazz, MethodDef method) {
        return process(clazz, method, MethodCallStack.getBlank());
    }

    public LifeLineDef process(ClassDef clazz, MethodDef method, MethodCallStack callStack) {
        LifeLineDef lifeLine = new LifeLineDef();
        lifeLine.setSourceId(clazz.getSourceId());
        lifeLine.setEntryMessage(buildEntryMessage(lifeLine, method));
        lifeLine.setObjectName(clazz.getName());
        lifeLine.setElements(method.getStatements().stream()
                .map(statement -> statement.process(this, callStack))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList()));
        lifeLine.setApiDoc(method.getApiDoc());

        log.debug("Add lifeline {} -> {}", clazz.getName(), lifeLine);

        return lifeLine;
    }

    MessageDef buildEntryMessage(LifeLineDef lifeLine, MethodDef method) {
        MessageDef message = new MessageDef();
        message.setRequestName(method.getName());
        message.setRequestParamTypes(method.getParamTypes());
        message.setRequestQualifiedSignature(method.getQualifiedSignature());
        message.setTarget(lifeLine);
        message.setResponseType(method.getReturnType());

        return message;
    }

    Optional<MessageDef> methodCall2Message(MethodCallDef methodCall, MethodCallStack callStack) {

        if (methodCall.getClassDef() == null) {
            return Optional.empty();
        }

        MethodDef methodImpl = implementDetector.detectImplMethod(methodCall);

        if (!ClassDefFilter.match(methodImpl.getClassDef(), classFilterGroup)) {
            return Optional.empty();
        }

        if (callStack.contains(methodImpl)) {
            log.debug("method: {} is called recursively", methodImpl.getQualifiedSignature());
            return Optional.empty();
        }
        MethodCallStack pushedStack = callStack.push(methodImpl);

        MessageDef message = new MessageDef();
        message.setRequestName(methodImpl.getName());
        message.setRequestParamTypes(methodImpl.getParamTypes());
        message.setRequestQualifiedSignature(methodImpl.getQualifiedSignature());
        message.setTarget(process(methodImpl.getClassDef(), methodImpl, pushedStack));
        message.setResponseType(methodCall.getReturnType());
        message.setMethodCall(methodCall);

        return Optional.of(message);

    }

    @Override
    public Optional<SequenceElement> process(CvStatement statement, MethodCallStack context) {
        return Optional.empty();
    }

    @Override
    public Optional<SequenceElement> process(LoopStatement statement, MethodCallStack callStack) {

        List<SequenceElement> groupElements = statement.getChildren().stream()
                .map(childStatement -> childStatement.process(this, callStack))
                .filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList());

        if (groupElements.isEmpty()) {
            return Optional.empty();
        } else {
            LoopSequenceGroup group = new LoopSequenceGroup();
            group.getElements().addAll(groupElements);
            group.setScope(statement.getScope());
            return Optional.of(group);
        }
    }

    @Override
    public Optional<SequenceElement> process(BranchStatement statement, MethodCallStack callStack) {

        List<ConditionalSequenceGroup> conditions = statement.getConditions().stream()
                .map(childStatement -> childStatement.process(this, callStack))
                .filter(Optional::isPresent).map(Optional::get)
                .map(ConditionalSequenceGroup.class::cast).collect(Collectors.toList());

        Optional<ConditionalSequenceGroup> notEmptyCondition = conditions.stream()
                .filter((c) -> !c.getElements().isEmpty()).findAny();
        return notEmptyCondition.map((condition) -> {
            BranchSequenceElement group = new BranchSequenceElement();
            group.getConditions().addAll(conditions);
            return group;
        });
    }

    @Override
    public Optional<SequenceElement> process(ConditionalStatement statement,
            MethodCallStack callStack) {

        List<SequenceElement> groupElements = statement.getChildren().stream()
                .map(child -> child.process(this, callStack)).filter(Optional::isPresent)
                .map(Optional::get).collect(Collectors.toList());

        ConditionalSequenceGroup group = new ConditionalSequenceGroup();
        group.getElements().addAll(groupElements);
        group.setCondition(statement.getCondition());
        group.setFirst(statement.isFirst());
        return Optional.of(group);
    }

    @Override
    public Optional<SequenceElement> process(TryStatement statement, MethodCallStack callStack) {

        List<SequenceElement> groupElements = statement.getChildren().stream()
                .map(child -> child.process(this, callStack)).filter(Optional::isPresent)
                .map(Optional::get).collect(Collectors.toList());
        List<CatchSequenceGroup> catchGroups = statement.getCatchStatements().stream()
                .map(childStatement -> childStatement.process(this, callStack))
                .filter(Optional::isPresent).map(Optional::get)
                .map(CatchSequenceGroup.class::cast).collect(Collectors.toList());
        FinallySequenceGroup finallyGroup = null;
        if (statement.getFinallyStatement() != null) {
            finallyGroup = (FinallySequenceGroup) statement.getFinallyStatement().process(this, callStack).get();
        }

        Optional<? extends SequenceGroup> notEmptyGroup = Stream
                .concat(catchGroups.stream(), Stream.of(finallyGroup)).filter(Objects::nonNull)
                .filter((c) -> !c.getElements().isEmpty()).findAny();
        if (!groupElements.isEmpty() || notEmptyGroup.isPresent()) {
            TrySequenceGroup group = new TrySequenceGroup();
            group.getElements().addAll(groupElements);
            group.getCatchGroups().addAll(catchGroups);
            group.setFinallyGroup(finallyGroup);
            return Optional.of(group);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Optional<SequenceElement> process(CatchStatement statement, MethodCallStack callStack) {

        List<SequenceElement> groupElements = statement.getChildren().stream()
                .map(child -> child.process(this, callStack)).filter(Optional::isPresent)
                .map(Optional::get).collect(Collectors.toList());

        CatchSequenceGroup group = new CatchSequenceGroup();
        group.getElements().addAll(groupElements);
        group.setParameter(statement.getParameter());
        return Optional.of(group);
    }

    @Override
    public Optional<SequenceElement> process(FinallyStatement statement, MethodCallStack callStack) {

        List<SequenceElement> groupElements = statement.getChildren().stream()
                .map(child -> child.process(this, callStack)).filter(Optional::isPresent)
                .map(Optional::get).collect(Collectors.toList());

        FinallySequenceGroup group = new FinallySequenceGroup();
        group.getElements().addAll(groupElements);
        return Optional.of(group);
    }

    @Override
    public Optional<SequenceElement> process(MethodCallDef methodCall, MethodCallStack callStack) {
        Optional<MessageDef> message = methodCall2Message(methodCall, callStack);
        if (message.isPresent()) {
            return Optional.of(message.get());
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Optional<SequenceElement> process(CvStatement statement) {
        return process(statement, MethodCallStack.getBlank());
    }

    @Override
    public Optional<SequenceElement> process(LoopStatement statement) {
        return process(statement, MethodCallStack.getBlank());
    }

    @Override
    public Optional<SequenceElement> process(BranchStatement statement) {
        return process(statement, MethodCallStack.getBlank());
    }

    @Override
    public Optional<SequenceElement> process(ConditionalStatement statement) {
        return process(statement, MethodCallStack.getBlank());
    }

    @Override
    public Optional<SequenceElement> process(TryStatement statement) {
        return process(statement, MethodCallStack.getBlank());
    }

    @Override
    public Optional<SequenceElement> process(CatchStatement statement) {
        return process(statement, MethodCallStack.getBlank());
    }

    @Override
    public Optional<SequenceElement> process(FinallyStatement statement) {
        return process(statement, MethodCallStack.getBlank());
    }

    @Override
    public Optional<SequenceElement> process(MethodCallDef statement) {
        return process(statement, MethodCallStack.getBlank());
    }

}
