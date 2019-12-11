package io.sitoolkit.cv.core.domain.uml;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public abstract class SequenceGroup extends SequenceElement {

  private List<SequenceElement> elements = new ArrayList<>();

  @Override
  public Stream<MessageDef> getMessagesRecursively() {
    return getElements()
        .stream()
        .flatMap(SequenceElement::getMessagesRecursively)
        .filter(Objects::nonNull)
        .distinct();
  }
}
