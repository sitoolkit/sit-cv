package a.b.c.app;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import a.b.c.domain.User;
import a.b.c.domain.UserRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserServiceTest {

  @Autowired UserRepository userRepository;

  UserService userService;

  @Before
  public void setUp() {
    userService = new UserService(userRepository);
  }

  @Test
  public void test() {
    User user = userService.findById(Integer.valueOf(1)).get();
    assertThat(user.getName().trim(), is("Taro Test"));

    String updateName = "Ichiro Test";
    userService.updateNameById(user.getId(), updateName);
    User updatedUser = userService.findById(user.getId()).get();
    assertThat(updatedUser.getName().trim(), is(updateName));

    Integer newUserId = Integer.valueOf(4);
    User insertUser = new User(newUserId, "Insert Test", Integer.valueOf(2));
    userService.create(insertUser);
    User insertedUser = userService.findById(newUserId).get();
    assertThat(insertedUser.getName().trim(), is("Insert Test"));

    userService.deleteById(newUserId);
    User deletedUser = userService.findById(newUserId).orElse(null);
    assertNull(deletedUser);
  }
}
