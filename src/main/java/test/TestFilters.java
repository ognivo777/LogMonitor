package test;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import ru.lanit.dibr.utils.core.*;

import java.io.IOException;

import static org.mockito.Mockito.*;

/**
 * User: Vova
 * Date: 09.12.12
 * Time: 18:44
 */

public class TestFilters {

    @Mock
    Source source;

    private boolean stop = false;

    @Before
    public void init() throws IOException {
        MockitoAnnotations.initMocks(this);
        when(source.readLine())
                .thenReturn("[block]")
                .thenReturn("aa 111")
                .thenReturn("[block]")
                .thenReturn("aa 222")
                .thenReturn("[block]")
                .thenReturn("aa 333")
                .thenReturn("[block]")
                .thenReturn("bb 111")
                .thenReturn("[block]")
                .thenReturn("bb 222")
                .thenReturn("[block]")
                .thenReturn("bb 333")
                .thenReturn(LogSource.SingletonSkipLineValue.SKIP_LINE)
                .thenReturn(LogSource.SingletonSkipLineValue.SKIP_LINE)
                .thenReturn("[block]")
                .thenReturn("cc 111")
                .thenReturn("[block]")
                .thenReturn("cc 222")
                .thenReturn("[block]")
                .thenReturn("cc 333")
                .thenReturn(LogSource.SingletonSkipLineValue.SKIP_LINE)
                .then(new Answer<Object>() {
                    public Object answer(InvocationOnMock invocation) throws Throwable {
                        stop = true;
                        return LogSource.SingletonSkipLineValue.SKIP_LINE;
                    }
                });
        ;
    }

    @Test
    public void testRepeatedSource() throws IOException {
        RepeatedSource repeatedSource = new RepeatedSource(source, 2);
        String result = "";
        for (stop = false; !stop;) {
            result+=repeatedSource.readLine()+"\n";
        }
        Assert.assertEquals("[block]\n" +
                "[block]\n" +
                "aa 111\n" +
                "aa 111\n" +
                "[block]\n" +
                "[block]\n" +
                "aa 222\n" +
                "aa 222\n" +
                "[block]\n" +
                "[block]\n" +
                "aa 333\n" +
                "aa 333\n" +
                "[block]\n" +
                "[block]\n" +
                "bb 111\n" +
                "bb 111\n" +
                "[block]\n" +
                "[block]\n" +
                "bb 222\n" +
                "bb 222\n" +
                "[block]\n" +
                "[block]\n" +
                "bb 333\n" +
                "bb 333\n" +
                "SKIP_LINE\n" +
                "SKIP_LINE\n" +
                "SKIP_LINE\n" +
                "SKIP_LINE\n" +
                "[block]\n" +
                "[block]\n" +
                "cc 111\n" +
                "cc 111\n" +
                "[block]\n" +
                "[block]\n" +
                "cc 222\n" +
                "cc 222\n" +
                "[block]\n" +
                "[block]\n" +
                "cc 333\n" +
                "cc 333\n" +
                "SKIP_LINE\n" +
                "SKIP_LINE\n" +
                "SKIP_LINE\n", result);

    }


    @Test
    public void testGrepSequence() throws IOException {
        Source filteredSource = new LineSearchFilter("111", false).apply(source);
        filteredSource = new LineSearchFilter("aa", false).apply(filteredSource);

        String result = "";
        for (stop = false; !stop;) {
            result+=filteredSource.readLine()+"\n";
        }
        Assert.assertEquals(
                "SKIP_LINE\n" +
                        "aa 111\n" +
                        "SKIP_LINE\n" +
                        "SKIP_LINE\n" +
                        "SKIP_LINE\n" +
                        "SKIP_LINE\n" +
                        "SKIP_LINE\n" +
                        "SKIP_LINE\n" +
                        "SKIP_LINE\n" +
                        "SKIP_LINE\n" +
                        "SKIP_LINE\n" +
                        "SKIP_LINE\n" +
                        "SKIP_LINE\n" +
                        "SKIP_LINE\n" +
                        "SKIP_LINE\n" +
                        "SKIP_LINE\n" +
                        "SKIP_LINE\n" +
                        "SKIP_LINE\n" +
                        "SKIP_LINE\n" +
                        "SKIP_LINE\n" +
                        "SKIP_LINE\n" +
                        "SKIP_LINE\n",
                result);
    }

