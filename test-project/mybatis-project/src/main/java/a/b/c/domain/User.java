package a.b.c.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class User {
  private Integer id;
  private String name;
  private Integer companyId;
}
