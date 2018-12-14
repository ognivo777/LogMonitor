package hlam;

import java.io.UnsupportedEncodingException;

/**
 * Created by U_M0NJ2 on 01.06.2016.
 */
public class TestEncode {

    public static void main(String[] args) throws UnsupportedEncodingException {
        String s = "Владимиров Владимир Владимирович";
        byte a1[] = s.getBytes("cp1251");
        String s2 = new String(a1, "utf8");
        System.out.println(s2);
    }

}
