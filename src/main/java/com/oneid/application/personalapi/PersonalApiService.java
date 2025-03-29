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

package com.oneid.application.personalapi;

import com.alibaba.fastjson2.JSONObject;
import com.oneid.application.personalapi.dto.*;
import org.springframework.http.ResponseEntity;


public interface PersonalApiService {
    /**
     * 创建token
     * @param personalApiTokenDTO 用户token设置DTO
     * @param userInfoDTO 用户账号信息
     */
    JSONObject createToken(PersonalApiTokenDTO personalApiTokenDTO, UserInfoDTO userInfoDTO);

    /**
     * 更新token
     * @param personalApiTokenDetailDTO 更新用户信息
     */
    void updateToken(PersonalApiTokenDetailDTO personalApiTokenDetailDTO, UserInfoDTO userInfoDTO);

    /**
     * 刷新token
     * @param personalApiTokenIdDTO 需要刷新的token信息
     */
    JSONObject refreshToken(PersonalApiTokenIdDTO personalApiTokenIdDTO, UserInfoDTO userInfoDTO);

    /**
     * 删除token
     * @param personalApiTokenIdDTO 需要删除的token信息
     */
    void deleteToken(PersonalApiTokenIdDTO personalApiTokenIdDTO, UserInfoDTO userInfoDTO);

    /**
     * 获取用户的token
     * @param userId 用户ID
     * @return 响应体
     */
    ResponseEntity getPersonalApiTokens(String userId);

    /**
     * 获取所有权限信息
     * @return 响应体
     */
    ResponseEntity getAllPermissions();

    /**
     * 检查token是否有请求对应url权限
     */
    ResponseEntity checkToken(CheckTokenDTO checkTokenDTO);
}
