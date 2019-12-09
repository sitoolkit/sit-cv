package a.b.c.infra;

import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Connector {

  @Getter private static SqlSession sqlSession;

  static {
    try (Reader reader = Resources.getResourceAsReader("myBatisConfig.xml")) {
      sqlSession = new SqlSessionFactoryBuilder().build(reader).openSession();
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
