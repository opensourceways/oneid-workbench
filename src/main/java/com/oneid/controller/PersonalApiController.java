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

package com.oneid.controller;

import com.oneid.application.personalapi.PersonalApiService;
import com.oneid.application.personalapi.PersonalApiServiceImpl;
import com.oneid.application.personalapi.dto.PersonalApiTokenDTO;
import com.oneid.application.personalapi.dto.PersonalApiTokenDetailDTO;
import com.oneid.application.personalapi.dto.PersonalApiTokenIdDTO;
import com.oneid.application.personalapi.vo.PersonalApiTokenVO;
import com.oneid.common.utils.ResultUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 私人令牌.
 */
@RequestMapping(value = "/oneid-workbench/openapi")
@RestController
public class PersonalApiController {
    /**
     * 日志记录器.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(PersonalApiController.class);

    @Autowired
    private PersonalApiServiceImpl personalApiServiceImpl;

    /**
     * 创建令牌.
     *
     * @param servletRequest 请求
     * @param personalApiTokenDTO 请求体
     * @return 令牌信息
     */
    @RequestMapping(value = "/createToken", method = RequestMethod.POST)
    public ResponseEntity createToken(HttpServletRequest servletRequest,
                                              @Valid @RequestBody PersonalApiTokenDTO personalApiTokenDTO) {
        HashMap<String, String> tokenMap = new HashMap<>();
        tokenMap.put("token", "sdfdsfsdfsdf");
        personalApiServiceImpl.createToken(personalApiTokenDTO);
        return ResultUtil.result(HttpStatus.OK, "success", tokenMap);
    }

    /**
     * 更新详情.
     *
     * @param servletRequest 请求
     * @param personalApiTokenDetailDTO 请求体
     * @return 更新结果
     */
    @RequestMapping(value = "/updateToken", method = RequestMethod.POST)
    public ResponseEntity updateToken(HttpServletRequest servletRequest,
                                       @Valid @RequestBody PersonalApiTokenDetailDTO personalApiTokenDetailDTO) {
        return ResultUtil.result(HttpStatus.OK, "success", null);
    }

    /**
     * 刷新token.
     *
     * @param servletRequest 请求
     * @param personalApiTokenIdDTO 请求体
     * @return 刷新的token
     */
    @RequestMapping(value = "/refreshToken", method = RequestMethod.POST)
    public ResponseEntity refreshToken(HttpServletRequest servletRequest,
                                      @Valid @RequestBody PersonalApiTokenIdDTO personalApiTokenIdDTO) {
        HashMap<String, String> tokenMap = new HashMap<>();
        tokenMap.put("token", "sdfdsfsdfsdf");
        personalApiServiceImpl.refreshToken(personalApiTokenIdDTO);
        return ResultUtil.result(HttpStatus.OK, "success", tokenMap);
    }

    /**
     * 删除token.
     *
     * @param servletRequest 请求
     * @param personalApiTokenIdDTO 请求体
     * @return 删除结果
     */
    @RequestMapping(value = "/deleteToken", method = RequestMethod.POST)
    public ResponseEntity deleteToken(HttpServletRequest servletRequest,
                                       @Valid @RequestBody PersonalApiTokenIdDTO personalApiTokenIdDTO) {
        HashMap<String, String> tokenMap = new HashMap<>();
        personalApiServiceImpl.deleteToken(personalApiTokenIdDTO);
        return ResultUtil.result(HttpStatus.OK, "success", tokenMap);
    }

    /**
     * 获取私人令牌信息.
     *
     * @param servletRequest 请求
     * @param userId 用户Id
     * @return 私人令牌信息
     */
    @RequestMapping(value = "/getToken", method = RequestMethod.GET)
    public ResponseEntity getToken(HttpServletRequest servletRequest, @Valid @RequestParam String userId) {
        List<PersonalApiTokenVO> personalApiTokenVOS = personalApiServiceImpl.getPersonalApiTokens(userId);
        return ResultUtil.result(HttpStatus.OK, "success", personalApiTokenVOS);
    }

    /**
     * 获取私人令牌权限列表信息.
     *
     * @param servletRequest 请求
     * @return 私人令牌权限信息
     */
    @RequestMapping(value = "/getAllPermissions", method = RequestMethod.GET)
    public ResponseEntity getAllPermissions(HttpServletRequest servletRequest) {
        List<PersonalApiTokenVO> personalApiTokenVOS = new ArrayList<>();
        return ResultUtil.result(HttpStatus.OK, "success", personalApiTokenVOS);
    }
}
