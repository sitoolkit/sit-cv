package io.sitoolkit.cv.core.domain.crud;

import java.util.List;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;

import io.sitoolkit.cv.core.domain.classdef.ClassDef;
import io.sitoolkit.cv.core.domain.classdef.MethodDef;
import io.sitoolkit.cv.core.domain.tabledef.TableDef;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
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
              .forEach(crud -> matrix.add(sqlPerMethod.getRepositoryMethod(), new TableDef(table),
                  crud, sqlPerMethod.getSqlText())));
    });

    return matrix;
  }

  public CrudMatrix adjustAxis(List<ClassDef> entryPoints, CrudMatrix repositoryMethodMatrix) {

    CrudMatrix result = new CrudMatrix();

    Stream<MethodDef> entryPointMethods = entryPoints.stream().map(ClassDef::getMethods)
        .flatMap(List::stream);

    entryPointMethods.forEach(entryPointMethod -> {
      Stream<MethodDef> implMethods = entryPointMethod.collectCalledMethodsRecursively();

      implMethods.forEach(methodCalledByEntryPoint -> {
        CrudRow repositoryMethodCrudRow = repositoryMethodMatrix.getCrudRowMap()
            .get(methodCalledByEntryPoint.getQualifiedSignature());

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
            methodCalledByEntryPoint.getQualifiedSignature(), entryPointMethodCrudRow.getCellMap());
      });
    });

    return result;
  }

}
