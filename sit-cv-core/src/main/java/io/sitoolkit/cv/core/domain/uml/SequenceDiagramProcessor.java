package io.sitoolkit.cv.core.domain.uml;

import java.util.Optional;
import java.util.stream.Collectors;

import io.sitoolkit.cv.core.domain.classdef.ClassDef;
import io.sitoolkit.cv.core.domain.classdef.MethodCallDef;
import io.sitoolkit.cv.core.domain.classdef.MethodDef;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SequenceDiagramProcessor {

    public LifeLineDef process(ClassDef clazz, MethodDef method) {
        LifeLineDef lifeLine = new LifeLineDef();
        lifeLine.setSourceId(clazz.getSourceId());
        lifeLine.setEntryMessage(method.getQualifiedSignature());
        lifeLine.setObjectName(clazz.getName());
        lifeLine.setMessages(method.getMethodCalls().stream().map(this::methodCall2Message)
                .filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList()));

        log.debug("Add lifeline {} -> {}", clazz.getName(), lifeLine);

        return lifeLine;
    }

    Optional<MessageDef> methodCall2Message(MethodCallDef methodCall) {
        if (methodCall.getClassDef() == null) {
            return Optional.empty();
        }
        MessageDef message = new MessageDef();
        message.setName(methodCall.getSignature());
        message.setTarget(process(methodCall.getClassDef(), methodCall));

        return Optional.of(message);
    }
}
