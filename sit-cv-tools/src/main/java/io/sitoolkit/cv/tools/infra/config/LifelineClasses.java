package io.sitoolkit.cv.tools.infra.config;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class LifelineClasses {
	
    @JsonIgnore
	private FilterCondition condition;
	private boolean dbAccess = false;
	private boolean entryPoint = false;
	private boolean lifelineOnly = false;
    
    public LifelineClasses(@JsonProperty("name") String name,
            @JsonProperty("annotation") String annotation) {
    	this.condition = new FilterCondition(name, annotation);
  	}
}
