package a.b.c;

import java.util.List;

public interface AService {

    /**
     * AService search method
     *
     * @param search condition
     * @return list of XEntity
     */
    public List<XEntity> search(SearchConditioner condition);
    public int save(XEntity entity);
    public SearchConditioner deprecatedMethod(XEntity entity, String str);
}
