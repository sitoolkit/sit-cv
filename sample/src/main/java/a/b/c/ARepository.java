package a.b.c;

import java.util.List;

public interface ARepository {

    /**
     * ARepository searchBy method
     *
     * @return list of XEntity
     */
    public List<XEntity> searchBy(SearchConditioner condition);
    public int save(XEntity entity);
}
