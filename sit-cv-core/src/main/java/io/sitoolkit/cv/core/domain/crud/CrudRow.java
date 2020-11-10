package io.sitoolkit.cv.core.domain.crud;

import io.sitoolkit.cv.core.domain.tabledef.TableDef;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class CrudRow {

  private String actionPath;
  private Map<TableDef, Set<CrudType>> cellMap = new HashMap<>();
  private Map<TableDef, Set<String>> sqlTextMap = new HashMap<>();
  private List<String> repositoryFunctions = new ArrayList<>();

  public CrudRow(String actionPath) {
    super();
    this.actionPath = actionPath;
  }

  public void add(TableDef table, CrudType type, String sqlText) {
    Set<CrudType> types = cellMap.computeIfAbsent(table, key -> new HashSet<>());
    types.add(type);
    Set<String> sqls = sqlTextMap.computeIfAbsent(table, key -> new HashSet<>());
    sqls.add(sqlText);
  }

  public CrudRow merge(CrudRow mergingCrud) {

    mergingCrud.getCellMap().entrySet().stream()
        .forEach(
            cellMapEntry -> {
              Set<CrudType> existingSet =
                  cellMap.computeIfAbsent(cellMapEntry.getKey(), key -> new HashSet<>());
              existingSet.addAll(cellMapEntry.getValue());
            });

    mergingCrud.getSqlTextMap().entrySet().stream()
        .forEach(
            sqlTextMapEntry -> {
              Set<String> existingSet =
                  sqlTextMap.computeIfAbsent(sqlTextMapEntry.getKey(), key -> new HashSet<>());
              existingSet.addAll(sqlTextMapEntry.getValue());
            });

    repositoryFunctions.addAll(mergingCrud.getRepositoryFunctions());

    return mergingCrud;
  }
}
