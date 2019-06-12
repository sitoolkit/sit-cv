package io.sitoolkit.cv.tools.infra.config;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FilterConditionGroup {

    private List<FilterCondition> include;
    private List<FilterCondition> exclude;

}
