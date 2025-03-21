/* This project is licensed under the Mulan PSL v2.
 You can use this software according to the terms and conditions of the Mulan PSL v2.
 You may obtain a copy of Mulan PSL v2 at:
     http://license.coscl.org.cn/MulanPSL2
 THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR
 PURPOSE.
 See the Mulan PSL v2 for more details.
 Create: 2022
*/

package com.om.controller;

import com.anji.captcha.model.common.RepCodeEnum;
import com.anji.captcha.model.common.ResponseModel;
import com.anji.captcha.model.vo.CaptchaVO;
import com.anji.captcha.service.CaptchaService;
import com.anji.captcha.util.StringUtils;
import com.om.controller.bean.request.PermissionInfo;
import com.om.result.Constant;
import com.om.service.AuthingService;
import com.om.service.LoginService;
import com.om.service.OidcService;
import com.om.service.OneIdManageService;
import com.om.service.ResourceService;
import com.om.service.SendMessageService;
import com.om.service.UserCenterServiceContext;
import com.om.service.inter.UserCenterServiceInter;
import com.om.utils.ClientIPUtil;
import com.om.utils.HttpClientUtils;
import com.om.aop.RequestLimitRedis;
import com.om.authing.AuthingUserToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Map;


/**
 * 控制器类，用于处理与 Authing 相关的请求和逻辑.
 */
@RequestMapping(value = "/oneid")
@RestController
public class AuthingController {
    /**
     * 用于处理 Authing 相关逻辑的服务.
     */
    @Autowired
    private AuthingService authingService;

    /**
     * 用户中心服务上下文信息.
     */
    @Autowired
    private UserCenterServiceContext userCenterServiceContext;

    /**
     * 管理者 service.
     */
    @Autowired
    private OneIdManageService oneIdManageService;

    /**
     * 登录服务.
     */
    @Autowired
    private LoginService loginService;

    /**
     * 验证码服务.
     */
    @Autowired
    private CaptchaService captchaService;

    /**
     * oidc服务.
     */
    @Autowired
    private OidcService oidcService;

    /**
     * 发送短信服务.
     */
    @Autowired
    private SendMessageService sendMessageService;

