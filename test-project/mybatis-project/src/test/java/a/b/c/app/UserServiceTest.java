package a.b.c.app;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import a.b.c.domain.TestBase;
import a.b.c.domain.User;

public class UserServiceTest extends TestBase {

  UserService userService;

  @Before
  public void setUp() {
    userService = new UserService();
  }

  @Test
  public void test() {
    User user = userService.selectById(Integer.valueOf(1));
    assertThat(user.getName().trim(), is("Taro Test"));

    user.setName("Ichiro Test");
    userService.updateNameById(user.getId(), "Ichiro Test");
    User updatedUser = userService.selectById(user.getId());
    assertThat(updatedUser.getName().trim(), is("Ichiro Test"));

    Integer newUserId = Integer.valueOf(4);
    User insertUser = new User(newUserId, "Insert Test", Integer.valueOf(2));
    userService.insert(insertUser);
    User insertedUser = userService.selectById(newUserId);
    assertThat(insertedUser.getName().trim(), is("Insert Test"));

    userService.deleteById(newUserId);
    User deletedUser = userService.selectById(newUserId);
    assertNull(deletedUser);
  }
}
