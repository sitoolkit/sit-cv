package io.sitoolkit.cv.core.domain.uml;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import io.sitoolkit.cv.core.domain.classdef.ClassDef;
import io.sitoolkit.cv.core.domain.classdef.ClassDefFilter;
import io.sitoolkit.cv.core.domain.classdef.CvStatement;
import io.sitoolkit.cv.core.domain.classdef.LoopStatement;
import io.sitoolkit.cv.core.domain.classdef.MethodCallDef;
import io.sitoolkit.cv.core.domain.classdef.MethodCallStack;
import io.sitoolkit.cv.core.domain.classdef.MethodDef;
import io.sitoolkit.cv.core.domain.classdef.StatementProcessor;
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
        lifeLine.setEntryMessage(method.getQualifiedSignature());
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
        message.setRequestName(methodImpl.getSignature());
        message.setRequestQualifiedSignature(methodImpl.getQualifiedSignature());
        message.setTarget(process(methodImpl.getClassDef(), methodImpl, pushedStack));
        message.setResponseName(methodCall.getReturnType().toString());

        return Optional.of(message);

    }

    @Override
    public Optional<SequenceElement> process(CvStatement statement, MethodCallStack context) {
        return Optional.empty();
    }

    @Override
    public Optional<SequenceElement> process(LoopStatement statement, MethodCallStack callStack) {

        List<SequenceElement> groupElements =  statement.getChildren().stream()
                .map(childStatement -> childStatement.process(this, callStack))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());

        if (groupElements.isEmpty()) {
            return Optional.empty();

        } else {
            SequenceGroup group = new SequenceGroup();
            group.getElements().addAll(groupElements);
            return Optional.of(group);
        }
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
    public Optional<SequenceElement> process(MethodCallDef statement) {
        return process(statement, MethodCallStack.getBlank());
    }

}
