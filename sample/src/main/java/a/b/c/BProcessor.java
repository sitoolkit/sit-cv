package a.b.c;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class BProcessor {

    public static String staticProcess(String input) {
        return input;
    }

    public String process(String input) {
        return input;
    }

    public String process2(String input) {
        return input;
    }

    public String process3(String input) {
        return input;
    }

    public String process4(String input) {
        return input;
    }


    public boolean isNotNull(String input) {
        return input != null;
    }

    public List<String> getList(String input) {
        return Collections.emptyList();
    }

    public BProcessor getSelf() {
        return this;
    }

    public BufferedReader read() throws FileNotFoundException {
        return new BufferedReader(new FileReader(" "));
    }

}
