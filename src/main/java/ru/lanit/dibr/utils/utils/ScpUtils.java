package ru.lanit.dibr.utils.utils;

import com.jcraft.jsch.*;

import java.io.*;
import java.util.concurrent.BlockingQueue;

import ru.lanit.dibr.utils.gui.configuration.SshHost;

/**
 * User: Vova
 * Date: 13.09.2010
 * Time: 0:09:01
 */
public class ScpUtils {

    public static String getFile(SshHost host, String file, String localFileNamePrefix, BlockingQueue<String> debugOutput) throws JSchException, IOException {
        FileOutputStream fos = null;
        try {

            JSch jsch = new JSch();
            Session session = host.connect(debugOutput);

            // exec 'scp -f rfile' remotely
            String command = "scp -f " + file;
            Channel channel = session.openChannel("exec");
            ((ChannelExec) channel).setCommand(command);

            // get I/O streams for remote scp
            OutputStream out = channel.getOutputStream();
            InputStream in = channel.getInputStream();

            channel.connect(30000);

            byte[] buf = new byte[1024];

            // send '\0'
            buf[0] = 0;
            out.write(buf, 0, 1);
            out.flush();
            String fileName = null;
            while (true) {
                int c = checkAck(in);
                if (c != 'C') {
                    break;
                }

                // read '0644 '
                in.read(buf, 0, 5);

                long filesize = 0L;
                while (true) {
                    if (in.read(buf, 0, 1) < 0) {
                        // error
                        break;
                    }
                    if (buf[0] == ' ') break;
                    filesize = filesize * 10L + (long) (buf[0] - '0');
                }


                for (int i = 0; ; i++) {
                    in.read(buf, i, 1);
                    if (buf[i] == (byte) 0x0a) {
                        fileName = new String(buf, 0, i);
                        break;
                    }
                }

                //System.out.println("filesize="+filesize+", fileName="+fileName);

                // send '\0'
                buf[0] = 0;
                out.write(buf, 0, 1);
                out.flush();

                // read a content of lfile
                if (localFileNamePrefix != null && localFileNamePrefix.length() != 0)
                    fileName = localFileNamePrefix + "_" + fileName.replaceAll("/", "-");
                fos = new FileOutputStream(fileName);
                int foo;
                while (true) {
                    if (buf.length < filesize) foo = buf.length;
                    else foo = (int) filesize;
                    foo = in.read(buf, 0, foo);
                    if (foo < 0) {
                        // error
                        break;
                    }
                    fos.write(buf, 0, foo);
                    filesize -= foo;
                    if (filesize == 0L) break;
                }
                fos.close();
                fos = null;

                if (checkAck(in) != 0) {
                    System.exit(0);
                }

                // send '\0'
                buf[0] = 0;
                out.write(buf, 0, 1);
                out.flush();
            }

            session.disconnect();
            return fileName;
        } catch (Exception e) {
            System.out.println(e);
            try {
                if (fos != null) fos.close();
            } catch (Exception ee) {
            }
        }
        return null;

    }

    static int checkAck(InputStream in) throws IOException {
        int b = in.read();
        // b may be 0 for success,
        //          1 for error,
        //          2 for fatal error,
        //          -1
        if (b == 0) return b;
        if (b == -1) return b;

        if (b == 1 || b == 2) {
            StringBuffer sb = new StringBuffer();
            int c;
            do {
                c = in.read();
                sb.append((char) c);
            }
            while (c != '\n');
            if (b == 1) { // error
                System.out.print(sb.toString());
            }
            if (b == 2) { // fatal error
                System.out.print(sb.toString());
            }
        }
        return b;
    }
}
