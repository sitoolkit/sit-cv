package a.b.c.domain;

import lombok.Data;

@Data
public class User {
  private Integer id;
  private String name;
  private Integer companyId;

  public User(Integer id, String name, Integer companyId) {
    super();
    this.id = id;
    this.name = name;
    this.companyId = companyId;
  }
}
