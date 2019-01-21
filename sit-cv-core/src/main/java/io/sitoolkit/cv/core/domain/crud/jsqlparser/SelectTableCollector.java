package io.sitoolkit.cv.core.domain.crud.jsqlparser;

import java.util.HashSet;
import java.util.Set;

import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.SelectVisitorAdapter;
import net.sf.jsqlparser.statement.select.WithItem;

class SelectTableCollector extends SelectVisitorAdapter {

    Set<String> tables = new HashSet<>();
    Set<String> intoTables = new HashSet<>();
    FromTableCollector fromTableCollector = new FromTableCollector(tables);
    SubSelectTableCollector subSelectTableCollector = new SubSelectTableCollector(this);

    @Override
    public void visit(PlainSelect plainSelect) {
        plainSelect.getFromItem().accept(fromTableCollector);

        if (plainSelect.getJoins() != null) {
            plainSelect.getJoins().stream().forEach(join -> {
                join.getRightItem().accept(fromTableCollector);
            });
        }

        if (plainSelect.getWhere() != null) {
            plainSelect.getWhere().accept(subSelectTableCollector);
        }

        if (plainSelect.getIntoTables() != null) {
            plainSelect.getIntoTables().stream().forEach(table -> intoTables.add(table.getName()));
        }
    }

    @Override
    public void visit(WithItem withItem) {
        withItem.getSelectBody().accept(this);
        tables.remove(withItem.getName());
    }
}