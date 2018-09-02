package a.b.c;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

@Repository
public class ARepository {

    public List<XEntity> searchBy(SearchConditioner condition) {
        return new ArrayList<>();
    }

    public int save(XEntity entity) {
        return 1;
    }
}
