package com.seeyon.apps.test.util;

import com.seeyon.ctp.common.log.CtpLogFactory;
import org.apache.commons.logging.Log;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;

/**
 * @author 
 * @version 1.0.0
 * @Description TODO  发送rest请求工具类
 * @createTime 2021年08月25日 15:30:00
 */
public class HttpUtils {
    //日志
    private static final Log log = CtpLogFactory.getLog(HttpUtils.class);

    //get请求
    public static String sendGet(String url, String param) throws Exception {
        StringBuilder result = new StringBuilder();
        BufferedReader in = null;
        String urlNameString;
        try {
            urlNameString = url + "?" + param;
            URL realUrl = new URL(urlNameString);
            URLConnection connection = realUrl.openConnection();
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            connection.connect();
            in = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
            String line;
            while ((line = in.readLine()) != null) {
                result.append(line);
            }
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return result.toString();
    }

    //post请求
    public static void sendPost(String url, String content) {
        CloseableHttpClient httpClient = null;
        HttpPost httpPost = null;
        CloseableHttpResponse httpResponse = null;
        try {
            httpClient = HttpClients.createDefault();
            int CONNECTION_TIMEOUT = 10 * 60 * 1000;
            RequestConfig requestConfig = RequestConfig.custom().setConnectionRequestTimeout(CONNECTION_TIMEOUT).setConnectTimeout(CONNECTION_TIMEOUT).setSocketTimeout(CONNECTION_TIMEOUT).build();
            httpPost = new HttpPost(url);
            httpPost.setConfig(requestConfig);
            httpPost.addHeader("Content-Type", "application/json");
            StringEntity requestEntity = new StringEntity(content, "UTF-8");
            httpPost.setEntity(requestEntity);
            httpResponse = httpClient.execute(httpPost, new BasicHttpContext());
            HttpEntity entity = httpResponse.getEntity();
            if (entity != null) {
                EntityUtils.toString(entity, "UTF-8");
            }
        } catch (Exception e) {
            log.error("", e);
        } finally {
            if (httpResponse != null) {
                try {
                    httpResponse.close();
                } catch (Exception ignored) {
                }
            }
            if (httpPost != null) {
                try {
                    httpPost.releaseConnection();
                } catch (Exception ignored) {
                }
            }
            if (httpClient != null) {
                try {
                    httpClient.close();
                } catch (Exception ignored) {
                }
            }
        }
    }
}
