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
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(exclude = {"entryMessage"})
@ToString(exclude = {"entryMessage"})
public class LifeLineDef {
  private String objectName;
  private MessageDef entryMessage;
  private String sourceId;
  private List<SequenceElement> elements = new ArrayList<>();
  private ApiDocDef apiDoc;

  public Set<String> getAllSourceIds() {
    Set<String> tags =
        getLifeLinesRecursively().map(LifeLineDef::getSourceId).collect(Collectors.toSet());
    tags.add(sourceId);
    return tags;
  }

  public Stream<MessageDef> getMessagesRecursively() {
    return getElements()
        .stream()
        .flatMap(SequenceElement::getMessagesRecursively)
        .filter(Objects::nonNull)
        .distinct();
  }

  public Stream<LifeLineDef> getLifeLinesRecursively() {
    Stream<LifeLineDef> stream = getMessagesRecursively().map(MessageDef::getTarget);
    return Stream.concat(Stream.of(this), stream);
  }

  public Map<String, ApiDocDef> getApiDocsRecursively() {
    return getLifeLinesRecursively()
        .collect(
            Collectors.toMap(
                (l) -> l.getEntryMessage().getRequestQualifiedSignature(),
                LifeLineDef::getApiDoc,
                (doc1, doc2) -> doc1));
  }

  public Stream<MethodDef> getSequenceMethodsRecursively() {
    return Stream.concat(
        Stream.of(entryMessage.getMethodDef()),
        getMessagesRecursively().map(MessageDef::getMethodDef));
  }
}
