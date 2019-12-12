package a.b.c.app;

import java.util.Optional;

import a.b.c.domain.User;
import a.b.c.domain.UserRepository;

public class UserService {

  private UserRepository userRepository;

  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public Optional<User> findById(Integer id) {
    return userRepository.findById(id);
  }

  public boolean updateNameById(Integer id, String name) {
    return userRepository.updateNameById(id, name);
  }

  public void create(User user) {
    userRepository.create(user);
  }

  public void deleteById(Integer id) {
    userRepository.deleteById(id);
  }
}
