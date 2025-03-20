package com.oneid.application.personalapi;


import com.oneid.application.personalapi.dto.UserInfoDTO;

public interface AccountApiService {
    UserInfoDTO getUserInfo(String utToken, String ygToken);
}
