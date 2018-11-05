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
        return aRepository.searchBy(condition);
    }

    @Override
    public int save(XEntity entity) {

        return aRepository.save(entity);
    }

    /**
     * sample comment
     *   multiline and indented
     * See the <a href="{@docRoot}/copyright.html">Copyright</a>.
     * {@link other/link}
     * @since 123.123
     * @param entity
     *      param1 is XEntity object
     *      param multiline comment
     * @param str param2 is String
     * @return return sample
     *         return multiline comment
     * @exception RuntimeException exception1 description
     * @exception NullPointerException
     *      exception2 description
     *      exception multiline comment
     * @deprecated this method is deprecated.
     */
    @Deprecated
    @Override
    public SearchConditioner deprecatedMethod(XEntity entity, String str) {
        return new SearchConditioner();
    }

}
