package antlr.generate;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CodePointCharStream;
import org.antlr.v4.runtime.IntStream;
import org.antlr.v4.runtime.misc.Interval;

/**
 * <pre>
 * *********************************************
 * Copyright sf-express.
 * All rights reserved.
 * Description:
 * HISTORY
 * *********************************************
 *  ID   DATE               PERSON             REASON
 *  1   2022/5/17 14:09     01407273            Create
 *
 * *********************************************
 * </pre>
 */
public class UpperCaseCharStreamJava implements CharStream {
    CodePointCharStream wrapped;
    public UpperCaseCharStreamJava(CodePointCharStream wrapped ) {
        this.wrapped=wrapped;
    }

    @Override
    public String getText(Interval interval) {
        // ANTLR 4.7's CodePointCharStream implementations have bugs when
        // getText() is called with an empty stream, or intervals where
        // the start > end. See
        // https://github.com/antlr/antlr4/commit/ac9f7530 for one fix
        // that is not yet in a released ANTLR artifact.
        if (size() > 0 && (interval.b - interval.a >= 0)) {
           return wrapped.getText(interval);
        } else {
            return  "";
        }
    }

    @Override
    public void consume() {
        wrapped.consume();
    }

    @Override
    public int LA(int i) {
        int la = wrapped.LA(i);
        if (la == 0 || la == IntStream.EOF)
            return la;
        else
            return  Character.toUpperCase(la);
    }

    @Override
    public int mark() {
        return wrapped.mark();
    }

    @Override
    public void release(int marker) {
       wrapped.release(marker);
    }

    @Override
    public int index() {
        return wrapped.index();
    }

    @Override
    public void seek(int index) {
         wrapped.seek(index);
    }

    @Override
    public int size() {
        return  wrapped.size();
    }

    @Override
    public String getSourceName() {
        return wrapped.getSourceName();
    }
}
