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

import com.alibaba.fastjson2.JSONObject;
import com.oneid.application.personalapi.AccountApiService;
import com.oneid.application.personalapi.AccountApiServiceImpl;
import com.oneid.application.personalapi.PersonalApiServiceImpl;
import com.oneid.application.personalapi.dto.PersonalApiTokenDTO;
import com.oneid.application.personalapi.dto.PersonalApiTokenDetailDTO;
import com.oneid.application.personalapi.dto.PersonalApiTokenIdDTO;
import com.oneid.application.personalapi.dto.UserInfoDTO;
import com.oneid.common.exception.CustomException;
import com.oneid.common.exception.ErrorCode;
import com.oneid.common.utils.ResultUtil;
import com.oneid.infrastructure.dapr.DaprEmailActuator;
import com.oneid.infrastructure.dapr.DaprRedisActuator;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    @Autowired
    private AccountApiServiceImpl accountApiServiceImpl;
    @Autowired
    private DaprEmailActuator daprEmailActuator;
    @Autowired
    private DaprRedisActuator daprRedisActuator;

    /**
     * 创建令牌.
     *
     * @param servletRequest      请求
     * @param personalApiTokenDTO 请求体
     * @param ygToken             包含令牌的 Cookie 值（可选）
     * @return 令牌信息
     */
    @RequestMapping(value = "/createToken", method = RequestMethod.POST)
    public ResponseEntity createToken(HttpServletRequest servletRequest,
                                      @Valid @RequestBody PersonalApiTokenDTO personalApiTokenDTO,
                                      @CookieValue(value = "_Y_G_", required = false) String ygToken) {
        String token = servletRequest.getHeader("token");
        UserInfoDTO userInfoDTO = accountApiServiceImpl.getUserInfo(token, ygToken);
        personalApiServiceImpl.createToken(personalApiTokenDTO, userInfoDTO);
        return ResultUtil.result(HttpStatus.OK, "success", null);
    }

    /**
     * 更新详情.
     *
     * @param servletRequest            请求
     * @param personalApiTokenDetailDTO 请求体
     * @param ygToken                   包含令牌的 Cookie 值（可选）
     * @return 更新结果
     */
    @RequestMapping(value = "/updateToken", method = RequestMethod.POST)
    public ResponseEntity updateToken(HttpServletRequest servletRequest,
                                      @Valid @RequestBody PersonalApiTokenDetailDTO personalApiTokenDetailDTO,
                                      @CookieValue(value = "_Y_G_", required = false) String ygToken) {
        String token = servletRequest.getHeader("token");
        accountApiServiceImpl.checkLogin(token, ygToken);
        personalApiServiceImpl.updateToken(personalApiTokenDetailDTO);
        return ResultUtil.result(HttpStatus.OK, "success", null);
    }

    /**
     * 刷新token.
     *
     * @param servletRequest        请求
     * @param personalApiTokenIdDTO 请求体
     * @param ygToken               包含令牌的 Cookie 值（可选）
     * @return 刷新的token
     */
    @RequestMapping(value = "/refreshToken", method = RequestMethod.POST)
    public ResponseEntity refreshToken(HttpServletRequest servletRequest,
                                       @Valid @RequestBody PersonalApiTokenIdDTO personalApiTokenIdDTO,
                                       @CookieValue(value = "_Y_G_", required = false) String ygToken) {
        String token = servletRequest.getHeader("token");
        accountApiServiceImpl.checkLogin(token, ygToken);
        personalApiServiceImpl.refreshToken(personalApiTokenIdDTO);
        return ResultUtil.result(HttpStatus.OK, "success", null);
    }

    /**
     * 删除token.
     *
     * @param servletRequest        请求
     * @param personalApiTokenIdDTO 请求体
     * @param ygToken               包含令牌的 Cookie 值（可选）
     * @return 删除结果
     */
    @RequestMapping(value = "/deleteToken", method = RequestMethod.POST)
    public ResponseEntity deleteToken(HttpServletRequest servletRequest,
                                      @Valid @RequestBody PersonalApiTokenIdDTO personalApiTokenIdDTO,
                                      @CookieValue(value = "_Y_G_", required = false) String ygToken) {
        String token = servletRequest.getHeader("token");
        accountApiServiceImpl.checkLogin(token, ygToken);
        personalApiServiceImpl.deleteToken(personalApiTokenIdDTO);
        return ResultUtil.result(HttpStatus.OK, "success", null);
    }

    /**
     * 获取私人令牌信息.
     *
     * @param servletRequest 请求
     * @param ygToken        包含令牌的 Cookie 值（可选）
     * @return 私人令牌信息
     */
    @RequestMapping(value = "/getToken", method = RequestMethod.GET)
    public ResponseEntity getToken(HttpServletRequest servletRequest,
                                   @CookieValue(value = "_Y_G_", required = false) String ygToken) {
        String token = servletRequest.getHeader("token");
        UserInfoDTO userInfoDTO = accountApiServiceImpl.getUserInfo(token, ygToken);
        return personalApiServiceImpl.getPersonalApiTokens(userInfoDTO.getUserId());
    }

    /**
     * 获取私人令牌权限列表信息.
     *
     * @param servletRequest 请求
     * @param ygToken        包含令牌的 Cookie 值（可选）
     * @return 私人令牌权限信息
     */
    @RequestMapping(value = "/getAllPermissions", method = RequestMethod.GET)
    public ResponseEntity getAllPermissions(HttpServletRequest servletRequest,
                                            @CookieValue(value = "_Y_G_", required = false) String ygToken) {
        String token = servletRequest.getHeader("token");
        accountApiServiceImpl.checkLogin(token, ygToken);
        return personalApiServiceImpl.getAllPermissions();
    }

    /**
     * 发送邮件测试
     */
    @RequestMapping(value = "/sendEmail", method = RequestMethod.GET)
    public ResponseEntity sendEmail() {
        String zhouEmail = "zhouyi198@h-partners.com";
        if (!daprEmailActuator.sendMessageCode(zhouEmail, "66666", 30)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }
        return ResultUtil.result(HttpStatus.OK, "success", null);
    }

    /**
     * 测试Redis
     */
    @RequestMapping(value = "/redis", method = RequestMethod.GET)
    public ResponseEntity redis() {
        daprRedisActuator.saveState("zhouyi", "18");
        JSONObject result = new JSONObject();
        result.put("result", Integer.parseInt(daprRedisActuator.getState("zhouyi")) + 5);
        daprRedisActuator.deleteState("zhouyi");
        result.put("delete", daprRedisActuator.getState("zhouyi"));
        return ResultUtil.result(HttpStatus.OK, "success", result);
    }
}
