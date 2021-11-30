package ru.lanit.dibr.utils.core;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * User: Vova
 * Date: 13.11.12
 * Time: 3:08
 */
public class LinesReader {
    BufferedReader reader = null;

    public String readLine() throws IOException {
        return reader.readLine();
    }
}
