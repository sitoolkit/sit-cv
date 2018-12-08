package a.b.c;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BServiceImpl implements BService {

    @Autowired
    AService aService;

    @Override
    public void search(SearchCondition condition) {

    }
}
