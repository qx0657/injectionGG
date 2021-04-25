package com.android.system.a;

import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * 网络请求工具类
 * Create by QianXiao
 * On 2020/7/24
 * https://blog.csdn.net/DeMonliuhui/article/details/71453656?locationNum=9&fps=1
 */
public class h {
    public static h http = new h();

    public static h g() {
        return http;
    }

    //检测是否代理
    private static boolean a() {
        String proxyAddress = "";
        int proxyPort = 0;
        proxyAddress = System.getProperty("http.proxyHost");
        String proxyPortString = System.getProperty("http.proxyPort");
        proxyPort = Integer.parseInt((proxyPortString != null ? proxyPortString : "-1"));
        if (!TextUtils.isEmpty(proxyAddress) && proxyPort != -1) {
            return true;
        }
        return false;
    }

    public String g(final String url) {
        if(a()){
            return "";
        }
        final StringBuilder sb = new StringBuilder();
        FutureTask<String> task = new FutureTask<String>(new Callable<String>() {
            @Override
            public String call() throws Exception {
                HttpURLConnection connection = null;
                BufferedReader reader = null;
                try {
                    URL requestUrl = new URL(url);
                    connection = (HttpURLConnection) requestUrl.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    if (connection.getResponseCode() == 200) {
                        InputStream in = connection.getInputStream();
                        reader = new BufferedReader(new InputStreamReader(in));
                        String line;
                        while ((line = reader.readLine()) != null) {
                            sb.append(line);
                        }
                        System.out.println(sb);

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (reader != null) {
                        reader.close();
                    }
                    if (connection != null) {
                        connection.disconnect();//断开连接，释放资源
                    }
                }
                return sb.toString();
            }
        });
        new Thread(task).start();
        String s = null;
        try {
            s = task.get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return s;
    }

    public InputStream o(String url){
        InputStream is;
        FutureTask<InputStream> task = new FutureTask<>(() -> new URL(url).openStream());
        new Thread(task).start();
        try {
            is = task.get();
            return is;
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }


}
