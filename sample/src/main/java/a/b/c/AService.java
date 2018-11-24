package a.b.c;

import java.util.List;

public interface AService {

    /**
     * AService search method
     *
     * @param search condition
     * @return list of XEntity
     */
    public List<XEntity> search(SearchCondition condition);

    public List<XEntity> searchByDoubleCondition(SearchCondition conditionFirst,
            SearchCondition conditionSecond);

    public int save(XEntity entity);
}
