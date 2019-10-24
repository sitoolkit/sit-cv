package io.sitoolkit.cv.core.infra.config;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
public class LifelineClasses {
	
	@JsonIgnore
	private FilterCondition condition = new FilterCondition();
	private boolean entryPoint = false;
	private boolean dbAccess = false;
	private boolean lifelineOnly = false;
	private boolean exclude = false;
	
	public String getName() {
		return condition.getName();
	}
	public void setName(String name) {
		condition.setName(name);
	}
	public String getAnnotation() {
		return condition.getAnnotation();
	}
	public void setAnnotation(String annotation) {
		condition.setAnnotation(annotation);
	}

}
