package ru.lanit.dibr.utils.core;

import okhttp3.*;
import ru.lanit.dibr.utils.gui.configuration.PegaHost;
import ru.lanit.dibr.utils.utils.Utils;

import javax.xml.bind.DatatypeConverter;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

/**
 * Created by U_M0NJ2 on 15.01.2018.
 */
public class PegaSource implements LogSource {

    public static final MediaType text
            = MediaType.parse("text; charset=utf-8");

    private boolean isClosed = false;
    private boolean paused = false;
    private boolean writeLineNumbers = false;
    int readedLines;

    private Thread readThread;

    List<String> buffer;


    private PegaHost host;
    private int selectedPage = 0;
    private int linesPerPage = 1000;
    private int prevLinesPerPage = 1000;
    private int pagesPerChapter = 100;
    private int prevPagesPerChapter = 100;

    int lastPage =-1;
    int lastLogSize = 0;

    private OkHttpClient client;
    private Request.Builder requestBulder;

    public PegaSource(PegaHost host) {
        this.host = host;
    }

    @Override
    public void startRead() throws Exception {

        readedLines = 0;
        paused = false;
        buffer = new ArrayList<String>();
        isClosed = false;


        final OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.cookieJar(new MyCookieJar());
        client = builder.build();

        String encoded = DatatypeConverter.printBase64Binary(host.getPassword().getBytes());
        String url = host.getHost() + "?pyStream=LogViewer" +
                "&UserIdentifier=" + host.getUser() +
                "&Password=" + encoded;
        System.out.println(url);
        requestBulder = new Request.Builder()
                .url(url);

        Utils.writeToDebugQueue(debugOutput, "Tailing for host '" + host.getDescription() + "' are started.");

        //Запуск треда, который читает строки из Pega и кладёт в буффер.
        readThread = new Thread(new Runnable() {
            public void run() {
                try {
                    while (!isClosed) {

                        while(true) {

                            String responseBody = null;
                            try {
                                responseBody = getString(selectedPage, linesPerPage, pagesPerChapter, prevLinesPerPage, prevPagesPerChapter, requestBulder, client);
                                if(responseBody.contains("onclick='selectNextPage")) {
                                    String seletPageLabel = "onclick='selectPage(";
                                    int lastPageIdx1 = responseBody.lastIndexOf(seletPageLabel);
                                    int lastPageIdx2 = responseBody.indexOf(")", lastPageIdx1);
                                    int lastPageNum = Integer.parseInt(responseBody.substring(lastPageIdx1 + seletPageLabel.length(), lastPageIdx2));
                                    System.out.println("Last page:" + lastPageNum);
                                    if (lastPageNum != selectedPage) {
                                        System.out.println("Get LAST PAGE");
                                        selectedPage = lastPageNum;
                                        lastLogSize = 0;
                                        responseBody = getString(selectedPage, linesPerPage, pagesPerChapter, prevLinesPerPage, prevPagesPerChapter, requestBulder, client);
                                    }
                                }

                                //if(true) break;

                                String log = responseBody.substring(responseBody.indexOf("<pre>") + 5, responseBody.indexOf("</pre>"));
                                log = log.replaceAll("&lt;", "<");
                                log = log.replaceAll("&gt;", ">");
                                log = log.replaceAll("&quot;", "\"");
                                log = log.replaceAll("&nbsp;", "");
                                log = log.replaceAll("&amp;", "&");
                                log = log.replaceAll("&#39;", "'");
                                log = log.trim();

                                String resultLog = "";
                                if(lastPage!=selectedPage) {
                                    resultLog = log;
                                    lastPage = selectedPage;
                                } else if (lastLogSize!=log.length()){
                                    resultLog = log.substring(lastLogSize);
                                    lastLogSize = log.length();
                                }

                                if(!resultLog.isEmpty()){
                                    StringReader stringReader = new StringReader(resultLog);
                                    BufferedReader reader = new BufferedReader(stringReader);
                                    String nextLine;
                                    while ((nextLine = reader.readLine())!=null){
                                        buffer.add(nextLine);
                                    }
                                }

                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    try {
                        close();
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                    e.printStackTrace();
                }
                System.out.println("Stopped Pega read thread.");
            }
        }, "PEGA2BufferReader");

        readThread.start();

    }

    @Override
    public void reloadFull() throws Exception {

    }

    @Override
    public String readLine() throws IOException {
        try {
            while (paused && !isClosed) {
                System.out.println("I'm asleep..");
                Thread.sleep(10);
            }
            if(isClosed) {
                throw new IOException("Connection lost.");
            }
            if (buffer.size() > readedLines) {
                if(writeLineNumbers) {
                    return String.format("%6d: %s", readedLines + 1, buffer.get(readedLines++));
                } else {
                    return buffer.get(readedLines++);
                }
            } else {
                Thread.sleep(10);
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return LogSource.SingletonSkipLineValue.SKIP_LINE;
    }

    /** сбрасывает счётчик прочитанных строк на 0. Следующий вызов readLine() вернёт первую строку из буффера. */
    @Override
    public void reset() {
        readedLines = 0;
        //reader.reset();
    }


    @Override
    public void close() throws Exception {
        if(isClosed) {
            System.out.println("Pega source already closed!");
            return;
        }
        System.out.println("Closing Pega log source..");
        isClosed = true;
        if(readThread!=null) {
            readThread.interrupt();
        }
        buffer.clear();
    }

    public void setPaused(boolean paused) {
        System.out.println("set paused: " + paused);
        this.paused = paused;
    }

    public boolean isWriteLineNumbers() {
        return writeLineNumbers;
    }

    public void setWriteLineNumbers(boolean writeLineNumbers) {
        this.writeLineNumbers = writeLineNumbers;
    }

    public boolean isPaused() {
        return paused;
    }

    @Override
    public String getName() {
        return host.getHost();
    }

    @Override
    public BlockingQueue<String> getDebugOutput() {
        return debugOutput;
    }

    static class MyCookieJar implements CookieJar {

        private List<Cookie> cookies;

        @Override
        public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
            this.cookies =  cookies;
        }

        @Override
        public List<Cookie> loadForRequest(HttpUrl url) {
            if (cookies != null)
                return cookies;
            return new ArrayList<Cookie>();

        }
    }

    private static String getString(int selectedPage, int linesPerPage, int pagesPerChapter, int prevLinesPerPage, int prevPagesPerChapter, Request.Builder url, OkHttpClient client) throws InterruptedException, IOException {
        Thread.sleep(1000);
        RequestBody body = RequestBody.create(text, "" +
                "linesPerPage=" + linesPerPage +
                "&pagesPerChapter=" + pagesPerChapter +
                "&filterString=" +
                "&selectedPage=" + selectedPage +
                "&currentChapter=0" +
                "&prevLinesPerPage=" + prevLinesPerPage +
                "&prevPagesPerChapter=" + prevPagesPerChapter +
                "&logType=PEGA" +
                "&initDisplay=false");
        Request request = url.post(body).build();

        Response response = client.newCall(request).execute();
        return response.body().string();
    }

}


