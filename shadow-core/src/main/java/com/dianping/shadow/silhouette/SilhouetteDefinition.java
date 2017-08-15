package com.dianping.shadow.silhouette;

/**
 * Created by jourrey on 16/8/20.
 */
public class SilhouetteDefinition {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("Hi, I'm Silhouette Definition!");
        SilhouetteDefinition definition = new SilhouetteDefinition();
        definition.sayHello0();
        definition.sayHello1("123");
        definition.sayHello2();
        definition.sayHello3("123");
        hello0();
        hello1("123");
        hello2();
        hello3("123");
//        while (true) {
//            Thread.sleep(1000);
//        }
    }

    public void sayHello0() {
        System.out.println("hello world0！");
    }

    public String sayHello1(String s) {
        System.out.println("hello world1！");
        return s;
    }

    private void sayHello2() {
        System.out.println("hello world2！");
    }

    private String sayHello3(String s) {
        System.out.println("hello world3！");
        return s;
    }

    public static void hello0() {
        System.out.println("Hi, I'm Silhouette hello0!");
    }

    public static String hello1(String a) {
        System.out.println("Hi, I'm Silhouette hello1!");
        return a;
    }

    private static void hello2() {
        System.out.println("Hi, I'm Silhouette hello2!");
    }

    private static String hello3(String a) {
        System.out.println("Hi, I'm Silhouette hello3!");
        return a;
    }

}