    @Test
    public void testInvertedGrepSequence() throws IOException {
        Source filteredSource = new LineSearchFilter("111", true).apply(source);
        filteredSource = new LineSearchFilter("aa", true).apply(filteredSource);

        String result = "";
        for (stop = false; !stop;) {
            result+=filteredSource.readLine()+"\n";
        }
        Assert.assertEquals(
                "[block]\n" +
                        "SKIP_LINE\n" +
                        "[block]\n" +
                        "SKIP_LINE\n" +
                        "[block]\n" +
                        "SKIP_LINE\n" +
                        "[block]\n" +
                        "SKIP_LINE\n" +
                        "[block]\n" +
                        "bb 222\n" +
                        "[block]\n" +
                        "bb 333\n" +
                        "SKIP_LINE\n" +
                        "SKIP_LINE\n" +
                        "[block]\n" +
                        "SKIP_LINE\n" +
                        "[block]\n" +
                        "cc 222\n" +
                        "[block]\n" +
                        "cc 333\n" +
                        "SKIP_LINE\n" +
                        "SKIP_LINE\n",
                result);
    }

    @Test
    public void testGrepMultiFilter() throws IOException {
        Source filteredSource = new LineSearchFilter(false, "111", "aa").apply(source);

        String result = "";
        for (stop = false; !stop;) {
            result+=filteredSource.readLine()+"\n";
        }
        Assert.assertEquals(
                "SKIP_LINE\n" +
                        "aa 111\n" +
                        "SKIP_LINE\n" +
                        "aa 222\n" +
                        "SKIP_LINE\n" +
                        "aa 333\n" +
                        "SKIP_LINE\n" +
                        "bb 111\n" +
                        "SKIP_LINE\n" +
                        "SKIP_LINE\n" +
                        "SKIP_LINE\n" +
                        "SKIP_LINE\n" +
                        "SKIP_LINE\n" +
                        "SKIP_LINE\n" +
                        "SKIP_LINE\n" +
                        "cc 111\n" +
                        "SKIP_LINE\n" +
                        "SKIP_LINE\n" +
                        "SKIP_LINE\n" +
                        "SKIP_LINE\n" +
                        "SKIP_LINE\n" +
                        "SKIP_LINE\n",
                result);
    }
    @Test
    public void testInversedGrepMultiFilter() throws IOException {
        Source filteredSource = new LineSearchFilter(true, "111", "aa").apply(source);

        String result = "";
        for (stop = false; !stop;) {
            result+=filteredSource.readLine()+"\n";
        }
        Assert.assertEquals(
                "[block]\n" +
                        "SKIP_LINE\n" +
                        "[block]\n" +
                        "SKIP_LINE\n" +
                        "[block]\n" +
                        "SKIP_LINE\n" +
                        "[block]\n" +
                        "SKIP_LINE\n" +
                        "[block]\n" +
                        "bb 222\n" +
                        "[block]\n" +
                        "bb 333\n" +
                        "SKIP_LINE\n" +
                        "SKIP_LINE\n" +
                        "[block]\n" +
                        "SKIP_LINE\n" +
                        "[block]\n" +
                        "cc 222\n" +
                        "[block]\n" +
                        "cc 333\n" +
                        "SKIP_LINE\n" +
                        "SKIP_LINE\n",
                result);
    }

    @Test
    public void testInvertedBlockFilterSequence() throws IOException {
        Source filterSequence = new BlockSearchFilter("\\[block\\]", "aa", true).apply(source);
        filterSequence = new BlockSearchFilter("\\[block\\]", "222", true).apply(filterSequence);

        String expected = "SKIP_LINE\n" +
                "SKIP_LINE\n" +
                "SKIP_LINE\n" +
                "SKIP_LINE\n" +
                "SKIP_LINE\n" +
                "SKIP_LINE\n" +
                "[block]\n" +
                "bb 111\n" +
                "SKIP_LINE\n" +
                "SKIP_LINE\n" +
                "[block]\n" +
                "bb 333\n" +
                "SKIP_LINE\n" +
                "SKIP_LINE\n" +
                "[block]\n" +
                "cc 111\n" +
                "SKIP_LINE\n" +
                "SKIP_LINE\n" +
                "[block]\n" +
                "cc 333\n" +
                "SKIP_LINE\n" +
                "SKIP_LINE\n";

        String firstResult = "";
        for (stop = false; !stop;) {
            firstResult+=filterSequence.readLine()+"\n";
        }
        Assert.assertEquals(expected, firstResult);

    }

