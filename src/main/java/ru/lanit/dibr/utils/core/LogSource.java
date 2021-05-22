package ru.lanit.dibr.utils.core;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * User: Vova
 * Date: 13.11.12
 * Time: 2:15
 */
public interface LogSource extends Source {
    BlockingQueue<String> debugOutput = new LinkedBlockingQueue<String>();

    /** Подключение к источнику и запуск его чтения */
    public void startRead() throws Exception;
    public void reloadFull() throws Exception;
    public void close() throws Exception;
    public String getName();
    public boolean isWriteLineNumbers();

    public void setWriteLineNumbers(boolean writeLineNumbers);

    public BlockingQueue<String> getDebugOutput();

    final class SingletonSkipLineValue {
        public final static String SKIP_LINE = "SKIP_LINE";
    }
}

