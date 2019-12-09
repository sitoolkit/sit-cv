package a.b.c.domain.mapper;

import org.apache.ibatis.annotations.Param;

import a.b.c.domain.User;

public interface UserMapper {
  User selectById(Integer id);

  Integer updateNameById(@Param("id") Integer id, @Param("name") String name);

  Integer insert(
      @Param("id") Integer id, @Param("name") String name, @Param("companyId") Integer companyId);

  Integer deleteById(Integer id);
}
