package io.sitoolkit.cv.core.infra.config;

import java.util.List;
import lombok.Data;

@Data
public class FilterConditionGroup {

  private List<FilterCondition> include;
  private List<FilterCondition> exclude;
}