    /**
     * 资源管理服务.
     */
    @Autowired
    private ResourceService resourceService;

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
    @RequestLimitRedis()
    @RequestMapping(value = "/captcha/get", method = RequestMethod.POST)
    public ResponseModel captchaGet(@RequestBody Map<String, String> data, HttpServletRequest request) {
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
    @RequestLimitRedis
    @RequestMapping(value = "/captcha/check", method = RequestMethod.POST)
    public ResponseModel captchaCheck(@RequestBody Map<String, String> data, HttpServletRequest request) {
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
     * 检查账户是否存在的方法.
     *
     * @param servletRequest HTTP 请求对象
     * @param servletResponse HTTP 响应对象
     * @return 返回 ResponseEntity 对象
     */
    @RequestLimitRedis
    @RequestMapping(value = "/account/exists", method = RequestMethod.GET)
    public ResponseEntity accountExists(HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
        UserCenterServiceInter service = getServiceImpl(servletRequest);
        return service.accountExists(servletRequest, servletResponse);
    }

    /**
     * 发送验证码版本3的方法 POST请求.
     *
     * @param servletRequest HTTP 请求对象
     * @param servletResponse HTTP 响应对象
     * @return 返回 ResponseEntity 对象
     */
    @RequestLimitRedis
    @RequestMapping(value = "/captcha/sendCode", method = RequestMethod.POST)
    public ResponseEntity sendCodeV3(HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
        UserCenterServiceInter service = getServiceImpl(servletRequest);
        Map<String, Object> body = HttpClientUtils.getBodyFromRequest(servletRequest);
        String captchaVerification = (String) body.getOrDefault("captchaVerification", null);
        return service.sendCodeV3(servletRequest, servletResponse, verifyCaptcha(captchaVerification));
    }

    /**
     * 发送验证码版本3的方法 GET请求.
     *
     * @param servletRequest HTTP 请求对象
     * @param servletResponse HTTP 响应对象
     * @param captchaVerification 验证码验证信息
     * @return 返回 ResponseEntity 对象
     */
    @RequestLimitRedis
    @RequestMapping(value = {"/captcha/sendCode", "/v3/sendCode"}, method = RequestMethod.GET)
    public ResponseEntity sendCodeV3(HttpServletRequest servletRequest, HttpServletResponse servletResponse,
                                     @RequestParam("captchaVerification") String captchaVerification) {
        UserCenterServiceInter service = getServiceImpl(servletRequest);
        return service.sendCodeV3(servletRequest, servletResponse, verifyCaptcha(captchaVerification));
    }

    /**
     * 处理验证码登录请求的方法.
     *
     * @param servletRequest HTTP 请求对象
     * @return 返回 ResponseEntity 对象
     */
    @RequestLimitRedis
    @RequestMapping(value = "/captcha/checkLogin", method = {RequestMethod.POST, RequestMethod.GET})
    public ResponseEntity captchaLogin(HttpServletRequest servletRequest) {
        UserCenterServiceInter service = getServiceImpl(servletRequest);
        return service.captchaLogin(servletRequest);
    }

    /**
     * 处理注册请求的方法.
     *
     * @param servletRequest HTTP 请求对象
     * @param servletResponse HTTP 响应对象
     * @return 返回 ResponseEntity 对象
     */
    @RequestLimitRedis
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public ResponseEntity register(HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
        UserCenterServiceInter service = getServiceImpl(servletRequest);
        return service.register(servletRequest, servletResponse);
    }

    /**
     * 处理登录请求的方法.
     *
     * @param servletRequest HTTP 请求对象
     * @param servletResponse HTTP 响应对象
     * @param body 包含登录信息的 Map 对象
     * @return 返回 ResponseEntity 对象
     */
    @RequestLimitRedis
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseEntity login(HttpServletRequest servletRequest,
                                HttpServletResponse servletResponse,
                                @RequestBody Map<String, Object> body) {
        return loginService.login(servletRequest, servletResponse,
                verifyCaptcha((String) body.get("captchaVerification")));
    }

    /**
     * 处理应用程序验证请求的方法.
     *
     * @param servletRequest HTTP 请求对象
     * @param clientId 客户端ID
     * @param redirect 重定向URI
     * @return 返回 ResponseEntity 对象
     */
    @RequestLimitRedis
    @RequestMapping(value = "/app/verify", method = RequestMethod.GET)
    public ResponseEntity appVerify(HttpServletRequest servletRequest,
                                    @RequestParam(value = "client_id") String clientId,
                                    @RequestParam(value = "redirect_uri") String redirect) {
        UserCenterServiceInter service = getServiceImpl(servletRequest);
        return service.appVerify(clientId, redirect);
    }

    /**
     * 处理 OIDC认证请求的方法.
     *
     * @param token 请求中包含的令牌
     * @param clientId 客户端ID
     * @param redirectUri 重定向URI
     * @param responseType 响应类型
     * @param state 状态信息（可选）
     * @param scope 范围
     * @return 返回 ResponseEntity 对象
     */
    @RequestLimitRedis
    @AuthingUserToken
    @RequestMapping(value = "/oidc/auth", method = RequestMethod.GET)
    public ResponseEntity oidcAuth(@CookieValue(value = "_Y_G_", required = false) String token,
                                   @RequestParam(value = "client_id") String clientId,
                                   @RequestParam(value = "redirect_uri") String redirectUri,
                                   @RequestParam(value = "response_type") String responseType,
                                   @RequestParam(value = "state", required = false) String state,
                                   @RequestParam(value = "scope") String scope) {
        return oidcService.oidcAuth(token, clientId, redirectUri, responseType, state, scope);
    }

    /**
     * 处理 OIDC授权请求的方法.
     *
     * @param servletRequest HTTP 请求对象
     * @param servletResponse HTTP 响应对象
     * @return 返回 ResponseEntity 对象
     */
    @RequestLimitRedis(period = 10, count = 1000)
    @RequestMapping(value = "/oidc/authorize", method = RequestMethod.GET)
    public ResponseEntity oidcAuthorize(HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
        return oidcService.oidcAuthorize(servletRequest, servletResponse);
    }

    /**
     * 处理 OIDC令牌请求的方法.
     *
     * @param servletRequest HTTP 请求对象
     * @return 返回 ResponseEntity 对象
     */
    @RequestLimitRedis(period = 10, count = 1000)
    @RequestMapping(value = "/oidc/token", method = RequestMethod.POST)
    public ResponseEntity oidcToken(HttpServletRequest servletRequest) {
        return oidcService.oidcToken(servletRequest);
    }

    /**
     * 处理 OIDC用户信息请求的方法.
     *
     * @param servletRequest HTTP 请求对象
     * @return 返回 ResponseEntity 对象
     */
    @RequestLimitRedis(period = 10, count = 1000)
    @RequestMapping(value = "/oidc/user", method = RequestMethod.GET)
    public ResponseEntity oidcUser(HttpServletRequest servletRequest) {
        return oidcService.userByAccessToken(servletRequest);
    }

    /**
     * 处理注销请求的方法.
     *
     * @param servletRequest HTTP 请求对象
     * @param servletResponse HTTP 响应对象
     * @param token 包含令牌的 Cookie 值（可选）
     * @return 返回 ResponseEntity 对象
     */
    @RequestLimitRedis
    @AuthingUserToken
    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public ResponseEntity logout(HttpServletRequest servletRequest, HttpServletResponse servletResponse,
                                 @CookieValue(value = "_Y_G_", required = false) String token) {
        UserCenterServiceInter service = getServiceImpl(servletRequest);
        return service.logout(servletRequest, servletResponse, token);
    }

        /**
     * 处理刷新用户请求的方法.
     *
     * @param servletRequest HTTP 请求对象
     * @param servletResponse HTTP 响应对象
     * @param token 包含令牌的 Cookie 值（可选）
     * @return 返回 ResponseEntity 对象
     */
    @RequestLimitRedis
    @AuthingUserToken
    @RequestMapping(value = "/user/refresh", method = RequestMethod.GET)
    public ResponseEntity refreshUser(HttpServletRequest servletRequest, HttpServletResponse servletResponse,
                                      @CookieValue(value = "_Y_G_", required = false) String token) {
        UserCenterServiceInter service = getServiceImpl(servletRequest);
        return service.refreshUser(servletRequest, servletResponse, token);
    }

    /**
     * 检测登录状态.
     *
     * @param servletRequest request
     * @param token token信息
     * @return 解析的用户信息
     */
    @RequestLimitRedis
    @AuthingUserToken
    @RequestMapping(value = "/verify/token", method = RequestMethod.GET)
    public ResponseEntity verifyToken(HttpServletRequest servletRequest,
                                      @CookieValue(value = "_Y_G_", required = false) String token) {
        UserCenterServiceInter service = getServiceImpl(servletRequest);
        return service.verifyToken(token);
    }

    /**
     * 获取用户信息的方法.
     *
     * @param token 包含令牌的 Cookie 值（可选）
     * @return 返回 ResponseEntity 对象
     */
    @RequestLimitRedis
    @AuthingUserToken
    @RequestMapping(value = "/user/permission", method = RequestMethod.GET)
    public ResponseEntity getUser(@CookieValue(value = "_Y_G_", required = false) String token) {
        return authingService.authingUserPermission(token);
    }

    /**
     * 获取用户权限的方法.
     *
     * @param token 包含令牌的 Cookie 值（可选）
     * @return 返回 ResponseEntity 对象
     */
    @RequestLimitRedis
    @AuthingUserToken
    @RequestMapping(value = "/user/permissions", method = RequestMethod.GET)
    public ResponseEntity userPermissions(@CookieValue(value = "_Y_G_", required = false) String token) {
        return oneIdManageService.userPermissions(token);
    }

    /**
     * 查询用户是否有权限.
     *
     * @param token 包含令牌的 Cookie 值（可选）
     * @param permissionInfo 请求体
     * @return 是否有权限
     */
    @RequestLimitRedis
    @AuthingUserToken
    @RequestMapping(value = "/user/checkPermission", method = RequestMethod.POST)
    public ResponseEntity checkPermission(@CookieValue(value = "_Y_G_", required = false) String token,
                                          @RequestBody PermissionInfo permissionInfo) {
        return resourceService.checkPermission(token, permissionInfo);
    }

    /**
     * 处理令牌申请请求的方法.
     *
     * @param httpServletRequest HTTP 请求对象
     * @param servletResponse HTTP 响应对象
     * @param code 代码
     * @param permission 权限
     * @param redirect 重定向URI
     * @return 返回 ResponseEntity 对象
     */
    @RequestLimitRedis
    @RequestMapping(value = "/token/apply", method = RequestMethod.GET)
    public ResponseEntity tokenApply(HttpServletRequest httpServletRequest,
                                     HttpServletResponse servletResponse,
                                     @RequestParam(value = "code") String code,
                                     @RequestParam(value = "permission") String permission,
                                     @RequestParam(value = "redirect") String redirect) {
        return authingService.tokenApply(httpServletRequest, servletResponse, code, permission, redirect);
    }

    /**
     * 处理用户信息请求的方法.
     *
     * @param servletRequest HTTP 请求对象
     * @param servletResponse HTTP 响应对象
     * @param token 包含令牌的 Cookie 值（可选）
     * @return 返回 ResponseEntity 对象
     */
    @RequestLimitRedis
    @AuthingUserToken
    @RequestMapping(value = "/personal/center/user", method = RequestMethod.GET)
    public ResponseEntity userInfo(HttpServletRequest servletRequest,
                                   HttpServletResponse servletResponse,
                                   @CookieValue(value = "_Y_G_", required = false) String token) {
        return oneIdManageService.personalCenterUserInfo(servletRequest, servletResponse, token);
    }

    /**
     * 处理删除用户请求的方法.
     *
     * @param httpServletRequest HTTP 请求对象
     * @param servletResponse HTTP 响应对象
     * @param token 包含令牌的 Cookie 值（可选）
     * @return 返回 ResponseEntity 对象
     */
    @RequestLimitRedis
    @AuthingUserToken
    @RequestMapping(value = "/delete/user", method = RequestMethod.GET)
    public ResponseEntity deleteUser(HttpServletRequest httpServletRequest,
                                     HttpServletResponse servletResponse,
                                     @CookieValue(value = "_Y_G_", required = false) String token) {
        return oneIdManageService.deleteUser(httpServletRequest, servletResponse, token);
    }

    /**
     * 发送验证码的方法.
     *
     * @param httpServletRequest HTTP 请求对象
     * @param token 包含令牌的 Cookie 值（可选）
     * @return 返回 ResponseEntity 对象
     */
    @RequestLimitRedis
    @AuthingUserToken
    @RequestMapping(value = "/sendcode", method = RequestMethod.POST)
    public ResponseEntity sendCode(HttpServletRequest httpServletRequest,
                                   @CookieValue(value = "_Y_G_", required = false) String token) {
        Map<String, Object> body = HttpClientUtils.getBodyFromRequest(httpServletRequest);
        String captchaVerification = (String) body.getOrDefault("captchaVerification", null);
        String channel = (String) body.getOrDefault("channel", null);
        String account = (String) body.getOrDefault("account", null);
        return authingService.sendCode(httpServletRequest, token, account, channel, verifyCaptcha(captchaVerification));
    }

    /**
     * 发送验证码的方法,get.
     *
     * @param httpServletRequest 请求对象
     * @param account 账号信息
     * @param channel 通道信息
     * @param token 包含令牌的 Cookie 值（可选）
     * @param captchaVerification 验证码验证信息
     * @return 返回 ResponseEntity 对象
     */
    @RequestLimitRedis
    @AuthingUserToken
    @RequestMapping(value = "/sendcode", method = RequestMethod.GET)
    public ResponseEntity sendCode(HttpServletRequest httpServletRequest,
                                   @RequestParam(value = "account") String account,
                                   @RequestParam(value = "channel") String channel,
                                   @CookieValue(value = "_Y_G_", required = false) String token,
                                   @RequestParam("captchaVerification") String captchaVerification) {
        return authingService.sendCode(httpServletRequest, token, account, channel, verifyCaptcha(captchaVerification));
    }

    /**
     * 发送解绑验证码的方法.
     *
     * @param servletRequest HTTP 请求对象
     * @param servletResponse HTTP 响应对象
     * @return 返回 ResponseEntity 对象
     */
    @RequestLimitRedis
    @AuthingUserToken
    @RequestMapping(value = "/sendcode/unbind", method = RequestMethod.POST)
    public ResponseEntity sendCodeUnbind(HttpServletRequest servletRequest,
                                         HttpServletResponse servletResponse) {
        UserCenterServiceInter service = getServiceImpl(servletRequest);
        Map<String, Object> body = HttpClientUtils.getBodyFromRequest(servletRequest);
        String captchaVerification = (String) body.getOrDefault("captchaVerification", null);
        return service.sendCodeUnbind(servletRequest, servletResponse, verifyCaptcha(captchaVerification));
    }

    /**
     * 发送解绑验证码的方法,get请求.
     *
     * @param servletRequest HTTP 请求对象
     * @param servletResponse HTTP 响应对象
     * @return 返回 ResponseEntity 对象
     */
    @RequestLimitRedis
    @AuthingUserToken
    @RequestMapping(value = "/sendcode/unbind", method = RequestMethod.GET)
    public ResponseEntity sendCodeUnbindForGet(HttpServletRequest servletRequest,
                                         HttpServletResponse servletResponse) {
        UserCenterServiceInter service = getServiceImpl(servletRequest);
        String captchaVerification = servletRequest.getParameter("captchaVerification");
        return service.sendCodeUnbind(servletRequest, servletResponse, verifyCaptcha(captchaVerification));
    }

    /**
     * 处理更新账号信息请求的方法.
     *
     * @param servletRequest HTTP 请求对象
     * @param servletResponse HTTP 响应对象
     * @param token 包含令牌的 Cookie 值（可选）
     * @return 返回 ResponseEntity 对象
     */
    @RequestLimitRedis
    @AuthingUserToken
    @RequestMapping(value = "/update/account", method = RequestMethod.POST)
    public ResponseEntity updateAccount(HttpServletRequest servletRequest,
                                        HttpServletResponse servletResponse,
                                        @CookieValue(value = "_Y_G_", required = false) String token) {
        UserCenterServiceInter service = getServiceImpl(servletRequest);
        return service.updateAccount(servletRequest, servletResponse, token);
    }

    /**
     * 处理更新账号信息请求的方法.
     *
     * @param servletRequest HTTP 请求对象
     * @param servletResponse HTTP 响应对象
     * @param token 包含令牌的 Cookie 值（可选）
     * @return 返回 ResponseEntity 对象
     */
    @RequestLimitRedis
    @AuthingUserToken
    @RequestMapping(value = "/update/account", method = RequestMethod.GET)
    public ResponseEntity updateAccountForGet(HttpServletRequest servletRequest,
                                        HttpServletResponse servletResponse,
                                        @CookieValue(value = "_Y_G_", required = false) String token) {
        UserCenterServiceInter service = getServiceImpl(servletRequest);
        return service.updateAccount(servletRequest, servletResponse, token);
    }

    /**
     * 处理解绑账号请求的方法.
     *
     * @param servletRequest HTTP 请求对象
     * @param servletResponse HTTP 响应对象
     * @param token 包含令牌的 Cookie 值（可选）
     * @return 返回 ResponseEntity 对象
     */
    @RequestLimitRedis
    @AuthingUserToken
    @RequestMapping(value = "/unbind/account", method = RequestMethod.POST)
    public ResponseEntity unbindAccount(HttpServletRequest servletRequest,
                                        HttpServletResponse servletResponse,
                                        @CookieValue(value = "_Y_G_", required = false) String token) {
        UserCenterServiceInter service = getServiceImpl(servletRequest);
        return service.unbindAccount(servletRequest, servletResponse, token);
    }

    /**
     * 处理解绑账号请求的方法.
     *
     * @param servletRequest HTTP 请求对象
     * @param servletResponse HTTP 响应对象
     * @param token 包含令牌的 Cookie 值（可选）
     * @return 返回 ResponseEntity 对象
     */
    @RequestLimitRedis
    @AuthingUserToken
    @RequestMapping(value = "/unbind/account", method = RequestMethod.GET)
    public ResponseEntity unbindAccountForGet(HttpServletRequest servletRequest,
                                        HttpServletResponse servletResponse,
                                        @CookieValue(value = "_Y_G_", required = false) String token) {
        UserCenterServiceInter service = getServiceImpl(servletRequest);
        return service.unbindAccount(servletRequest, servletResponse, token);
    }

    /**
     * 处理绑定账号请求的方法.
     *
     * @param servletRequest HTTP 请求对象
     * @param servletResponse HTTP 响应对象
     * @param token 包含令牌的 Cookie 值（可选）
     * @return 返回 ResponseEntity 对象
     */
    @RequestLimitRedis
    @AuthingUserToken
    @RequestMapping(value = "/bind/account", method = RequestMethod.POST)
    public ResponseEntity bindAccount(HttpServletRequest servletRequest,
                                      HttpServletResponse servletResponse,
                                      @CookieValue(value = "_Y_G_", required = false) String token) {
        UserCenterServiceInter service = getServiceImpl(servletRequest);
        return service.bindAccount(servletRequest, servletResponse, token);
    }

    /**
     * 处理绑定账号请求的方法.
     *
     * @param servletRequest HTTP 请求对象
     * @param servletResponse HTTP 响应对象
     * @param token 包含令牌的 Cookie 值（可选）
     * @return 返回 ResponseEntity 对象
     */
    @RequestLimitRedis
    @AuthingUserToken
    @RequestMapping(value = "/bind/account", method = RequestMethod.GET)
    public ResponseEntity bindAccountWithGet(HttpServletRequest servletRequest,
                                      HttpServletResponse servletResponse,
                                      @CookieValue(value = "_Y_G_", required = false) String token) {
        UserCenterServiceInter service = getServiceImpl(servletRequest);
        return service.bindAccount(servletRequest, servletResponse, token);
    }

    /**
     * 获取连接列表的方法.
     *
     * @param token 包含令牌的 Cookie 值（可选）
     * @return 返回 ResponseEntity 对象
     */
    @RequestLimitRedis
    @AuthingUserToken
    @RequestMapping(value = "/conn/list", method = RequestMethod.GET)
    public ResponseEntity linkConnList(@CookieValue(value = "_Y_G_", required = false) String token) {
        return authingService.linkConnList(token);
    }

    /**
     * 链接账号的方法.
     *
     * @param token 包含令牌的 Cookie 值（可选）
     * @param secondtoken 第二个令牌值
     * @return 返回 ResponseEntity 对象
     */
    @RequestLimitRedis
    @AuthingUserToken
    @RequestMapping(value = "/link/account", method = RequestMethod.GET)
    public ResponseEntity linkAccount(@CookieValue(value = "_Y_G_", required = false) String token,
                                      @RequestParam(value = "secondtoken") String secondtoken) {
        return authingService.linkAccount(token, secondtoken);
    }

    /**
     * 解除账号链接的方法.
     *
     * @param servletRequest 请求入参
     * @param token 包含令牌的 Cookie 值（可选）
     * @param platform 平台信息
     * @return 返回 ResponseEntity 对象
     */
    @RequestLimitRedis
    @AuthingUserToken
    @RequestMapping(value = "/unlink/account", method = RequestMethod.GET)
    public ResponseEntity unLinkAccount(HttpServletRequest servletRequest,
                                        @CookieValue(value = "_Y_G_", required = false) String token,
                                        @RequestParam(value = "platform") String platform) {
        return authingService.unLinkAccount(servletRequest, token, platform);
    }

    /**
     * 更新用户基本信息的方法.
     *
     * @param servletRequest HTTP 请求对象
     * @param servletResponse HTTP 响应对象
     * @param token 包含令牌的 Cookie 值（可选）
     * @param map 包含更新数据的 Map 对象
     * @return 返回 ResponseEntity 对象
     */
    @RequestLimitRedis
    @AuthingUserToken
    @RequestMapping(value = "/update/baseInfo", method = RequestMethod.POST)
    public ResponseEntity updateUserBaseInfo(HttpServletRequest servletRequest,
                                             HttpServletResponse servletResponse,
                                             @CookieValue(value = "_Y_G_", required = false) String token,
                                             @RequestBody Map<String, Object> map) {
        UserCenterServiceInter service = getServiceImpl(servletRequest);
        return service.updateUserBaseInfo(servletRequest, servletResponse, token, map);
    }

    /**
     * 上传用户照片的方法.
     *
     * @param servletRequest HTTP 请求对象
     * @param servletResponse HTTP 响应对象
     * @param token 包含令牌的 Cookie 值（可选）
     * @param file 要上传的文件
     * @return 返回 ResponseEntity 对象
     */
    @RequestLimitRedis
    @AuthingUserToken
    @RequestMapping(value = "/update/photo", method = RequestMethod.POST)
    public ResponseEntity upload(HttpServletRequest servletRequest,
                                 HttpServletResponse servletResponse,
                                 @CookieValue(value = "_Y_G_", required = false) String token,
                                 @RequestParam(value = "file") MultipartFile file) {
        UserCenterServiceInter service = getServiceImpl(servletRequest);
        return service.updatePhoto(servletRequest, servletResponse, token, file);
    }

    /**
     * 获取公钥的方法.
     *
     * @param request HTTP 请求对象
     * @return 返回 ResponseEntity 对象
     */
    @RequestLimitRedis
    @RequestMapping(value = "/public/key", method = RequestMethod.GET)
    public ResponseEntity getPublicKey(HttpServletRequest request) {
        UserCenterServiceInter service = getServiceImpl(request);
        return service.getPublicKey();
    }

    /**
     * 更新密码的方法.
     *
     * @param servletRequest HTTP 请求对象
     * @param servletResponse HTTP 响应对象
     * @return 返回 ResponseEntity 对象
     */
    @RequestLimitRedis
    @AuthingUserToken
    @RequestMapping(value = "/update/password", method = RequestMethod.POST)
    public ResponseEntity updatePassword(HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
        UserCenterServiceInter service = getServiceImpl(servletRequest);
        return service.updatePassword(servletRequest, servletResponse);
    }

    /**
     * 重置密码验证的方法.
     *
     * @param request HTTP 请求对象
     * @return 返回 ResponseEntity 对象
     */
    @RequestLimitRedis
    @RequestMapping(value = "/reset/password/verify", method = RequestMethod.POST)
    public ResponseEntity resetPwdVerify(HttpServletRequest request) {
        UserCenterServiceInter service = getServiceImpl(request);
        return service.resetPwdVerify(request);
    }

    /**
     * 重置密码的方法.
     *
     * @param servletRequest HTTP 请求对象
     * @param servletResponse HTTP 响应对象
     * @return 返回 ResponseEntity 对象
     */
    @RequestLimitRedis
    @RequestMapping(value = "/reset/password", method = RequestMethod.POST)
    public ResponseEntity resetPwd(HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
        UserCenterServiceInter service = getServiceImpl(servletRequest);
        return service.resetPwd(servletRequest, servletResponse);
    }

    /**
     * 合并账号.
     *
     * @param servletRequest 请求体
     * @param servletResponse 响应体
     * @param token 会话信息
     * @return 合并后用户登录信息
     */
    @RequestLimitRedis
    @AuthingUserToken
    @RequestMapping(value = "/merge/user", method = RequestMethod.POST)
    public ResponseEntity mergeUser(HttpServletRequest servletRequest,
                                    HttpServletResponse servletResponse,
                                    @CookieValue(value = "_Y_G_", required = false) String token) {
        UserCenterServiceInter service = getServiceImpl(servletRequest);
        return service.mergeUser(servletRequest, servletResponse, token);
    }

    private UserCenterServiceInter getServiceImpl(HttpServletRequest servletRequest) {
        String community = servletRequest.getParameter("community");
        if (community == null) {
            Map<String, Object> body = HttpClientUtils.getBodyFromRequest(servletRequest);
            community = (String) body.getOrDefault("community", null);
        }

        String serviceType =
                (community == null
                        || community.toLowerCase().equals(Constant.ONEID_VERSION_V1)
                        || community.toLowerCase().equals(Constant.ONEID_VERSION_V2)
                        || community.toLowerCase().equals(Constant.OPEN_MIND)
                        || community.toLowerCase().equals(Constant.OPEN_UBMC))
                        ? Constant.AUTHING : community.toLowerCase();
        return userCenterServiceContext.getUserCenterService(serviceType);
    }

    private boolean verifyCaptcha(String captchaVerification) {
        CaptchaVO captchaVO = new CaptchaVO();
        captchaVO.setCaptchaVerification(captchaVerification);
        ResponseModel response = captchaService.verification(captchaVO);
        if (response != null) {
            return response.isSuccess();
        }
        return false;
    }

    /**
     * 发送短信的方法.
     *
     * @param map 短信入参对象字符串
     * @param servletRequest HTTP 请求对象
     * @return 返回 ResponseEntity 对象
     */
    @RequestLimitRedis
    @RequestMapping(value = "/sendMessage", method = RequestMethod.POST)
    public Object sendMessage(@RequestBody String map, HttpServletRequest servletRequest) throws Exception {
        return sendMessageService.getMessage(map, servletRequest);
    }
}
