package a.b.c;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AService {

    @Autowired
    ARepository aRepository;

    public List<XEntity> search(SearchConditioner condition) {

        //
        //
        return aRepository.searchBy(condition);
    }

    public int save(XEntity entity) {
        return aRepository.save(entity);
    }
}
