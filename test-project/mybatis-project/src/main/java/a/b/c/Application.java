package a.b.c;

import a.b.c.app.UserService;
import a.b.c.domain.User;
import a.b.c.domain.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.Transactional;

@SpringBootApplication
@Slf4j
public class Application implements CommandLineRunner {
  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

  private final UserRepository userRepository;

  public Application(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Transactional
  @Override
  public void run(String... args) throws Exception {
    UserService userService = new UserService(userRepository);
    User user = userService.findById(1).orElse(null);
    log.info("user={}", user);
  }
}
