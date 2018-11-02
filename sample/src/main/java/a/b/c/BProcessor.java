package a.b.c;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class BProcessor {

    public String process(String input) {
        return input;
    }

    public String process2(String input) {
        return input;
    }

    public String process3(String input) {
        return input;
    }

    public boolean isNotNull(String input) {
        return input != null;
    }

    public List<String> getList(String input) {
        return Collections.emptyList();
    }

}
