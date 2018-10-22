package io.sitoolkit.cv.core.domain.uml;

import java.util.Optional;
import java.util.stream.Collectors;

import io.sitoolkit.cv.core.domain.classdef.ClassDef;
import io.sitoolkit.cv.core.domain.classdef.ClassDefFilter;
import io.sitoolkit.cv.core.domain.classdef.MethodCallDef;
import io.sitoolkit.cv.core.domain.classdef.MethodDef;
import io.sitoolkit.cv.core.infra.config.FilterConditionGroup;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SequenceDiagramProcessor {

    ImplementDetector implementDetector = new ImplementDetector();

    private FilterConditionGroup classFilterGroup;

    public SequenceDiagramProcessor(FilterConditionGroup classFilterGroup) {
        this.classFilterGroup = classFilterGroup;
    }

    public LifeLineDef process(ClassDef clazz, MethodDef method) {
        LifeLineDef lifeLine = new LifeLineDef();
        lifeLine.setSourceId(clazz.getSourceId());
        lifeLine.setEntryMessage(method.getQualifiedSignature());
        lifeLine.setObjectName(clazz.getName());
        lifeLine.setMessages(method.getMethodCalls().stream().map(this::methodCall2Message)
                .filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList()));
        lifeLine.setComment(method.getComment());

        log.debug("Add lifeline {} -> {}", clazz.getName(), lifeLine);

        return lifeLine;
    }

    Optional<MessageDef> methodCall2Message(MethodCallDef methodCall) {

        if (methodCall.getClassDef() == null) {
            return Optional.empty();
        }

        MethodDef methodImpl = implementDetector.detectImplMethod(methodCall);

        if (!ClassDefFilter.match(methodImpl.getClassDef(), classFilterGroup)) {
            return Optional.empty();
        }

        MessageDef message = new MessageDef();
        message.setRequestName(methodImpl.getSignature());
        message.setRequestQualifiedSignature(methodImpl.getQualifiedSignature());
        message.setTarget(process(methodImpl.getClassDef(), methodImpl));
        message.setResponseName(methodCall.getReturnType().toString());

        return Optional.of(message);

    }

}
