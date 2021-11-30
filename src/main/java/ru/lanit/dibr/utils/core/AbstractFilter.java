package ru.lanit.dibr.utils.core;

import java.io.IOException;

/**
 * Created by Vova on 18.12.2015.
 */
public abstract class AbstractFilter implements Filter {
    protected boolean isActive = true;

    abstract protected String readFilteredLine(Source source) throws IOException;

    protected void onReset() {
        isActive = true;
    }

    public Source apply(final Source source) {
        return new Source() {

            private boolean closed;

            public String readLine() throws IOException {
                if(closed) {
                    throw new RuntimeException("Stream is closed!");
                }
                return readFilteredLine(source);
            }

            public void reset() {
                source.reset();
                onReset();
            }

            public void close() {
                closed = true;
            }

            public void setPaused(boolean paused) {
                source.setPaused(paused);
            }

            public boolean isPaused() {
                return source.isPaused();
            }
        };
    }

    @Override
    public void disable() {
        isActive = false;
    }

    @Override
    public boolean isActive() {
        return isActive;
    }
}
