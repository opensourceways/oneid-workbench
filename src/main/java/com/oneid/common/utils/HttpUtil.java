package com.oneid.common.utils;

import com.oneid.common.exception.CustomException;
import com.oneid.common.exception.ErrorCode;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class HttpUtil {

    /**
     * 发送 GET 请求
     *
     * @param url    请求地址
     * @param params 请求参数（可选）
     * @return 响应结果
     * @throws IOException 如果请求失败
     */
    public static ResponseResult sendGet(String url, Map<String, String> params) throws IOException {
        if (params != null && !params.isEmpty()) {
            url = buildUrlWithParams(url, params);
        }

        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", "application/json");

        return getResponse(connection);
    }

    /**
     * 发送 POST 请求（JSON 格式）
     *
     * @param url  请求地址
     * @param json JSON 请求体
     * @return 响应结果
     * @throws IOException 如果请求失败
     */
    public static ResponseResult sendPostJson(String url, String json) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json; utf-8");
        connection.setRequestProperty("Accept", "application/json");
        connection.setDoOutput(true);

        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = json.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        return getResponse(connection);
    }

    /**
     * 发送 POST 请求（form-data 格式）
     *
     * @param url    请求地址
     * @param params 请求参数
     * @return 响应结果
     * @throws IOException 如果请求失败
     */
    public static ResponseResult sendPostFormData(String url, Map<String, String> params) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        connection.setDoOutput(true);

        String formData = buildFormData(params);
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = formData.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        return getResponse(connection);
    }

    /**
     * 发送携带 Header 和 Cookie 的 GET 请求
     *
     * @param url     请求地址
     * @param params  请求参数（可选）
     * @param headers 请求头（可选）
     * @param cookie  Cookie 字符串（例如："key1=value1; key2=value2"）
     * @return 响应结果
     * @throws IOException 如果请求失败
     */
    public static ResponseResult sendGetWithHeaderAndCookie(String url, Map<String, String> params, Map<String, String> headers, String cookie) throws IOException {
        if (params != null && !params.isEmpty()) {
            url = buildUrlWithParams(url, params);
        }

        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", "application/json");

        // 设置请求头
        if (headers != null && !headers.isEmpty()) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                connection.setRequestProperty(entry.getKey(), entry.getValue());
            }
        }

        // 设置 Cookie
        if (cookie != null && !cookie.isEmpty()) {
            connection.setRequestProperty("Cookie", cookie);
        }

        return getResponse(connection);
    }

    /**
     * 构建带参数的 URL
     *
     * @param url    原始 URL
     * @param params 请求参数
     * @return 带参数的 URL
     */
    private static String buildUrlWithParams(String url, Map<String, String> params) {
        StringBuilder urlBuilder = new StringBuilder(url);
        urlBuilder.append("?");

        for (Map.Entry<String, String> entry : params.entrySet()) {
            urlBuilder.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
        }

        // 删除最后一个 "&"
        urlBuilder.deleteCharAt(urlBuilder.length() - 1);
        return urlBuilder.toString();
    }

    /**
     * 构建 form-data 格式的请求体
     *
     * @param params 请求参数
     * @return form-data 格式的字符串
     */
    private static String buildFormData(Map<String, String> params) {
        StringBuilder formData = new StringBuilder();

        for (Map.Entry<String, String> entry : params.entrySet()) {
            formData.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
        }

        // 删除最后一个 "&"
        formData.deleteCharAt(formData.length() - 1);
        return formData.toString();
    }

    /**
     * 获取响应结果及 Cookie
     *
     * @param connection HTTP 连接
     * @return 包含响应内容和 Cookie 的 ResponseResult 对象
     * @throws IOException 如果读取响应失败
     */
    private static ResponseResult getResponse(HttpURLConnection connection) throws IOException {
        int responseCode = connection.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK) {
            ErrorCode errorCode = ErrorCode.fromCode(responseCode);
            if (errorCode != null) {
                throw new CustomException(errorCode);
            } else {
                throw new IOException("HTTP request failed with response code: " + responseCode);
            }
        }

        // 获取响应头中的 Cookie
        Map<String, List<String>> headerFields = connection.getHeaderFields();
        List<String> cookiesHeader = headerFields.get("Set-Cookie");

        // 读取响应内容
        StringBuilder response = new StringBuilder();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
        }

        // 返回包含响应内容和 Cookie 的对象
        return new ResponseResult(response.toString(), cookiesHeader);
    }

    /**
     * 用于封装响应结果和 Cookie 的类
     */
    public static class ResponseResult {
        private final String response;
        private final List<String> cookies;

        public ResponseResult(String response, List<String> cookies) {
            this.response = response;
            this.cookies = cookies;
        }

        public String getResponse() {
            return response;
        }

        public List<String> getCookies() {
            return cookies;
        }
    }
}
