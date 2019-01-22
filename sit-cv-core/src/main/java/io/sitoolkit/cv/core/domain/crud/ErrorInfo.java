package io.sitoolkit.cv.core.domain.crud;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorInfo {
	private String sqlText;
	private String errorMessage;
}