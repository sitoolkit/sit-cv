package io.sitoolkit.cv.tools.config;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RepositoryFilterConditionGroup {

    private List<RepositoryFilterCondition> include;
    private List<RepositoryFilterCondition> exclude;

}