    @Test
    public void testInvertedBlockMultiFilter() throws IOException {
        Source multiFilter = new BlockSearchFilter("\\[block\\]", "aa", true).addStringToSearch("222").apply(source);

        String expected = "SKIP_LINE\n" +
                "SKIP_LINE\n" +
                "SKIP_LINE\n" +
                "SKIP_LINE\n" +
                "SKIP_LINE\n" +
                "SKIP_LINE\n" +
                "[block]\n" +
                "bb 111\n" +
                "SKIP_LINE\n" +
                "SKIP_LINE\n" +
                "[block]\n" +
                "bb 333\n" +
                "SKIP_LINE\n" +
                "SKIP_LINE\n" +
                "[block]\n" +
                "cc 111\n" +
                "SKIP_LINE\n" +
                "SKIP_LINE\n" +
                "[block]\n" +
                "cc 333\n" +
                "SKIP_LINE\n" +
                "SKIP_LINE\n";

        String secondResult = "";
        for (stop = false; !stop;) {
            secondResult+=multiFilter.readLine()+"\n";
        }
        Assert.assertEquals(expected, secondResult);
    }

    @Test
    public void testBlockFilterSequence() throws IOException {
        Source filteredSource = new BlockSearchFilter("\\[block\\]", "111", false).apply(source);
        filteredSource = new BlockSearchFilter("\\[block\\]", "bb", false).apply(filteredSource);

        String result = "";
        for (stop = false; !stop;) {
            result+=filteredSource.readLine()+"\n";
        }
        Assert.assertEquals(
                "SKIP_LINE\n" +
                        "SKIP_LINE\n" +
                        "SKIP_LINE\n" +
                        "SKIP_LINE\n" +
                        "SKIP_LINE\n" +
                        "SKIP_LINE\n" +
                        "[block]\n" +
                        "bb 111\n" +
                        "SKIP_LINE\n" +
                        "SKIP_LINE\n" +
                        "SKIP_LINE\n" +
                        "SKIP_LINE\n" +
                        "SKIP_LINE\n" +
                        "SKIP_LINE\n" +
                        "SKIP_LINE\n" +
                        "SKIP_LINE\n" +
                        "SKIP_LINE\n" +
                        "SKIP_LINE\n" +
                        "SKIP_LINE\n" +
                        "SKIP_LINE\n" +
                        "SKIP_LINE\n" +
                        "SKIP_LINE\n",
                result);
    }

    @Test
    public void testBlockMultuFilter() throws IOException {
        Source filteredSource = (new BlockSearchFilter("\\[block\\]", "111", false)).addStringToSearch("bb").apply(source);

        String result = "";
        for (stop = false; !stop;) {
            result+=filteredSource.readLine()+"\n";
        }
        Assert.assertEquals(
                "[block]\n" +
                        "aa 111\n" +
                        "SKIP_LINE\n" +
                        "SKIP_LINE\n" +
                        "SKIP_LINE\n" +
                        "SKIP_LINE\n" +
                        "[block]\n" +
                        "bb 111\n" +
                        "[block]\n" +
                        "bb 222\n" +
                        "[block]\n" +
                        "bb 333\n" +
                        "SKIP_LINE\n" +
                        "SKIP_LINE\n" +
                        "[block]\n" +
                        "cc 111\n" +
                        "SKIP_LINE\n" +
                        "SKIP_LINE\n" +
                        "SKIP_LINE\n" +
                        "SKIP_LINE\n" +
                        "SKIP_LINE\n" +
                        "SKIP_LINE\n",
                result);
    }

//    @Test
//    public void testFilteredSource_Grep() throws IOException {
//
//        FilteredSource filteredSource = new FilteredSource(source);
//        filteredSource.addFilterToScreen(new LineSearchFilter("111", false));
//        filteredSource.addFilterToScreen(new LineSearchFilter("333", false));
//        filteredSource.addFilterToQueue(new LineSearchFilter("aa", false));
//
//        String result = "";
//        for (stop = false; !stop;) {
//            result+=filteredSource.readLine()+"\n";
//        }
//        Assert.assertEquals(
//                "aa 111\n" +
//                "SKIP_LINE\n" +
//                "aa 333\n" +
//                "SKIP_LINE\n" +
//                "SKIP_LINE\n" +
//                "SKIP_LINE\n" +
//                "SKIP_LINE\n" +
//                "SKIP_LINE\n" +
//                "SKIP_LINE\n" +
//                "SKIP_LINE\n",
//                result);
//    }

}
