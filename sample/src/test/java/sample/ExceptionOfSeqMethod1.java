package sample;

public class ExceptionOfSeqMethod1 {

    public void throwExceptions1(int n) {
        switch (n) {
            case 1: throw new ArithmeticException();
            case 2: throw new ArrayIndexOutOfBoundsException();
            case 3: throw new NullPointerException();
            case 4: throw new NumberFormatException();
            default: throw new RuntimeException();
        }
    }

    public void throwExceptions2()
        throws ArithmeticException, ArrayIndexOutOfBoundsException, NumberFormatException {
        // Empty
    }
}
