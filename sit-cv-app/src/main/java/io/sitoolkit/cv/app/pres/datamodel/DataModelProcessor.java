package io.sitoolkit.cv.app.pres.datamodel;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import io.sitoolkit.cv.core.domain.crud.CrudMatrix;
import io.sitoolkit.cv.core.domain.crud.CrudType;
import io.sitoolkit.cv.core.infra.util.SignatureParser;

@Component
public class DataModelProcessor {

  private static final String HEADER_NAME_PACKAGE = "package";
  private static final String HEADER_NAME_FUNCTION = "function";

  public CrudResponseDto entity2dto(CrudMatrix entity) {
    CrudResponseDto dto = new CrudResponseDto();

    dto.getHeaders().add(HEADER_NAME_PACKAGE);
    dto.getHeaders().add(HEADER_NAME_FUNCTION);

    entity.getTableDefs().stream().forEach(table -> dto.getHeaders().add(table.getName()));

    entity.getCrudRowMap().forEach((function, crudRow) -> {

      Map<String, String> row = new HashMap<>();
      dto.getRows().add(row);

      SignatureParser parser = SignatureParser.parse(function);

      row.put(HEADER_NAME_FUNCTION, parser.getSimpleMedhod());
      row.put(HEADER_NAME_PACKAGE, parser.getPackageName());

      entity.getTableDefs().stream().forEach(table -> {

        String crudTypes = crudRow.getCellMap().getOrDefault(table, Collections.emptySet()).stream()
            .sorted().map(CrudType::toString).collect(Collectors.joining(","));

        row.put(table.getName(), crudTypes);

      });
    });

    return dto;
  }
}
