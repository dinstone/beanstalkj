
package com.dinstone.beanstalkj;

import java.util.concurrent.CountDownLatch;

import org.junit.Test;

import com.dinstone.beanstalkj.internal.DefaultBeanstalkClient;
import com.dinstone.beanstalkj.internal.NioConnection;
import com.dinstone.beanstalkj.internal.operation.PutOperation;

public class BeanstalkClientTest {

    @Test
    public void testBioStreesPut() {
        int tc = 2;
        final CountDownLatch doneLatch = new CountDownLatch(tc);
        final CountDownLatch startLatch = new CountDownLatch(1);

        Configuration config = new Configuration();
        final DefaultBeanstalkClient client = new DefaultBeanstalkClient(config);
        client.useTube("stress");

        // create thread for test case
        for (int i = 0; i < tc; i++) {
            Thread t = new Thread() {

                @Override
                public void run() {
                    try {
                        startLatch.await();
                    } catch (InterruptedException e) {
                        return;
                    }

                    for (int i = 0; i < 10000; i++) {
                        try {
                            client.putJob(1, 0, 5000, "this is some data".getBytes());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    doneLatch.countDown();
                }

            };
            t.setName("t-" + i);
            t.start();
        }

        try {
            Thread.sleep(1000);
            startLatch.countDown();
            long anyStart = System.currentTimeMillis();
            doneLatch.await();
            long anyEnd = System.currentTimeMillis();
            long ts = anyEnd - anyStart;
            System.out.println("this case takes " + ts + " ms, the rate is " + (tc * 10000000 / ts) + " p/s");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testNioStreesPut() {
        int tc = 2;
        final CountDownLatch doneLatch = new CountDownLatch(tc);
        final CountDownLatch startLatch = new CountDownLatch(1);

        Configuration config = new Configuration();
        NioConnection connection = new NioConnection(config.getServiceHost(), config.getServicePort());
        final DefaultBeanstalkClient client = new DefaultBeanstalkClient(config, connection, null);
        // final DefaultBeanstalkClient client = new
        // DefaultBeanstalkClient(config);
        client.useTube("stress");

        // create thread for test case
        for (int i = 0; i < tc; i++) {
            Thread t = new Thread() {

                @Override
                public void run() {
                    try {
                        startLatch.await();
                    } catch (InterruptedException e) {
                        return;
                    }

                    for (int i = 0; i < 10000; i++) {
                        try {
                            client.putJob(1, 0, 5000, "this is some data".getBytes());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    doneLatch.countDown();
                }

            };
            t.setName("t-" + i);
            t.start();
        }

        try {
            Thread.sleep(1000);
            startLatch.countDown();
            long anyStart = System.currentTimeMillis();
            doneLatch.await();
            long anyEnd = System.currentTimeMillis();
            long ts = anyEnd - anyStart;
            System.out.println("this case takes " + ts + " ms, the rate is " + (tc * 10000000 / ts) + " p/s");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testStreesBioPut00() {
        Configuration config = new Configuration();
        final DefaultBeanstalkClient client = new DefaultBeanstalkClient(config);
        client.useTube("stress");

        long anyStart = System.currentTimeMillis();
        int lc = 10000;
        for (int i = 0; i < lc; i++) {
            try {
                client.putJob(1, 0, 5000, "this is some data".getBytes());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        long anyEnd = System.currentTimeMillis();
        long ts = anyEnd - anyStart;
        System.out.println("this case[BIO] takes " + ts + " ms, the rate is " + (lc * 1000 / ts) + " p/s");

        client.close();
    }

    @Test
    public void testStreesNioPut00() {
        Configuration config = new Configuration();
        NioConnection connection = new NioConnection(config.getServiceHost(), config.getServicePort());
        final DefaultBeanstalkClient client = new DefaultBeanstalkClient(config, connection, null);
        client.useTube("stress");

        long anyStart = System.currentTimeMillis();
        int lc = 10000;
        for (int i = 0; i < lc; i++) {
            try {
                client.putJob(1, 0, 5000, "this is some data".getBytes());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        long anyEnd = System.currentTimeMillis();
        long ts = anyEnd - anyStart;
        System.out.println("this case[NIO] takes " + ts + " ms, the rate is " + (lc * 1000 / ts) + " p/s");

        client.close();
    }

    @Test
    public void testBioReserveJob() {
        Configuration config = new Configuration();
        NioConnection connection = new NioConnection(config.getServiceHost(), config.getServicePort());
        final DefaultBeanstalkClient client = new DefaultBeanstalkClient(config, connection, null);
        client.watchTube("stress");

        long anyStart = System.currentTimeMillis();
        int lc = 10000;
        for (int i = 0; i < lc; i++) {
            try {
                Job job = client.reserveJob(1);
                if (job != null) {
                    client.deleteJob(job.getId());
                } else {
                    System.out.println("it's null");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        long anyEnd = System.currentTimeMillis();
        long ts = anyEnd - anyStart;
        System.out.println("Reserve case[BIO] takes " + ts + " ms, the rate is " + (lc * 1000 / ts) + " p/s");

        client.close();
    }

    @Test
    public void testNioReserveJob() {
        Configuration config = new Configuration();
        NioConnection connection = new NioConnection(config.getServiceHost(), config.getServicePort());
        final DefaultBeanstalkClient client = new DefaultBeanstalkClient(config, connection, null);
        client.watchTube("stress");

        long anyStart = System.currentTimeMillis();
        int lc = 10000;
        for (int i = 0; i < lc; i++) {
            try {
                Job job = client.reserveJob(1);
                if (job != null) {
                    client.deleteJob(job.getId());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        long anyEnd = System.currentTimeMillis();
        long ts = anyEnd - anyStart;
        System.out.println("Reserve case[NIO] takes " + ts + " ms, the rate is " + (lc * 1000 / ts) + " p/s");

        client.close();
    }

    @Test
    public void testNewObject() {
        long anyStart = System.currentTimeMillis();
        int lc = 10000;
        for (int i = 0; i < lc; i++) {
            new PutOperation(1, 0, 1, "this is some data".getBytes());
        }
        long anyEnd = System.currentTimeMillis();
        long ts = anyEnd - anyStart;
        System.out.println("new object takes " + ts + " ms, the rate is " + (lc * 1000 / ts) + " p/s");
    }

    public static void main(String[] args) {
        BeanstalkClientTest beanstalkClientTest = new BeanstalkClientTest();
        beanstalkClientTest.testStreesBioPut00();
        beanstalkClientTest.testStreesNioPut00();

        beanstalkClientTest.testStreesBioPut00();
        beanstalkClientTest.testBioReserveJob();

        beanstalkClientTest.testStreesNioPut00();
        beanstalkClientTest.testNioReserveJob();
    }
}
