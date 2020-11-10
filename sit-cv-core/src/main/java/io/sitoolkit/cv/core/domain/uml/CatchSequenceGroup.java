package io.sitoolkit.cv.core.domain.uml;

import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CatchSequenceGroup extends SequenceGroup {

  private String parameter;

  @Override
  public List<String> write(LifeLineDef lifeLine, SequenceElementWriter writer) {
    return writer.write(lifeLine, this);
  }
}
