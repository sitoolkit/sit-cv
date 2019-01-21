package io.sitoolkit.cv.core.domain.crud.jsqlparser;

import lombok.AllArgsConstructor;
import net.sf.jsqlparser.expression.ExpressionVisitorAdapter;
import net.sf.jsqlparser.statement.select.SubSelect;

@AllArgsConstructor
class SubSelectTableCollector extends ExpressionVisitorAdapter {
    SelectTableCollector selectTableCollector;

    @Override
    public void visit(SubSelect subSelect) {
        subSelect.getSelectBody().accept(selectTableCollector);
    }
}