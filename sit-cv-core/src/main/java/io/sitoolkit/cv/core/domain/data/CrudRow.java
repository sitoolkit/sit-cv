package io.sitoolkit.cv.core.domain.data;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import lombok.Data;


@Data
public class CrudRow {

	private String actionPath;
	private Map<TableDef, Set<CrudType>> cellMap = new HashMap<>();
	private Map<TableDef, Set<String>> sqlTextMap = new HashMap<>();
	private Set<String> repositoryFunctions = new HashSet<>();

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

}
