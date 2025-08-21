package org.example;

import org.taymyr.lagom.soap.WebFaultException;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public class JWebFaultExample {

    public static void webFaultExceptionInJava() {
        testFunction(false).handle((result, e) -> {
            if (e == null) System.out.println(result);
            if (!(e instanceof CompletionException && e.getCause() instanceof WebFaultException && e.getCause().getCause() instanceof IllegalStateException)) {
                System.out.println("Expected exception hierarchy in java is broken, caught exception cause type is " + e.getCause().getCause().getClass());
                e.printStackTrace();
            }
            return 0;
        }).join();
    }

    static CompletableFuture<Integer> testFunction(boolean success) throws RuntimeException {
        return CompletableFuture.supplyAsync(() -> {
                    System.out.println("Entered in test java function");
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    System.out.println("Have waited in test java function");
                    return Integer.valueOf(1337);
                }
        ).handle((result, throwable) -> {
                    if (success)
                        return result;
                    else
                        throw new WebFaultException(new IllegalStateException("test error"));
                }
        );
    }
}
