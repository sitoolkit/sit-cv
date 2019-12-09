package a.b.c.app;

import org.apache.ibatis.session.SqlSession;

import a.b.c.domain.User;
import a.b.c.domain.mapper.UserMapper;
import a.b.c.infra.Connector;

public class UserService {

  private SqlSession sqlSession;
  private UserMapper userMapper;

  public UserService() {
    this.sqlSession = Connector.getSqlSession();
    this.userMapper = sqlSession.getMapper(UserMapper.class);
  }

  public User selectById(Integer id) {
    return userMapper.selectById(id);
  }

  public Integer updateNameById(Integer id, String name) {
    int ret = userMapper.updateNameById(id, name);
    if (ret > 0) {
      sqlSession.commit();
    }
    return ret;
  }

  public Integer insert(User user) {
    int ret = userMapper.insert(user.getId(), user.getName(), user.getCompanyId());
    if (ret > 0) {
      sqlSession.commit();
    }
    return ret;
  }

  public Integer deleteById(Integer id) {
    int ret = userMapper.deleteById(id);
    if (ret > 0) {
      sqlSession.commit();
    }
    return ret;
  }
}
