package io.sitoolkit.cv.core.domain.uml;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.sitoolkit.cv.core.domain.classdef.ApiDocDef;
import io.sitoolkit.cv.core.domain.classdef.MethodDef;
import lombok.Data;

@Data
public class LifeLineDef {
    private String objectName;
    private String entryMessage;
    private String sourceId;
    private List<MessageDef> messages = new ArrayList<>();
    private List<SequenceElement> elements = new ArrayList<>();
    private ApiDocDef apiDoc;

    public Set<String> getAllSourceIds() {
        Set<String> tags = messages.stream().map(MessageDef::getTarget)
                .map(LifeLineDef::getSourceId).collect(Collectors.toSet());
        tags.add(sourceId);
        return tags;
    }

    public Stream<MessageDef> getMessagesRecursively() {
        return getElements().stream().flatMap(SequenceElement::getMessagesRecursively)
                .filter(Objects::nonNull).distinct();
    }

    public Stream<LifeLineDef> getLifeLinesRecursively() {
        Stream<LifeLineDef> stream = getMessagesRecursively().map(MessageDef::getTarget);
        return Stream.concat(Stream.of(this), stream);
    }

    public Map<String, ApiDocDef> getApiDocsRecursively() {
        return getLifeLinesRecursively()
                .collect(Collectors.toMap(LifeLineDef::getEntryMessage, LifeLineDef::getApiDoc));
    }

    public Stream<MethodDef> getSequenceMethodsRecursively() {
        return getMessagesRecursively().map(MessageDef::getMethodCall);
    }
}
