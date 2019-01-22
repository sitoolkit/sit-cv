package io.sitoolkit.cv.core.domain.crud;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import io.sitoolkit.cv.core.domain.tabledef.TableDef;
import lombok.AllArgsConstructor;
import lombok.NonNull;

@AllArgsConstructor
public class CrudProcessor {

    @NonNull
    CrudFinder crudFinder;

    public CrudMatrix buildMatrix(List<SqlPerMethod> sqlPerMethodList) {
        CrudMatrix matrix = new CrudMatrix();

        sqlPerMethodList.stream().forEach(sqlPerMethod -> {

            if (StringUtils.isEmpty(sqlPerMethod.getSqlText())) {
                return;
            }

            CrudFindResult result = crudFinder.findCrud(sqlPerMethod.getSqlText());

            if (result.isError()) {
                matrix.addError(sqlPerMethod.getRepositoryMethod(), sqlPerMethod.getSqlText(),
                        result.getErrMsg());
            }

            result.getMap().keySet().stream()
                    .forEach(table -> result.getMap().get(table).stream()
                            .forEach(crud -> matrix.add(sqlPerMethod.getRepositoryMethod(),
                                    new TableDef(table), crud, sqlPerMethod.getSqlText())));
        });

        return matrix;
    }

}
