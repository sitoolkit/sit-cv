package io.sitoolkit.cv.core.domain.crud;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SqlPerMethod {
    private String repositoryMethod;
    private String sqlText;
}
