package com.example.mtk14060.auctionclient.util;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

/**
 * @file: HttpUtil.java
 * @date: 2018/9/14 10:23
 * @author: mtk14060
 * @function: 对HTTPClient进行封装的工具类
 * @remark: 本系统采用Apache HttpClient与远程服务器进行通信
 */
public class HttpUtil
{
    //创建httpClient
    public static HttpClient httpClient = new DefaultHttpClient();
    public static final String BASE_URL = "http://192.169.1.88:8888/auction/android";

    /**
     *@author MTK14060
     *@time 2018/9/14 10:40
     *@param: 发送请求的URL
     *@return: 服务器相应字符串
     *@exception:
     *@remark:
     */
    public static String getRequest(final String url) throws Exception
    {
        FutureTask<String> task = new FutureTask<String>(new Callable<String>() {
            @Override
            public String call() throws Exception {
                //创建HttpGet对象
                HttpGet get = new HttpGet(url);
                //发送GET请求
                HttpResponse httpResponse = httpClient.execute(get);
                //如果服务器成功返回响应
                if (httpResponse.getStatusLine().getStatusCode() == 200)
                {
                    //获取服务器相应字符串
                    String result = EntityUtils.toString(httpResponse.getEntity());
                    return result;
                }
                return null;
            }
        });
        new Thread(task).start();
        return task.get();
    }

    /**
     *@author MTK14060
     *@time 2018/9/14 11:00
     *@param: url 发送请求的URL
     *@param: params 请求参数
     *@return:  服务器相应字符串
     *@exception:
     *@remark:
     */
    public static String postRequest(final String url, final Map<String, String> rawParmas) throws Exception
    {
        final FutureTask<String> task = new FutureTask<>(
                new Callable<String>() {
                    @Override
                    public String call() throws Exception {
                        //创建HttpPost对象
                        HttpPost post = new HttpPost(url);
                        //如果传递参数个数较多，可对传递的参数进行封装
                        List<NameValuePair> params = new ArrayList<>();
                        for (String key : rawParmas.keySet()) {
                            //封装请求参数
                            params.add(new BasicNameValuePair(key, rawParmas.get(key)));
                        }
                        //设置请求参数
                        post.setEntity(new UrlEncodedFormEntity(params, "gbk"));
                        //发送POST请求
                        HttpResponse httpResponse = httpClient.execute(post);
                        //如果服务器成功返回相应
                        if (httpResponse.getStatusLine().getStatusCode() == 200) {
                            //获取服务器相应字符串
                            String result = EntityUtils.toString(httpResponse.getEntity());
                            return result;
                        }
                        return null;
                    }
                }
        );
        new Thread(task).start();
        return task.get();
    }
}
