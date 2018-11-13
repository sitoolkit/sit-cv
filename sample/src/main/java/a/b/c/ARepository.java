package a.b.c;

import java.util.List;

public interface ARepository {

    /**
     * ARepository searchBy method
     *
     * @return list of XEntity
     */
    public List<XEntity> searchBy(SearchConditioner condition);

    public List<XEntity> searchByDoubleCondition(SearchConditioner conditionFirst,
            SearchConditioner conditionSecond);

    public int save(XEntity entity);
}
