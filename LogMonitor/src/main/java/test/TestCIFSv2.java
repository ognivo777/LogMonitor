package test;

import com.hierynomus.msdtyp.AccessMask;
import com.hierynomus.mssmb2.SMB2CreateDisposition;
import com.hierynomus.mssmb2.SMB2ShareAccess;
import com.hierynomus.smbj.SMBClient;
import com.hierynomus.smbj.auth.AuthenticationContext;
import com.hierynomus.smbj.connection.Connection;
import com.hierynomus.smbj.session.Session;
import com.hierynomus.smbj.share.DiskShare;
import com.hierynomus.smbj.share.File;
import ru.lanit.dibr.utils.gui.configuration.CIFSHostV2;

import java.io.IOException;
import java.util.EnumSet;

public class TestCIFSv2 {

    public static void main(String[] args) {
        CIFSHostV2 host = new CIFSHostV2("1", "127.0.0.1", 0, "Vladimir", "D0fl1rih!", "utf-8", null);
        host.setDomain("");
        host.setShareName("Shares");
        SMBClient client = new SMBClient();
        File remoteFile;
        try (Connection connection = client.connect(host.getHost())) {
            AuthenticationContext ac = new AuthenticationContext(host.getUser(), host.getPassword().toCharArray(), host.getDomain());
            Session session = connection.authenticate(ac);

            // Connect to Share
            try (DiskShare share = (DiskShare) session.connectShare(host.getShareName())) {
                remoteFile = share.openFile("tmp/debug.log", EnumSet.of(AccessMask.GENERIC_READ), null, SMB2ShareAccess.ALL, SMB2CreateDisposition.FILE_OPEN, null);
                long fileSize = remoteFile.getFileInformation().getStandardInformation().getEndOfFile();
                System.out.println("fileSize = " + fileSize);
            }

            //8168323
            //8168323

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
