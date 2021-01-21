package com.xhtech.hermes.core.util;

import com.google.common.base.Charsets;
import org.apache.http.HttpHeaders;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static org.apache.http.client.utils.HttpClientUtils.closeQuietly;

public class HttpUtils {

    private static final Logger logger = LoggerFactory.getLogger(HttpUtils.class);

    private static CloseableHttpClient httpClient = HttpClients.custom().setMaxConnPerRoute(256).setMaxConnTotal(256).disableContentCompression().disableAutomaticRetries().build();

    /**
     * 发送get请求
     *
     * @param url
     * @return
     */
    public static String get(String url) throws IOException {
        CloseableHttpResponse response = null;

        try {
            HttpGet httpGet = new HttpGet(url);
            response = httpClient.execute(httpGet);
            return readResponseString(response);
        } catch (Exception e) {
            throw e;
        } finally {
            closeQuietly(response);
        }
    }

    /**
     * 发送post请求
     *
     * @param url
     * @param body
     * @return
     */
    public static String post(String url, String body) throws IOException {
        CloseableHttpResponse response = null;

        try {
            HttpPost httpPost = new HttpPost(url);
            httpPost.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
            StringEntity entity = new StringEntity(body, Charsets.UTF_8.name());
            entity.setContentEncoding(Charsets.UTF_8.name());
            httpPost.setEntity(entity);
            response = httpClient.execute(httpPost);
            return readResponseString(response);
        } catch (Exception e) {
            throw e;
        } finally {
            closeQuietly(response);
        }
    }

    private static String readResponseString(CloseableHttpResponse response) throws IOException {
        int status = response.getStatusLine().getStatusCode();

        if (status / 100 == 2) {
            return EntityUtils.toString(response.getEntity());
        } else {
            throw new HttpResponseException(status, EntityUtils.toString(response.getEntity()));
        }
    }
}
