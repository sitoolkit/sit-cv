package a.b.c;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class LoopController {

    @Autowired
    BProcessor processor;

    public void simpleFor() {

        for (int i = 0; i < 1; i++) {
            processor.process("");
        }

        int sum = 0;
        for (int i = 0; i < 1; i++) {
            for (int j = 0; j < 1; j++) {
                sum += i * j;
            }
        }
    }

    public void forEach() {

        List<String> list = Arrays.asList("");

        for (String str : list) {
            processor.process("");
        }

    }

    public void multiLoop() {

        for (int i = 0; i < 1; i++) {
            for (int j = 0; j < 1; j++) {
                processor.process("");
            }
        }
    }

    public void blankLoop() {

        processor.process("");

        int sum = 0;
        for (int i = 0; i < 1; i++) {
            for (int j = 0; j < 1; j++) {
                sum += i * j;
            }
        }
    }

    public void streamMethodRef() {
        List<String> list = Arrays.asList("");

        list.stream().map(processor::process);
    }

    public void streamLambda() {

        List<String> list = Arrays.asList("");

        list.stream().map(str -> {
            return processor.process("");
        });

    }

}
