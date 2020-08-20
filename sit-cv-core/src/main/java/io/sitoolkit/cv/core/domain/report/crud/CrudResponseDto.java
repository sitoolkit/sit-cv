package io.sitoolkit.cv.core.domain.report.crud;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CrudResponseDto {
  private List<String> headers = new ArrayList<>();
  private List<Map<String, String>> rows = new ArrayList<>();
}
