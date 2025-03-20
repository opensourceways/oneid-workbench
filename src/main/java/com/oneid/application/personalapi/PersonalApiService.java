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
    void createToken(PersonalApiTokenDTO personalApiTokenDTO, UserInfoDTO userInfoDTO);

    void updateToken(PersonalApiTokenDetailDTO personalApiTokenDetailDTO);

    void refreshToken(PersonalApiTokenIdDTO personalApiTokenIdDTO);

    void deleteToken(PersonalApiTokenIdDTO personalApiTokenIdDTO);

    ResponseEntity getPersonalApiTokens(String userId);

    ResponseEntity getAllPermissions();
}
