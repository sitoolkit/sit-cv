package io.sitoolkit.cv.tools.infra.config;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RepositoryLoggerConfig {

    @JsonIgnore
    private String repositoryMethodMarker;
    
    private List<LifelineClasses> lifelines = new ArrayList<>();

    private String projectType;
    
    private FilterConditionGroup repositoryFilter;

	public FilterConditionGroup getRepositoryFilter() {
		if (repositoryFilter != null) {
			return repositoryFilter;
			
		} else {
			List<LifelineClasses> repositories = lifelines.stream().filter(LifelineClasses::isDbAccess)
					.collect(toList());
			return toFilterConditionGroup(repositories);
		}
	}

	private FilterConditionGroup toFilterConditionGroup(List<LifelineClasses> lifelines) {
		FilterConditionGroup fcg = new FilterConditionGroup();
		fcg.setInclude(lifelines.stream().map(LifelineClasses::getCondition).collect(toList()));
		return fcg;
	}
}
