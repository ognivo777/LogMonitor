package test;

import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;

import java.io.IOException;
import java.net.MalformedURLException;

/**
 * Created by Vova on 17.02.2016.
 */
public class TestCIFS {

    public static void main(String[] args) throws IOException, InterruptedException {
        SmbFile smbFile = new SmbFile("smb://user:111@127.0.0.1/Share/2.log");
        SmbFileInputStream in = new SmbFileInputStream( smbFile );

        smbFile.getAttributes();
        System.out.println(smbFile.getLastModified());
        System.out.println(smbFile.getContentLength());
        System.out.println(smbFile.canRead());
        System.out.println(in.available());

        while (true) {
            Thread.sleep(1000);
//            smbFile.canRead();
//            smbFile.getAttributes();
            System.out.println(smbFile.getContentLength());
        }



//        byte[] b = new byte[8192];
//        int n, tot = 0;
//        long t1 = t0;
//        while(( n = in.read( b )) > 0 ) {
//            System.out.write(b, 0, n);
////            System.out.println();
//            tot += n;
//            System.out.print( '#' );
//        }

    }
}
