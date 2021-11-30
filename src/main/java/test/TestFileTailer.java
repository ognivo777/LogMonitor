package test;

import ru.lanit.dibr.utils.utils.FileTailer;

/**
 * Created by Vova on 15.02.2015.
 */
public class TestFileTailer {
    public static void main(String[] args) throws InterruptedException {
        FileTailer tail = new FileTailer("test.log");
        tail.start();
        while (true) {
            System.out.println(tail.readLine());
        }
    }
}
