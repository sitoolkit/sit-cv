package io.sitoolkit.cv.core.domain.data;

import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Data;
import lombok.Getter;

@Data
public class TableDef implements Comparable<TableDef> {

    @JsonValue
	private String name;

	@Getter
	private String nameToFind;

	@Override
	public int compareTo(TableDef o) {
		return this.getName().compareTo(o.getName());
	}

	public TableDef(String name) {
		super();
		this.name = name;
		this.nameToFind = " " + name.toLowerCase() + " ";
	}
}
