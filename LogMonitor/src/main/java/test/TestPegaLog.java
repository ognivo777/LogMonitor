package test;

import okhttp3.*;

import java.io.IOException;
import java.util.*;

/**
 * Created by U_M0NJ2 on 15.01.2018.
 */
public class TestPegaLog {
    public static final MediaType text
            = MediaType.parse("text; charset=utf-8");

    public static void main(String[] args) throws IOException, InterruptedException {
        int selectedPage = 0;
        int linesPerPage = 1000;
        int prevLinesPerPage = 1000;
        int pagesPerChapter = 100;
        int prevPagesPerChapter = 100;



        Request.Builder url = new Request.Builder()
                .url("http://172.25.196.97:16666/prweb/PRServlet/?pyStream=LogViewer&UserIdentifier=administrator@alfabank.ru&Password=dGVzdA==");



        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.cookieJar(new MyCookieJar());
        OkHttpClient client = builder.build();

        //String lastTail = "";
        int lastPage =-1;
        int lastLogSize = 0;
        while(true) {
            String responseBody = getString(selectedPage, linesPerPage, pagesPerChapter, prevLinesPerPage, prevPagesPerChapter, url, client);
            //System.out.println("responseBody = " + responseBody);

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
                    responseBody = getString(selectedPage, linesPerPage, pagesPerChapter, prevLinesPerPage, prevPagesPerChapter, url, client);
                }
            }

            //if(true) break;

            String log = responseBody.substring(responseBody.indexOf("<pre>") + 5, responseBody.indexOf("</pre>"));
            log = log.replaceAll("&lt;", "<");
            log = log.replaceAll("&gt;", ">");
            log = log.replaceAll("&quot;", "\"");
            log = log.replaceAll("&nbsp;", "");
            log = log.trim();

            if(lastPage!=selectedPage) {
                System.out.println(log);
                lastPage = selectedPage;
            } else if (lastLogSize!=log.length()){
                System.out.println(log.substring(lastLogSize));
                lastLogSize = log.length();
            }


            //System.out.println(log);
//
//            if(!lastTail.isEmpty()) {
//                int tailIndex = log.lastIndexOf(lastTail, log.length()-lastTail.length()-1);
//                if(tailIndex>0) {
//                    log = log.substring(tailIndex + lastTail.length());
//                    System.out.println(" !!!!!!!! TAIL !!!!!!!!! " + tailIndex);
//                    System.out.println(log.length());
//                } else {
//                    System.out.println("========= ERROR: TAIL NOT FOUND =========");
//                    continue;
//                }
//            } else {
//                //System.out.println(log
//                System.out.println(log.length());
//            }
//
//            int tailStart = log.lastIndexOf("\n2018-");
//            lastTail = log.substring(tailStart + 1);

            System.out.println("================");

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

    public static void testssss(){
        LinkedList<String> ss = new LinkedList<String>();


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


}
