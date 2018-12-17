package a.b.c;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;

public class BranchController {

    @Autowired
    BProcessor processor;

    public void ifStatement(int num, String str) {
        /**
         * if comment
         */
        if ((num == 0 || (isTrue()))) {
            processor.process("");
        } else if (num < 10 || isTrue() // if comment
                || false) {
            processor.process2("");
        } else {
            processor.process3("");
        }
    }

    public void omittedIfStatement(int num, String str) {
        if (num == 0 || isTrue()) processor.process("");
        else if (num < 10 || isTrue() || false) processor.process2("");
        else processor.process3("");
    }

    public void nestedIfStatement(int num, String str) {
        if (num == 0 || isTrue()) {
            processor.process("");
        } else if (num < 10 || false) {
            processor.process2("");
        } else {
            if (num > 1000) {
                processor.process2("");
            } else if (num < 100) {
                // nothing
            } else {
                processor.process("");
            }

            processor.process3("");
        }
    }

    public void blankIfStatement(int num, String str) {
        int i;
        if (num == 0 || isTrue()) {
            i = 0;
        } else if (num < 10 || isTrue()) {
            i = 1;
        } else {
            i = 2;
        }

        processor.process("");
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

    public void tryStatement() {
        try (BufferedReader reader = processor.read()) {
            processor.process("");
        } catch (FileNotFoundException | NullPointerException | ArrayIndexOutOfBoundsException
                | NumberFormatException e) {
            processor.process2("");
        } catch (IOException e) {
            processor.process3("");
        } finally {
            processor.process4("");
        }
    }

    public void tryWithoutFinallyStatement() {
        try {
            processor.process("");
        } catch (NullPointerException | NumberFormatException e) {
            processor.process2("");
        } catch (RuntimeException e) {
            processor.process3("");
        }
    }

    public void nestedTryStatement() {
        try (BufferedReader reader = processor.read()) {
            processor.process("");
            try {
                processor.process2("");
            } catch (NullPointerException e) {
                processor.process3("");
            }
        } catch (ArrayIndexOutOfBoundsException | IOException e) {
            try {
                processor.process("");
            } catch (NumberFormatException ex) {
                processor.process3("");
            }
        } finally {
            try {
                processor.process3("");
            } catch (RuntimeException ex) {
                processor.process4("");
            }
        }
    }

    public void emptyTryStatement() {
        int i = 0;
        try {
            i++;
        } catch (NullPointerException | NumberFormatException e) {
            i++;
        } catch (RuntimeException e) {
            i++;
        } finally {
            i++;
        }
    }

    public void notEmptyTryStatement() {
        try {
            processor.process("");
        } catch (NullPointerException e) {
        } catch (RuntimeException e) {
        } finally {
        }

        try {
        } catch (NullPointerException e) {
        } catch (RuntimeException e) {
            processor.process2("");
        } finally {
        }

        try {
        } catch (NullPointerException e) {
        } catch (RuntimeException e) {
        } finally {
            processor.process3("");
        }
    }

    private boolean isTrue() {
        return true;
    }
}
