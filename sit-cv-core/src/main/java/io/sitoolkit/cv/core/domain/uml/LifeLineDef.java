package io.sitoolkit.cv.core.domain.uml;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.sitoolkit.cv.core.domain.classdef.ApiDocDef;
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

    public Stream<LifeLineDef> getLifeLinesRecursively() {
        Stream<LifeLineDef> stream = getElements().stream()
                .flatMap(SequenceElement::getLifeLinesRecursively).filter(Objects::nonNull);
        return Stream.concat(Stream.of(this), stream).distinct();
    }

    public Map<String, ApiDocDef> getApiDocsRecursively() {
        return getLifeLinesRecursively()
                .collect(Collectors.toMap(LifeLineDef::getEntryMessage, LifeLineDef::getApiDoc));
    }
}
