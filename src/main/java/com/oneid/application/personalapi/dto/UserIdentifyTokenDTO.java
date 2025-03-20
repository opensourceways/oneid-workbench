package com.oneid.application.personalapi.dto;

import lombok.Data;

@Data
public class UserIdentifyTokenDTO {
    /**
     * 用户登录时携带的token：_U_T_
     */
    private String utToken;
    /**
     * 用户登录时携带的token：_Y_G_
     */
    private String ygToken;
}
