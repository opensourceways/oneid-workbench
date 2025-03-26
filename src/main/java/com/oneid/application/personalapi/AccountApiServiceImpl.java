package com.oneid.application.personalapi;

import com.alibaba.fastjson2.JSONObject;
import com.oneid.application.personalapi.dto.UserInfoDTO;
import com.oneid.common.exception.CustomException;
import com.oneid.common.exception.ErrorCode;
import com.oneid.common.utils.HttpUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class AccountApiServiceImpl implements AccountApiService {

    private static final Logger log = LoggerFactory.getLogger(AccountApiServiceImpl.class);
    @Value("${account.person.center.url}")
    private String accountUrl;

    @Override
    public UserInfoDTO getUserInfo(String utToken, String ygToken, HttpServletResponse servletResponse) {
        Map<String, String> header = new HashMap<String, String>();
        header.put("token", utToken);
        StringBuilder cookie = new StringBuilder();
        cookie.append("_Y_G_=").append(ygToken);
        HttpUtil.ResponseResult response= null;
        try {
            response = HttpUtil.sendGetWithHeaderAndCookie(accountUrl, null, header, cookie.toString());
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }
        JSONObject responseJson = JSONObject.parseObject(response.getResponse());
        UserInfoDTO userInfoDTO = JSONObject.parseObject(responseJson.getJSONObject("data").toJSONString(), UserInfoDTO.class);
        userInfoDTO.setUtToken(utToken);
        userInfoDTO.setYgToken(ygToken);
        if (userInfoDTO.getEmail() == null || StringUtils.isBlank(userInfoDTO.getEmail())) {
            throw new CustomException(ErrorCode.EMAIL_NOT_EXIST);
        }
        if (response.getCookies() != null) {
            for (String cookieStr : response.getCookies()) {
                // 解析cookie字符串并添加到响应头
                servletResponse.addHeader("Set-Cookie", cookieStr);
            }
        }

        return userInfoDTO;
    }

    @Override
    public void checkLogin(String utToken, String ygToken, HttpServletResponse servletResponse) {
        getUserInfo(utToken, ygToken, servletResponse);
    }

    private Cookie parseCookieString(String cookieStr) {

        String[] parts = cookieStr.split(";");
        String name = parts[0].split("=")[0];
        String value = parts[0].split("=")[1];
        Cookie cookie = new Cookie(name, value);
        for (String part : parts) {
            String[] keyValue = part.split("=");
            String attName = keyValue[0].trim().toLowerCase();
            String attValue = parts.length > 1 ? parts[1].trim() : "";
            switch (attName) {
                case "path" -> cookie.setPath(attValue);
                case "domain" -> cookie.setDomain(attValue);
                case "secure" -> cookie.setSecure(true);
                case "httponly" -> cookie.setHttpOnly(true);
                case "max-age" -> cookie.setMaxAge(Integer.parseInt(attValue));
            }
        }

        return cookie;
    }
}
