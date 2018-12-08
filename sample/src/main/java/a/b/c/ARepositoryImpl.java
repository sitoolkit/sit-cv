package a.b.c;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

@Repository
public class ARepositoryImpl implements ARepository{

    /**
     * ARepositoryImpl searchBy method
     * 指定された条件で検索を行い結果を返却します。
     *
     * @param search condition
     * @return list of XEntity
     */
    @Override
    public List<XEntity> searchBy(SearchCondition condition) {
        return searchRecursivelyA(condition);
    }

    private List<XEntity> searchRecursivelyA(SearchCondition condition) {
        return searchRecursivelyB(condition);
    }

    private List<XEntity> searchRecursivelyB(SearchCondition condition) {
        return searchRecursivelyA(condition);
    }

    @Override
    public List<XEntity> searchByDoubleCondition(SearchCondition conditionFirst,
            SearchCondition conditionSecond) {
        return new ArrayList<>();
    }

    @Override
    public int save(XEntity entity) {
        return 1;
    }
}
