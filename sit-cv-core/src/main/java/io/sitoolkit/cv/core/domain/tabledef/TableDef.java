package io.sitoolkit.cv.core.domain.tabledef;

import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Data;

@Data
public class TableDef implements Comparable<TableDef> {

  @JsonValue private String name;

  @Override
  public int compareTo(TableDef o) {
    return this.getName().compareTo(o.getName());
  }

  public TableDef(String name) {
    super();
    this.name = name;
  }
}
