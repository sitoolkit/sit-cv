package io.sitoolkit.cv.core.domain.uml;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class BranchSequenceElement extends SequenceElement {

  private List<ConditionalSequenceGroup> conditions = new ArrayList<>();

  @Override
  public List<String> write(LifeLineDef lifeLine, SequenceElementWriter writer) {
    return writer.write(lifeLine, this);
  }

  @Override
  public Stream<MessageDef> getMessagesRecursively() {
    return getConditions().stream()
        .flatMap(SequenceElement::getMessagesRecursively)
        .filter(Objects::nonNull)
        .distinct();
  }
}
