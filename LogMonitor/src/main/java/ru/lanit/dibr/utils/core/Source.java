package ru.lanit.dibr.utils.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;

/**
 * User: Vova
 * Date: 13.11.12
 * Time: 2:08
 */
public interface Source {
    public static int MAX_CACHED_LINES = 15555;

    public String readLine() throws IOException;
    public void reset();
    public void close() throws Exception;
    public void setPaused(boolean paused);
    public boolean isPaused();
}
