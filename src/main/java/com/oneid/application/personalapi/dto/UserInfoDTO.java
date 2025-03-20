package com.oneid.application.personalapi.dto;

import lombok.Data;

import java.util.List;

@Data
public class UserInfoDTO {

    private String signedUp;
    private List<IdentityDTO> identities;
    private String phoneCountryCode;
    private String phone;
    private String nickname;
    private String photo;
    private String company;
    private String email;
    private String username;
    private String utToken;
    private String ygToken;

    @Data
    public static class IdentityDTO {
        private String loginName;
        private String userIdInIdp;
        private String identity;
        private String userName;
    }
}
