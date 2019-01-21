package io.sitoolkit.cv.core.domain.crud.jsqlparser;

import java.util.Set;

import lombok.AllArgsConstructor;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.FromItemVisitorAdapter;

@AllArgsConstructor
class FromTableCollector extends FromItemVisitorAdapter {
    Set<String> tables;

    @Override
    public void visit(Table table) {
        tables.add(table.getName());
    }
}