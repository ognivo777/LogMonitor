package ru.lanit.dibr.utils.core;

import java.io.IOException;

/**
 * User: Vova
 * Date: 09.12.12
 * Time: 18:03
 */
public class RepeatedSource implements Source {

    private Source source;

    private int repeatCount;
    private String line;
    private int lineReadedCount;

    public RepeatedSource(Source source, int repeatCount) {
        if(source==null) {
            throw new IllegalArgumentException("Creating RepeatedSource: Source is null!");
        }
        if(repeatCount<1) {
            throw new IllegalArgumentException("Creating RepeatedSource: repeatCount must be greater than zero!");
        }
        this.source = source;
        this.repeatCount = repeatCount;
        this.lineReadedCount = this.repeatCount;
    }

    public String readLine() throws IOException {
        if(lineReadedCount==repeatCount) {
            line = source.readLine();
            lineReadedCount = 1;
        } else {
            ++lineReadedCount;
        }
        return line;
    }

    public void setRepeatCount(int repeatCount) {
        this.repeatCount = repeatCount;
    }

    public void reset() {
        lineReadedCount = repeatCount;
        source.reset();
    }

    public void close() throws Exception {
        lineReadedCount = repeatCount;
        source.close();
    }

    public void setPaused(boolean paused) {
        source.setPaused(paused);
    }

    public boolean isPaused() {
        return source.isPaused();
    }
}
