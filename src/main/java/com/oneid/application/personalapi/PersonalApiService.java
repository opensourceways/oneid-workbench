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

import com.oneid.application.personalapi.dto.PersonalApiTokenDTO;
import com.oneid.application.personalapi.dto.PersonalApiTokenDetailDTO;
import com.oneid.application.personalapi.dto.PersonalApiTokenIdDTO;
import com.oneid.application.personalapi.dto.UserInfoDTO;
import org.springframework.http.ResponseEntity;


public interface PersonalApiService {
    /**
     * 创建token
     * @param personalApiTokenDTO 用户token设置DTO
     * @param userInfoDTO 用户账号信息
     */
    void createToken(PersonalApiTokenDTO personalApiTokenDTO, UserInfoDTO userInfoDTO);

    /**
     * 更新token
     * @param personalApiTokenDetailDTO 更新用户信息
     */
    void updateToken(PersonalApiTokenDetailDTO personalApiTokenDetailDTO);

    /**
     * 刷新token
     * @param personalApiTokenIdDTO 需要刷新的token信息
     */
    void refreshToken(PersonalApiTokenIdDTO personalApiTokenIdDTO);

    /**
     * 删除token
     * @param personalApiTokenIdDTO 需要删除的token信息
     */
    void deleteToken(PersonalApiTokenIdDTO personalApiTokenIdDTO);

    /**
     * 获取用户的token
     * @param userId 用户ID
     * @return
     */
    ResponseEntity getPersonalApiTokens(String userId);

    /**
     * 获取所有权限信息
     * @return
     */
    ResponseEntity getAllPermissions();
}
