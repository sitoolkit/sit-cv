package a.b.c;

import java.util.List;

public interface ARepository {

    public List<XEntity> searchBy(SearchConditioner condition);
    public int save(XEntity entity);
}
