package io.sitoolkit.cv.core.domain.crud;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorInfo {
	private String sqlText;
	private String errorMessage;
}