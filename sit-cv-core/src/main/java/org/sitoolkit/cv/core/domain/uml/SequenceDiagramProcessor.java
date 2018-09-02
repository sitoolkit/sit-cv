package org.sitoolkit.cv.core.domain.uml;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.sitoolkit.cv.core.domain.classdef.ClassDef;
import org.sitoolkit.cv.core.domain.classdef.MethodCallDef;
import org.sitoolkit.cv.core.domain.classdef.MethodDef;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SequenceDiagramProcessor {

    public List<SequenceDiagram> process(Collection<ClassDef> classDefs) {
        return classDefs.stream().map(this::process).flatMap(List::stream)
                .collect(Collectors.toList());

    }

    public List<SequenceDiagram> process(ClassDef clazz) {
        if (!clazz.getName().endsWith("Controller")) {
            return Collections.emptyList();
        }

        return clazz.getMethods().stream().map(method -> process(clazz, method))
                .map(lifeLine -> SequenceDiagram.builder().entryLifeLine(lifeLine).build())
                .collect(Collectors.toList());
    }

    public LifeLineDef process(ClassDef clazz, MethodDef method) {
        LifeLineDef lifeLine = new LifeLineDef();
        lifeLine.setSourceId(clazz.getSourceId());
        lifeLine.setEntryMessage(method.getQualifiedSignature());
        lifeLine.setObjectName(clazz.getName());
        lifeLine.setMessages(method.getMethodCalls().stream().map(this::methodCall2Message)
                .collect(Collectors.toList()));

        log.debug("Add lifeline {} -> {}", clazz.getName(), lifeLine);

        return lifeLine;
    }

    MessageDef methodCall2Message(MethodCallDef methodCall) {
        MessageDef message = new MessageDef();
        message.setName(methodCall.getSignature());
        message.setTarget(process(methodCall.getClassDef(), methodCall));

        return message;
    }
}
