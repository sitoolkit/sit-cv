package a.b.c;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AServiceImpl implements AService {

  @Autowired ARepository aRepository;

  @Autowired ASpecification aSpecification;

  @Autowired BProcessor processor;

  /**
   * Return search result of condition.
   *
   * @param search condition
   * @return list of XEntity
   */
  @Override
  public List<XEntity> search(SearchCondition condition) {

    //
    //
    List<XEntity> result = aRepository.searchBy(condition);
    return result;
  }

  @Override
  public List<XEntity> searchByDoubleCondition(
      SearchCondition conditionFirst, SearchCondition conditionSecond) {
    return aRepository.searchBy(conditionFirst);
  }

  /**
   * save XEntity object. multiline comment. See the <a
   * href="{@docRoot}/copyright.html">Copyright</a>. {@link other/link}
   *
   * @since 1.1.0.sample
   * @param entity XEntity object
   * @return return saved status(int)
   * @exception RuntimeException exception description
   * @throws NullPointerException throws description multiline comment
   * @see Sample#see
   */
  @Override
  public int save(XEntity entity) {

    aSpecification.isSatisfiedByList(Collections.singletonList(entity));

    return aRepository.save(entity);
  }

  @Override
  public List<XEntity> filter() {
    List<XEntity> result = aRepository.filter();
    return result;
  }
}
