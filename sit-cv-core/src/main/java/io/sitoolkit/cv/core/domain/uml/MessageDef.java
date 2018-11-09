package io.sitoolkit.cv.core.domain.uml;

import java.util.List;
import java.util.stream.Stream;

import io.sitoolkit.cv.core.domain.classdef.MethodCallDef;
import lombok.Data;

@Data
public class MessageDef extends SequenceElement {
    private MessageType type = MessageType.SYNC;
    private String requestName;
    private String requestQualifiedSignature;
    private LifeLineDef target;
    private String responseName;
    private MethodCallDef methodCall;

    @Override
    public List<String> write(LifeLineDef lifeLine, SequenceElementWriter writer) {
        return writer.write(lifeLine, this);
    }

    @Override
    public Stream<MessageDef> getMessagesRecursively() {
        Stream<MessageDef> messages = getTarget().getMessagesRecursively();
        return Stream.concat(Stream.of(this), messages);
    }
}
