package com.dianping.shadow.silhouette;

/**
 * Created by jourrey on 16/8/20.
 */
public class SilhouetteDefinition {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("Hi, I'm Silhouette Definition!");
        hello("123");
        hello2();
//        while (true) {
//            Thread.sleep(1000);
//        }
    }

    public static String hello(String a) {
        System.out.println("Hi, I'm Silhouette hello!");
        return a;
    }

    private static void hello2() {
        System.out.println("Hi, I'm Silhouette hello2!");
    }

}
