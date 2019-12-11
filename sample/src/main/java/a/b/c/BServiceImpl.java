package a.b.c;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BServiceImpl implements BService {

  @Autowired ARepositoryFileImpl aRepository;

  @Override
  public void search(SearchCondition condition) {}

  @Override
  public int save(XEntity entity) {
    return aRepository.save(entity);
  }
}
