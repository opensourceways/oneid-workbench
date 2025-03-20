package com.oneid.application.personalapi;

import com.alibaba.fastjson2.JSONObject;
import com.oneid.application.personalapi.dto.UserInfoDTO;
import com.oneid.common.exception.CustomException;
import com.oneid.common.exception.ErrorCode;
import com.oneid.common.utils.HttpUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class AccountApiServiceImpl implements AccountApiService {

    @Value("${account.person.center.url:https://openeuler-usercenter.test.osinfra.cn/oneid/personal/center/user}")
    public String accountUrl;

    @Override
    public UserInfoDTO getUserInfo(String utToken, String ygToken) {
        Map<String, String> header = new HashMap<String, String>();
        header.put("token", utToken);
        StringBuilder cookie = new StringBuilder();
        cookie.append("_Y_G_=").append(ygToken);
        String response= null;
        try {
            response = HttpUtil.sendGetWithHeaderAndCookie(accountUrl, null, header, cookie.toString());
        } catch (IOException e) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }
        JSONObject responseJson = JSONObject.parseObject(response);
        return JSONObject.parseObject(responseJson.getJSONObject("data").toJSONString(), UserInfoDTO.class);
    }
}
