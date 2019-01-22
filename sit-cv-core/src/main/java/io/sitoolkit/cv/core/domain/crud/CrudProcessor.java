package io.sitoolkit.cv.core.domain.crud;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import io.sitoolkit.cv.core.domain.classdef.ClassDef;
import io.sitoolkit.cv.core.domain.classdef.MethodDef;
import io.sitoolkit.cv.core.domain.tabledef.TableDef;
import io.sitoolkit.cv.core.domain.uml.ImplementDetector;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class CrudProcessor {

    ImplementDetector implementDetector = new ImplementDetector();

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

    public CrudMatrix adjustAxis(List<ClassDef> entryPoints, CrudMatrix repositoryMethodMatrix) {

        CrudMatrix result = new CrudMatrix();

        entryPoints.stream().forEach(entryPoint -> {
            entryPoint.getMethods().stream().forEach(entryPointMethod -> {
                entryPointMethod.getMethodCallsRecursively().forEach(methodCalledByEntryPoint -> {
                    MethodDef implMethod = implementDetector
                            .detectImplMethod(methodCalledByEntryPoint);
                    CrudRow repositoryMethodCrudRow = repositoryMethodMatrix.getCrudRowMap()
                            .get(implMethod.getQualifiedSignature());

                    if (repositoryMethodCrudRow == null) {
                        return;
                    }

                    CrudRow entryPointMethodCrudRow = result.getCrudRowMap()
                            .computeIfAbsent(entryPointMethod.getQualifiedSignature(), (key) -> {
                                return new CrudRow(entryPointMethod.getActionPath());
                            });
                    entryPointMethodCrudRow.merge(repositoryMethodCrudRow);

                    result.getTableDefs().addAll(repositoryMethodCrudRow.getCellMap().keySet());

                    log.debug("Mapped {} -> {} : {}", entryPointMethod.getQualifiedSignature(),
                            implMethod.getQualifiedSignature(),
                            entryPointMethodCrudRow.getCellMap());
                });
            });
        });

        return result;
    }

}
