package a.b.c;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AServiceImpl implements AService{

    @Autowired
    ARepository aRepository;

    /**
     * AServiceImpl search method
     * 指定された条件で検索を行い結果を返却します。
     *
     * @param search condition
     * @return list of XEntity
     */
    @Override
    public List<XEntity> search(SearchConditioner condition) {

        //
        //
        return aRepository.searchBy(condition);
    }

    @Override
    public int save(XEntity entity) {
        return aRepository.save(entity);
    }
}
