/* Copyright (c) 2025 openEuler Community
 oneid-workbench is licensed under the Mulan PSL v2.
 You can use this software according to the terms and conditions of the Mulan PSL v2.
 You may obtain a copy of Mulan PSL v2 at:
     http://license.coscl.org.cn/MulanPSL2
 THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND,
 EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT,
 MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 See the Mulan PSL v2 for more details.
*/

package com.oneid.application.personalapi.dto;

import lombok.Data;

import java.util.List;

@Data
public class UserInfoDTO {

    /**
     * 第三方身份认证信息
     */
    private List<IdentityDTO> identities;

    /**
     * 电话号码
     */
    private String phone;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 公司信息
     */
    private String company;

    /**
     * 邮箱地址
     */
    private String email;

    /**
     * 用户名
     */
    private String username;

    /**
     * 用户Id
     */
    private String userId;

    /**
     * _U_T_
     */
    private String utToken;

    /**
     * _Y_G_
     */
    private String ygToken;

    @Data
    public static class IdentityDTO {

        /**
         * 登录名称
         */
        private String loginName;

        /**
         * 第三方用户名
         */
        private String userName;
    }
}
