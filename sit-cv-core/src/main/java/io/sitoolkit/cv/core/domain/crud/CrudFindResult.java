package io.sitoolkit.cv.core.domain.crud;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CrudFindResult {
  /** key:table */
  private Map<String, Set<CrudType>> map = new HashMap<>();

  private String errMsg;

  public void put(String table, CrudType crud) {
    Set<CrudType> cruds = map.computeIfAbsent(table, key -> new HashSet<>());
    cruds.add(crud);
  }

  public boolean isError() {
    return StringUtils.isNotEmpty(errMsg);
  }
}
