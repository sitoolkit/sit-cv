package a.b.c;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AController {

    @Autowired
    AService aService;

    @RequestMapping("search")
    public List<XEntity> search() {

        return aService.search(new SearchConditioner());
    }

    public int save(XEntity entity) {
        return aService.save(entity);
    }
}
