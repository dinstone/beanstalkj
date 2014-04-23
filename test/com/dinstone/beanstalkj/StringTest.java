
package com.dinstone.beanstalkj;

import java.io.UnsupportedEncodingException;

public class StringTest {

    public static void main(String[] args) throws UnsupportedEncodingException {
        defaultgetBytes();
        getBytes("ISO-8859-1");
        defaultgetBytes();
        getBytes("utf-8");
    }

    private static void getBytes(String name) throws UnsupportedEncodingException {
        long s = System.currentTimeMillis();
        int lc = 10000000;
        for (int i = 0; i < lc; i++) {
            String command = "put " + 1 + " " + 0 + " " + 3000 + " " + 52;
            command.getBytes(name);
        }
        long ts = System.currentTimeMillis() - s;

        System.out.println(name + " is " + ts + " ms, tps is " + ts / lc);
    }

    private static void defaultgetBytes() {
        long s = System.currentTimeMillis();
        int lc = 10000000;
        for (int i = 0; i < lc; i++) {
            String command = "put " + 1 + " " + 0 + " " + 3000 + " " + 52;
            command.getBytes();
        }
        long ts = System.currentTimeMillis() - s;

        System.out.println("default is " + ts + " ms, tps is " + ts / lc);
    }
}
