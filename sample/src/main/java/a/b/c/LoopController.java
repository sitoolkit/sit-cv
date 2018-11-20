package a.b.c;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

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
    }

    public void forEach() {

        List<String> list = Arrays.asList("");

        for (String str : list) {
            processor.process("");
        }

    }

    public void multiLoop() {

        /**
         * loop comment
         */
        for (int i = 0; i < 1; i++) {
            for (int j = 0; j < 1; j++) {
                processor.process("");
            }
            processor.process2("");
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

    public void streamMethodRef2() {
        List<String> list = Arrays.asList("");

        list.stream().map(BProcessor::staticProcess)
                .forEach(this::process);
        list.stream().forEach(System.out::println);
    }

    private void process(String s){
        processor.process(s);
    }

    public void streamLambda() {

        List<String> list = Arrays.asList("");

        list.stream().map(str -> {
            return processor.process("");
        });

    }

    public void streamLambda2() {

        Optional<String> optional = Optional.ofNullable("");
        optional.ifPresent(s -> {
            processor.process2(s);
        });

            processor.getList("").stream() // loop start
                    .filter(s -> processor.isNotNull(s))
                    .map(s -> processor.process(s))
                    .forEach(s -> {
                        processor.process2("");
                            processor.process3("");
                    }); // end loop
        }

    public void streamLambda3() {
        createStream().forEach(this::process);
    }

    private Stream<String> createStream() {
        return Stream.of("");
    }
}
