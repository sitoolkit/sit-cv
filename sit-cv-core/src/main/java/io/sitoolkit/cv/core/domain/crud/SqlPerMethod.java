package io.sitoolkit.cv.core.domain.crud;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SqlPerMethod {
    private String repositoryMethod;
    private String sqlText;
}
