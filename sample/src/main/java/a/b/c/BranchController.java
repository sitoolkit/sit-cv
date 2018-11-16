package a.b.c;

import org.springframework.beans.factory.annotation.Autowired;

public class BranchController {

    @Autowired
    BProcessor processor;

    public void ifStatement(int num, String str) {
        if (num == 0 || isTrue()) {
            processor.process("");
        } else if (num < 10 || isTrue()) {
            processor.process2("");
        } else {
            if (num > 1000) {
                processor.process3("");
            } else {
                // nothing
            }

            processor.process3("");
        }
    }

    public void switchStatement(String str) {
        switch(str) {
        case "ABC":
            processor.process("");
            break;
        case "DEF":
        case "XYZ":
            processor.process2("");
            break;
        default:
            processor.process3("");
            break;
        }
    }

    public void ternaryOperatorStatement(int num) {
        String result = num == 2 ? processor.process("") :
            num < 20 ? processor.process2("") :
            processor.process3("");
    }

    private boolean isTrue() {
        return true;
    }
}
