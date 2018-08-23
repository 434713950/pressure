package com.pc;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @Author: PengCheng
 * @Description:
 * @Date: 2018/7/30
 */
public class PressureTest {
    public static AtomicInteger fail = new AtomicInteger(0);
    public static AtomicInteger success = new AtomicInteger(0);

    public static AtomicLong total = new AtomicLong(0);

    public static void main(String[] args) throws IOException {
        int num = 1000;

        //并发模拟器
        final CountDownLatch countDownLatch = new CountDownLatch(num);
        //打印通知
        final CountDownLatch count = new CountDownLatch(num);

        ExecutorService executorService = Executors.newFixedThreadPool(100);

        final URL url = new URL("http://127.0.0.1:8092/shoppingCart/updateCartProductAmount");


        try {
            for (int i =0; i<num;i++){
                executorService.submit(new Runnable() {

                    @Override
                    public void run() {
                        Random random = new Random();
                        Integer um = random.nextInt(100);
                        String body = "{\"productId\":\"1032199274569281537\",\"productNum\":"+um +",\"recommendId\":\"1032473054482984961\",\"shopId\":\"1032461872191094785\"}";
                        System.out.println("param:"+um);
                        byte[] content = body.getBytes(Charset.forName("UTF-8"));
                        try {
                            countDownLatch.await();
                            if (tet(url, content)){
                                success.addAndGet(1);
                            }else {
                                fail.addAndGet(1);
                            }
                            count.countDown();
                        } catch (InterruptedException e) {
                            fail.addAndGet(1);
                            e.printStackTrace();
                        } catch (IOException e) {
                            fail.addAndGet(1);
                            e.printStackTrace();
                        }
                    }
                });
                countDownLatch.countDown();
            }
            count.await();
            System.out.println("success:"+success.get()+";fail:"+fail.get()+";"+"total:"+total.get());
            executorService.shutdown();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static Boolean tet(URL url, byte[] content) throws IOException {
        long start = System.currentTimeMillis();
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setDoOutput(true);
        conn.setDoInput(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Connection", "Keep-Alive");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("content-length", String.valueOf(content.length));
        conn.setRequestProperty("Accept", "*/*");
        conn.setRequestProperty("Cache-Control","no-cache");
        conn.setRequestProperty("Cookie","bh_client=Bearer_eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ7XCJhdXRoZW50aWNhdGVkXCI6dHJ1ZSxcImF1dGhvcml0aWVzXCI6W10sXCJkZXRhaWxzXCI6e1wicmVtb3RlQWRkcmVzc1wiOlwiMTAuMi4yMS4yMTdcIn0sXCJuYW1lXCI6XCIxMzc3MDM5MTEyOFwiLFwicHJpbmNpcGFsXCI6e1wiYWNjb3VudFwiOlwiMTM3NzAzOTExMjhcIixcImFjY291bnROb25FeHBpcmVkXCI6dHJ1ZSxcImFjY291bnROb25Mb2NrZWRcIjp0cnVlLFwiYXV0aG9yaXRpZXNcIjpbXSxcImNyZWF0ZVRpbWVcIjoxNTM0OTM0MDA5MDAwLFwiY3JlZGVudGlhbHNOb25FeHBpcmVkXCI6ZmFsc2UsXCJlbmFibGVkXCI6ZmFsc2UsXCJpZFwiOjEwMzIyMTQxNjQ1ODI3MDMxMDYsXCJsYXN0TG9naW5UaW1lXCI6MTUzNTAwNDE3Njk1OSxcInBhc3N3b3JkXCI6XCJwYmtkZjJfc2hhMjU2JDIwMDAwJDdSQk8xaVo1UDR0diRYbEZwcC92a2hEazgyWEdQZk5DODBkZ2JVRWhTYUVyMHYxdGdjYnA1anh3PVwiLFwic3RhdHVzXCI6MCxcInN1cGVyVXNlclwiOmZhbHNlLFwidXNlcm5hbWVcIjpcIjEzNzcwMzkxMTI4XCJ9fSIsImV4cCI6MTUzNTA5MDU3N30.eJ5IENLJwepEPGaFiBGZjQYHw_8r8XoZJ3njPKphmppqOGsdCEZiJzUy7eCAyvOKP8KFgYL3sJuJBh9Nkq6A_A; bh_server=Bearer_eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ7XCJhdXRoZW50aWNhdGVkXCI6dHJ1ZSxcImF1dGhvcml0aWVzXCI6W10sXCJkZXRhaWxzXCI6e1wicmVtb3RlQWRkcmVzc1wiOlwiMTAuMi4yMS4yMTdcIn0sXCJuYW1lXCI6XCJzeXN0ZW1cIixcInByaW5jaXBhbFwiOntcImFjY291bnRcIjpcInN5c3RlbVwiLFwiYWNjb3VudE5vbkV4cGlyZWRcIjp0cnVlLFwiYWNjb3VudE5vbkxvY2tlZFwiOnRydWUsXCJjcmVhdGVUaW1lXCI6MTUzMzYwNTcyNDAwMCxcImNyZWRlbnRpYWxzTm9uRXhwaXJlZFwiOmZhbHNlLFwiZW5hYmxlZFwiOmZhbHNlLFwiaWRcIjoxLFwibGFzdExvZ2luVGltZVwiOjE1MzUwMDQ2NTIwMDYsXCJwYXNzd29yZFwiOlwicGJrZGYyX3NoYTI1NiQyMDAwMCRFSFY1RDQ5N0RKczAkb0ZuT1cwdlJ5azl5cG5FNFBhN1NTRFJMOUxJRE9pMnJnaU1qL2tBQlN4VT1cIixcInN0YXR1c1wiOjAsXCJzdXBlclVzZXJcIjp0cnVlLFwidXNlcm5hbWVcIjpcInN5c3RlbVwifX0iLCJleHAiOjE1MzUwOTEwNTJ9.bdzwZ3Opfaaz6RQA7kat3HlVCq-ogZhG43vVht0yEP-xj2RnmX-_wEqjNtQ7ntJH8BKB0yLZKWgZtnA0NZdLZQ");


        conn.connect();
        OutputStream out = conn.getOutputStream();
        out.write(content);
        out.flush();

        if (conn.getResponseCode() < 400){
            System.out.println(getStreamAsString(conn.getInputStream(),"UTF-8"));
            long time = System.currentTimeMillis()-start;
            System.out.println("===success==="+time);
            total.addAndGet(time);
            return true;
        }
        long time = System.currentTimeMillis()-start;
        System.out.println("===failed===="+time);
        total.addAndGet(time);
        return false;
    }

    private static String getStreamAsString(InputStream stream, String charset) throws IOException {
        try {
            Reader reader = new InputStreamReader(stream, charset);
            StringBuilder response = new StringBuilder();

            final char[] buff = new char[1024];
            int read = 0;
            while ((read = reader.read(buff)) > 0) {
                response.append(buff, 0, read);
            }

            return response.toString();
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
    }
}
