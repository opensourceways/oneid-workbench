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
import com.anji.captcha.model.common.RepCodeEnum;
import com.anji.captcha.model.common.ResponseModel;
import com.anji.captcha.model.vo.CaptchaVO;
import com.anji.captcha.service.CaptchaService;
import com.anji.captcha.util.StringUtils;
import com.oneid.application.personalapi.AccountApiServiceImpl;
import com.oneid.application.personalapi.CommonApiServiceImpl;
import com.oneid.application.personalapi.dto.CodeInfoDTO;
import com.oneid.application.personalapi.dto.EmailInfoDTO;
import com.oneid.application.personalapi.dto.UserInfoDTO;
import com.oneid.common.utils.ClientIPUtil;
import com.oneid.common.utils.ResultUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * CommonAPIController 通用 API 控制器，处理通用的 API 端点和功能.
 */
@RequestMapping(value = "/oneid-workbench")
@RestController
public class CommonAPIController {
    /**
     * 验证码服务.
     */
    @Autowired
    private CaptchaService captchaService;

    /**
     * 通用服务
     */
    @Autowired
    private CommonApiServiceImpl commonApiServiceImpl;
    /**
     * 获取账号信息服务
     */
    @Autowired
    private AccountApiServiceImpl accountApiServiceImpl;

    /**
     * 当前工作台是否为新版本
     */
    @Value("${personal.token.version.is.new}")
    private boolean isNewVersion;

    /**
     * 表示服务正常状态的常量.
     */
    private static final String SERVICE_STATUS_NORMAL = "normal";

    /**
     * 检查 OM 服务的方法.
     *
     * @return SERVICE_STATUS_NORMAL
     */
    @RequestMapping(value = "/checkOmService", method = RequestMethod.GET)
    public String checkOmService() {
        return SERVICE_STATUS_NORMAL;
    }

    /**
     * 从 HttpServletRequest 中获取远程主机的 IP 地址或者主机名.
     *
     * @param request HTTP 请求对象
     * @return 返回远程主机的 IP 地址或者主机名
     */
    public static String getRemoteId(HttpServletRequest request) {
        String ip = ClientIPUtil.getClientIpAddress(request);
        String ua = request.getHeader("user-agent");
        if (StringUtils.isNotBlank(ip)) {
            return ip + ua;
        }
        return request.getRemoteAddr() + ua;
    }

    /**
     * 处理获取验证码请求的方法.
     *
     * @param data    包含验证码信息的 CaptchaVO 对象
     * @param request HTTP 请求对象
     * @return 返回响应模型 ResponseModel
     */
    @RequestMapping(value = "/captcha/get", method = RequestMethod.POST)
    public ResponseModel captchaGet(@RequestBody Map<String, String> data, HttpServletRequest request,
                                    HttpServletResponse servletResponse,
                                    @CookieValue(value = "_Y_G_") String ygToken) {
        String token = request.getHeader("token");
        accountApiServiceImpl.checkLogin(token, ygToken, servletResponse);
        CaptchaVO captchaVO = new CaptchaVO();
        if (!"blockPuzzle".equals(data.get("captchaType"))) {
            return ResponseModel.errorMsg(RepCodeEnum.ERROR);
        }
        captchaVO.setCaptchaType(data.get("captchaType"));
        captchaVO.setBrowserInfo(getRemoteId(request));
        return captchaService.get(captchaVO);
    }

    /**
     * 处理验证码检查请求的方法.
     *
     * @param data    包含验证码信息的 CaptchaVO 对象
     * @param request HTTP 请求对象
     * @return 返回响应模型 ResponseModel
     */
    @RequestMapping(value = "/captcha/check", method = RequestMethod.POST)
    public ResponseModel captchaCheck(@RequestBody Map<String, String> data, HttpServletRequest request,
                                      HttpServletResponse servletResponse,
                                      @CookieValue(value = "_Y_G_") String ygToken) {
        String token = request.getHeader("token");
        accountApiServiceImpl.checkLogin(token, ygToken, servletResponse);
        CaptchaVO captchaVO = new CaptchaVO();
        if (!"blockPuzzle".equals(data.get("captchaType"))) {
            return ResponseModel.errorMsg(RepCodeEnum.ERROR);
        }
        captchaVO.setCaptchaType(data.get("captchaType"));
        captchaVO.setPointJson(data.get("pointJson"));
        captchaVO.setToken(data.get("token"));
        captchaVO.setBrowserInfo(getRemoteId(request));
        return captchaService.check(captchaVO);
    }


    /**
     * 发送验证码版本3的方法 POST请求.
     *
     * @param servletRequest  HTTP 请求对象
     * @param servletResponse HTTP 响应对象
     * @return 返回 ResponseEntity 对象
     */
    @RequestMapping(value = "/captcha/sendCode", method = RequestMethod.POST)
    public ResponseEntity sendCodeV3(HttpServletRequest servletRequest, HttpServletResponse servletResponse,
                                     @Valid @RequestBody EmailInfoDTO emailInfoDTO,
                                     @CookieValue(value = "_Y_G_") String ygToken) {
        String token = servletRequest.getHeader("token");
        UserInfoDTO userInfoDTO = accountApiServiceImpl.getUserInfo(token, ygToken, servletResponse);
        return commonApiServiceImpl.sendCodeV3(emailInfoDTO, userInfoDTO);
    }

    /**
     * 验证验证码是否正确 POST请求
     */
    @RequestMapping(value = "/captcha/verify", method = RequestMethod.POST)
    public ResponseEntity verifyCode(HttpServletRequest request, HttpServletResponse servletResponse,
                                     @Valid @RequestBody CodeInfoDTO codeInfoDTO,
                                     @CookieValue(value = "_Y_G_") String ygToken) {
        String token = request.getHeader("token");
        UserInfoDTO userInfoDTO = accountApiServiceImpl.getUserInfo(token, ygToken, servletResponse);
        return commonApiServiceImpl.verifyCode(codeInfoDTO, userInfoDTO);
    }

    /**
     * 检查当前工作台使用的是否为新版本
     *
     * @return 返回 ResponseEntity 对象
     */
    @RequestMapping(value = "/version/check", method = RequestMethod.GET)
    public ResponseEntity versionCheck() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("isNewVersion", isNewVersion);
        return ResultUtil.result(HttpStatus.OK, "success", jsonObject);
    }


}
