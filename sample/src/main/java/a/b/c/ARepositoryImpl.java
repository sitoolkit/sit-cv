package a.b.c;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

@Repository
public class ARepositoryImpl implements ARepository{

    @Override
    public List<XEntity> searchBy(SearchConditioner condition) {
        return new ArrayList<>();
    }

    @Override
    public int save(XEntity entity) {
        return 1;
    }
}
