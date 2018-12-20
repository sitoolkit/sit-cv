package a.b.c;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

@Repository
public class ARepositoryFileImpl implements ARepository{

    @Override
    public List<XEntity> searchBy(SearchCondition condition) {
        return new ArrayList<>();
    }

    @Override
    public List<XEntity> searchByDoubleCondition(SearchCondition conditionFirst,
            SearchCondition conditionSecond) {
        return new ArrayList<>();
    }

    @Override
    public int save(XEntity entity) {
        try (BufferedWriter bw = createWriter()) {
            bw.write(obj2str(entity));
        } catch (IOException e) {
            return 1;
        }
        return 0;
    }

    @Override
    public List<XEntity> filter() {
        return searchBy(new SearchCondition());
    }

    private BufferedWriter createWriter() throws IOException {
        FileWriter fw = new FileWriter("");
        BufferedWriter bw = new BufferedWriter(fw);
        return bw;
    }

    private String obj2str(Object obj) {
        return obj.toString();
    }
}
