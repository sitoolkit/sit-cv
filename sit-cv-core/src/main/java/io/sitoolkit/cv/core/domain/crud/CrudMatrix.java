package io.sitoolkit.cv.core.domain.crud;

import java.util.Map;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import io.sitoolkit.cv.core.domain.tabledef.TableDef;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
public class CrudMatrix {

    /**
     * key: function
     */
    private Map<String, CrudRow> crudRowMap = new TreeMap<>();

    private SortedSet<TableDef> tableDefs = new TreeSet<>();

    public void add(String function, TableDef table, CrudType type, String sqlText) {

        log.debug("{}, {}, {}", function, table.getName(), type);

        CrudRow row = crudRowMap.computeIfAbsent(function, CrudRow::new);
        row.add(table, type, sqlText);
    }

}

