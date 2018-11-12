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
    public List<XEntity> searchBy(SearchConditioner condition) {
        return searchRecursivelyA(condition);
    }

    private List<XEntity> searchRecursivelyA(SearchConditioner condition) {
        return searchRecursivelyB(condition);
    }

    private List<XEntity> searchRecursivelyB(SearchConditioner condition) {
        return searchRecursivelyA(condition);
    }

    @Override
    public List<XEntity> searchByDoubleCondition(SearchConditioner conditionFirst,
            SearchConditioner conditionSecond) {
        return new ArrayList<>();
    }

    @Override
    public int save(XEntity entity) {
        return 1;
    }
}
