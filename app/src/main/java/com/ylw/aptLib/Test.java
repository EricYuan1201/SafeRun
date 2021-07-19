package com.ylw.aptLib;

import com.ylw.annotation.SafeRun;

/**
 * author : liwen15
 * date : 2021/7/18
 * description :
 */
public class Test {

    @SafeRun
    public static void test2(String name) {
        int i = 2 / 0;
        System.out.println("test... " + "1");
    }

    @SafeRun
    public static boolean test23(String name) {
        int i = 2 / 0;
        System.out.println("test... " + "1");
        return false;
    }

    @SafeRun
    public static int getAge() {
        try {
            System.out.println("test... " + "getAge");
            return 2 / 0;
        } catch (Throwable throwable) {

        }
        return -1;
    }

    @SafeRun
    public static String getName() {
        System.out.println("test... " + "getName");
        int i = 2 / 0;
        return "zhangsan";
    }

    @SafeRun
    public static Person getPerson() {
        System.out.println("test... " + "getPerson");
        int i = 2 / 0;
        return new Person("zhangsan", 18);
    }
}
