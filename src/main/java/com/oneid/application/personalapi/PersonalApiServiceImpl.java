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
import com.oneid.application.personalapi.vo.PersonalApiTokenVO;
import com.oneid.infrastructure.personalapi.PersonalApiMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PersonalApiServiceImpl implements PersonalApiService {

    @Autowired
    private PersonalApiMapper personalApiMapper;

    @Override
    public JSONObject createToken(PersonalApiTokenDTO personalApiTokenDTO, UserInfoDTO userInfoDTO) {
        String token = personalApiMapper.createPersonalApiToken(personalApiTokenDTO, userInfoDTO);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("token", token);
        return jsonObject;
    }

    @Override
    public void updateToken(PersonalApiTokenDetailDTO personalApiTokenDetailDTO, UserInfoDTO userInfoDTO) {
        personalApiMapper.updatePersonalApiToken(personalApiTokenDetailDTO, userInfoDTO);
    }

    @Override
    public JSONObject refreshToken(PersonalApiTokenIdDTO personalApiTokenIdDTO, UserInfoDTO userInfoDTO) {
        String newToken = personalApiMapper.refreshPersonApiToken(personalApiTokenIdDTO, userInfoDTO);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("token", newToken);
        return jsonObject;
    }

    @Override
    public void deleteToken(PersonalApiTokenIdDTO personalApiTokenIdDTO, UserInfoDTO userInfoDTO) {
        personalApiMapper.deletePersonalApiToken(personalApiTokenIdDTO, userInfoDTO);
    }

    @Override
    public ResponseEntity getPersonalApiTokens(String userId) {
        return personalApiMapper.getPersonalApiTokensByUserId(userId);
    }

    @Override
    public ResponseEntity getAllPermissions() {
        return personalApiMapper.getAllPermissions();
    }

    @Override
    public ResponseEntity checkToken(CheckTokenDTO checkTokenDTO) {
        return personalApiMapper.checkToken(checkTokenDTO);
    }
}
