package test;

import it.sauronsoftware.ftp4j.*;

import java.io.*;

/**
 * Created by Vova on 16.02.2015.
 */
public class TestFTPConnect {
    public static void main(String[] args) throws FTPException, IOException, FTPIllegalReplyException, InterruptedException, FTPAbortedException, FTPDataTransferException, FTPListParseException {
        String path = "tst.log";
//        File outFile = new File("fromFTP.log");
        FTPClient client = new FTPClient();
        client.setType(FTPClient.TYPE_BINARY);
        client.connect("127.0.0.1", 2221);
        client.login("user", "password");
        long size = client.fileSize(path);

        PipedOutputStream pos = new PipedOutputStream();
        final BufferedReader reader = new BufferedReader(new InputStreamReader(new PipedInputStream(pos)));

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                String line;
                try {
                    while((line = reader.readLine())!=null) {
                        System.out.println("Line from FTP PIPE:" + line);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();

        for(int i = 0; i < 20 ; i++) {
            //client.list();
            java.util.Date md = client.modifiedDate(path);
            System.out.println("Modification date :" + md + "; and size: " + size) ;
            if(client.fileSize(path)!=size) {
//                client.download(path,outFile,size);
                client.download(path, pos, size, null);
                size = client.fileSize(path);
            }
            Thread.sleep(1000);
        }

        t.interrupt();
    }
}
