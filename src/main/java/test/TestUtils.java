package test;

import org.junit.Assert;
import org.junit.Test;
import ru.lanit.dibr.utils.utils.Utils;

/**
 * Created with IntelliJ IDEA.
 * User: Vladimir
 * Date: 21.01.14
 * Time: 15:00
 * To change this template use File | Settings | File Templates.
 */
public class TestUtils {

    @Test
    public void testFindFirstIgnoreCase1() {
        String text1 = "12300000000000001230000000000000123000000000123";
        //String text2 = "asdfgdjuy65y43rwerfg43ferdbsawra3r";
        Assert.assertEquals("find first", 0, Utils.indexOf(text1, true, 0, "123"));
        Assert.assertEquals("find first", 0, Utils.indexOf(text1, true, -5, "123"));
        Assert.assertEquals("find second", 16, Utils.indexOf(text1, true, 1, "123"));
        Assert.assertEquals("find second", 16, Utils.indexOf(text1, true, 16, "123"));
        Assert.assertEquals("find third", 32, Utils.indexOf(text1, true, 17, "123"));
        Assert.assertEquals("find last one", 44, Utils.indexOf(text1, true, 33, "123"));
        Assert.assertEquals("find last one", -1, Utils.indexOf(text1, true, 45, "123"));
        Assert.assertEquals("find last one", -1, Utils.indexOf(text1, true, 80, "123"));

        Assert.assertEquals("find last one", 44, Utils.lastIndexOf(text1, true, 47, "123"));
        Assert.assertEquals("find last one", 32, Utils.lastIndexOf(text1, true, 44, "123"));
        Assert.assertEquals("find last one", 16, Utils.lastIndexOf(text1, true, 32, "123"));
        Assert.assertEquals("find last one", 0, Utils.lastIndexOf(text1, true, 16, "123"));
        Assert.assertEquals("find last one", -1, Utils.lastIndexOf(text1, true, 0, "123"));

    }

    @Test
    public void testFindFirstIgnoreCase2() {
        String text1 = "BbBabbbaBBBabBb";
        Assert.assertEquals("find first", 0, Utils.indexOf(text1, false, 0, "BBB"));
        Assert.assertEquals("find first", 0, Utils.indexOf(text1, false, 0, "bbb"));
        Assert.assertEquals("find first", -1, Utils.indexOf(text1, true, 0, "BBb"));
        Assert.assertEquals("find first", -1, Utils.indexOf(text1, true, 0, "bBB"));
        Assert.assertEquals("find first", 4, Utils.indexOf(text1, true, 0, "bbb"));
        Assert.assertEquals("find first", 8, Utils.indexOf(text1, true, 0, "BBB"));

        Assert.assertEquals("find last one", 44, Utils.lastIndexOf(text1, true, 47, "123"));


    }
}
