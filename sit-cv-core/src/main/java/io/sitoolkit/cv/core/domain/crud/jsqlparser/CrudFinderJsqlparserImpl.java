package io.sitoolkit.cv.core.domain.crud.jsqlparser;

import org.apache.commons.lang3.exception.ExceptionUtils;
import io.sitoolkit.cv.core.domain.crud.CrudFindResult;
import io.sitoolkit.cv.core.domain.crud.CrudFinder;
import io.sitoolkit.cv.core.domain.crud.CrudType;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.parser.TokenMgrError;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.merge.Merge;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectVisitorAdapter;
import net.sf.jsqlparser.statement.update.Update;
import net.sf.jsqlparser.util.TablesNamesFinder;

public class CrudFinderJsqlparserImpl implements CrudFinder {

    @Override
    public CrudFindResult findCrud(String sqlText) {
        CrudFindResult result = new CrudFindResult();

        try {
            Statement stmt = CCJSqlParserUtil.parse(sqlText);

            if (stmt instanceof Insert) {
                Insert insert = (Insert) stmt;
                result.put(insert.getTable().getName(), CrudType.CREATE);

                findCrudFromSelect(insert.getSelect(), result);

            } else if (stmt instanceof Select) {

                findCrudFromSelect((Select) stmt, result);

            } else if (stmt instanceof Update) {
                Update update = (Update) stmt;
                update.getTables().stream()
                        .forEach(table -> result.put(table.getName(), CrudType.UPDATE));

                if (update.getExpressions() != null) {
                    update.getExpressions().stream()
                            .forEach(expr -> findReferenceFromExpression(expr, result));
                }

                findReferenceFromExpression(update.getWhere(), result);

            } else if (stmt instanceof Delete) {
                Delete delete = (Delete) stmt;
                result.put(delete.getTable().getName(), CrudType.DELETE);

            } else if (stmt instanceof Merge) {
                Merge merge = (Merge) stmt;
                result.put(merge.getTable().getName(), CrudType.CREATE);
                result.put(merge.getTable().getName(), CrudType.UPDATE);

                findReferenceFromExpression(merge.getUsingSelect(), result);

                if (merge.getUsingTable() != null) {
                    result.put(merge.getUsingTable().getName(), CrudType.REFERENCE);
                }
            }

        } catch (Exception | TokenMgrError e) {
            result.setErrMsg(ExceptionUtils.getStackTrace(e));
        }

        return result;
    }

    void findCrudFromSelect(Select select, CrudFindResult tableCrud) {

        if (select == null) {
            return;
        }

        findReferenceFromSatement(select, tableCrud);

        select.getSelectBody().accept(new SelectVisitorAdapter() {

            @Override
            public void visit(PlainSelect plainSelect) {
                if (plainSelect.getIntoTables() != null) {
                    plainSelect.getIntoTables().stream()
                            .forEach(table -> tableCrud.put(table.getName(), CrudType.CREATE));
                }
            }

        });

    }

    void findReferenceFromSatement(Statement stmt, CrudFindResult tableCrud) {

        if (stmt == null) {
            return;
        }

        TablesNamesFinder tablesNamesFinder = new TablesNamesFinder();

        tablesNamesFinder.getTableList(stmt).stream()
                .forEach(table -> tableCrud.put(table, CrudType.REFERENCE));

    }

    void findReferenceFromExpression(Expression expr, CrudFindResult tableCrud) {

        if (expr == null) {
            return;
        }
        TablesNamesFinder tablesNamesFinder = new TablesNamesFinder();

        tablesNamesFinder.getTableList(expr).stream()
                .forEach(table -> tableCrud.put(table, CrudType.REFERENCE));

    }
}
