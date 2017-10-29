package com.dianping.shadow.context;

import org.junit.Test;

import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by jourrey on 16/11/10.
 */
public class AspectContextTest {

    private static final ThreadLocal<String> token = new InheritableThreadLocal<String>();

    public static void main(String[] args) throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        for (int i = 0; i < 10; i++) {
            token.set("" + i);
            System.out.println("1:" + token.get());
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    System.out.println("2:" + token.get());
                    token.remove();
                    System.out.println("3:" + token.get());
                }
            };
            executorService.submit(runnable);
            TimeUnit.SECONDS.sleep(1);
            System.out.println("4:" + token.get());
            System.out.println("========");
        }
    }

    @Test
    public void testExecutor() throws InterruptedException {
        System.out.println(AspectContext.getInstance().getToken());
        System.out.println(AspectContext.getInstance().getAspectHierarchyAndIncrement());
        System.out.println(AspectContext.getInstance().getAspectSequenceAndIncrement());
        System.out.println("========");
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                System.out.println(AspectContext.getInstance().getToken());
                System.out.println(AspectContext.getInstance().getAspectHierarchyAndIncrement());
                System.out.println(AspectContext.getInstance().getAspectSequenceAndIncrement());
                AspectContext.getInstance().tryClean();
                System.out.println("========");
            }
        };

        ExecutorService executorService = Executors.newFixedThreadPool(1);
        for (int i = 0; i < 10; i++) {
            executorService.submit(runnable);
            TimeUnit.SECONDS.sleep(1);
        }
        System.out.println("========");
    }

}
