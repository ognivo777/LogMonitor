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
    /** возвращает очередную строку, либо константу "EMPTY_LINE". Если SSHSourсе поставлен на паузу - просто висит ожидая снятия паузы */
    public String readLine() throws IOException;
    /** сбрасывает счётчик прочитанных строк на 0. Следующий вызов readLine() вернёт первую строку из буффера. */
    public void reset();
    public void close() throws Exception;
    public void setPaused(boolean paused);
    public boolean isPaused();
}
