package a.b.c.domain;

import java.util.Optional;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface UserRepository {

  @Select("SELECT * from \"user\" where ID = #{id}")
  Optional<User> findById(Integer id);

  @Insert(
      "INSERT INTO "
          + "\"user\""
          + " (id, name, companyId) "
          + "VALUES "
          + "(#{id}, #{name}, #{companyId})")
  void create(User user);

  @Update("UPDATE \"user\" SET name = #{name} WHERE id = #{id}")
  boolean updateNameById(@Param("id") Integer id, @Param("name") String name);

  @Delete("DELETE FROM \"user\" WHERE id = #{id}")
  void deleteById(Integer id);
}
