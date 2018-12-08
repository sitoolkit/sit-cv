package a.b.c;

import java.util.List;

public interface ARepository {

    /**
     * ARepository searchBy method
     *
     * @return list of XEntity
     */
    public List<XEntity> searchBy(SearchCondition condition);

    public List<XEntity> searchByDoubleCondition(SearchCondition conditionFirst,
            SearchCondition conditionSecond);

    public int save(XEntity entity);
}
