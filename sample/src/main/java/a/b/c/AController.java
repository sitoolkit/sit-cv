package a.b.c;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AController {

  @Autowired AService aService;

  @Autowired BService bService;

  /**
   * AController search method
   *
   * @return list of XEntity
   */
  @RequestMapping("search")
  public List<XEntity> search() {

    return aService.search(new SearchCondition());
  }

  public List<XEntity> searchByDoubleCondition() {
    List<XEntity> xEntities =
        aService.searchByDoubleCondition(new SearchCondition(), new SearchCondition());
    return xEntities;
  }

  public int save(XEntity entity) {
    return aService.save(entity);
  }

  public void loopSave(XEntity entity) {
    for (int i = 0; i < 10; i++) {
      bService.search(new SearchCondition());
    }
    aService.save(entity);
  }

  public List<XEntity> filter() {
    return aService.filter();
  }
}
