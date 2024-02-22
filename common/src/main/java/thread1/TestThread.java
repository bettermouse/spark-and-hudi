package thread1;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class TestThread {
    public static void main(String[] args) {
        Integer x =1;
        Runnable y = ()-> System.out.println(x);
    }


    @Test
    public void  test1() throws ExecutionException, InterruptedException {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<?> submit = executor.submit(() -> {
            try {
                Thread.sleep(1000000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        Object o = submit.get();
    }
}
