package a.b.c;

import java.util.List;

public interface AService {

    public List<XEntity> search(SearchConditioner condition);
    public int save(XEntity entity);
}
