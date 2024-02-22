package thread;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class TestCompletableFuture {
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        CompletableFuture<String> future = new CompletableFuture<>();
        CompletableFuture<String> future1 = future.thenApplyAsync(x -> {
            System.out.println("123");
           return x;
        });
    //    future1.complete("x");
        future.complete("aa");
        System.out.println(future.isDone());
        String s = future1.get();
        Thread.sleep(10000);
    }
}
