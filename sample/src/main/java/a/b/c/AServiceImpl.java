package a.b.c;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AServiceImpl implements AService {

    @Autowired
    ARepository aRepository;

    @Autowired
    BProcessor processor;

    /**
     * Return search result of condition.
     *
     * @param search
     *            condition
     * @return list of XEntity
     */
    @Override
    public List<XEntity> search(SearchConditioner condition) {

        //
        //
        List<XEntity> result = aRepository.searchBy(condition);
        return result;
    }

    @Override
    public List<XEntity> searchByDoubleCondition(SearchConditioner conditionFirst,
            SearchConditioner conditionSecond) {
        return aRepository.searchByDoubleCondition(conditionFirst, conditionSecond);
    }

    /**
     * save XEntity object.
     * multiline comment.
     * See the <a href="{@docRoot}/copyright.html">Copyright</a>.
     * {@link other/link}
     *
     * @since 1.1.0.sample
     * @param entity XEntity object
     * @return return saved status(int)
     * @exception RuntimeException exception description
     * @throws NullPointerException
     *      throws description
     *      multiline comment
     * @see Sample#see
     */
    @Override
    public int save(XEntity entity) {

        return aRepository.save(entity);
    }
}
