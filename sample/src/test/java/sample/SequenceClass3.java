package sample;

import java.util.List;

import sample.ifs.INoImpls;
import sample.ifs.IOneImpl;
import sample.ifs.ITwoImpls;

public class SequenceClass3 {

    INoImpls if0;
    IOneImpl if1;
    ITwoImpls if2;

    public List<ResultOfSeqMethod1> sequence3(ParamOfSeqMethod1 param1, List<List<ParamOfSeqMethod2>> param2list) {
        if0.sequence3_0();
        if1.sequence3_1(null);
        if2.sequence3_2();
        return null;
    }

    public List<ResultOfSeqMethod2> sequence4(ParamOfSeqMethod3 param, boolean b) {
        return null;
    }

    public ResultOfNonSeqMethod nonSeqMethod(ParamOfNonSeqMethod param) {
        return null;
    }

}
