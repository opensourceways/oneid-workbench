package com.oneid.application.personalapi;

import com.alibaba.fastjson2.JSONObject;
import com.anji.captcha.model.common.ResponseModel;
import com.anji.captcha.model.vo.CaptchaVO;
import com.anji.captcha.service.CaptchaService;
import com.oneid.application.personalapi.dto.CodeInfoDTO;
import com.oneid.application.personalapi.dto.EmailInfoDTO;
import com.oneid.application.personalapi.dto.UserInfoDTO;
import com.oneid.common.constant.CommonConstant;
import com.oneid.common.exception.CustomException;
import com.oneid.common.exception.ErrorCode;
import com.oneid.common.utils.CharacterUtil;
import com.oneid.common.utils.RedisUtil;
import com.oneid.common.utils.ResultUtil;
import com.oneid.infrastructure.dapr.DaprEmailActuator;
import com.oneid.infrastructure.dapr.DaprRedisActuator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class CommonApiServiceImpl implements CommonApiService {

    @Autowired
    CaptchaService captchaService;
    @Autowired
    DaprRedisActuator daprRedisActuator;
    @Autowired
    DaprEmailActuator daprEmailActuator;

    @Override
    public ResponseEntity sendCodeV3(EmailInfoDTO emailInfoDTO, UserInfoDTO userInfoDTO) {
        if (!emailInfoDTO.getAccount().equals(userInfoDTO.getEmail())) {
            throw new CustomException(ErrorCode.EMAIL_NOT_BELONG_USER);
        }
        boolean isSuccess = verifyCaptcha(emailInfoDTO.getCaptchaVerification());
        // 验证码二次校验
        if (!isSuccess) {
            return ResultUtil.result(HttpStatus.BAD_REQUEST, ErrorCode.BAD_REQUEST.getMessage(), null);
        }
        String account = emailInfoDTO.getAccount();
        // 限制1分钟只能发送一次
        String redisKey = RedisUtil.getSendEmailCodeRedisKey(account.toLowerCase());
        String codeOld = daprRedisActuator.getState(redisKey);
        if (codeOld != null) {
            return ResultUtil.result(HttpStatus.BAD_REQUEST, null, ErrorCode.VERIFY_CODE_HAS_BEEN_SENT.getMessage(), null);
        }
        if (!emailInfoDTO.getChannel().equals(CommonConstant.CHANNEL_TYPE_TOKEN_EMAIL_CHECK)) {
            return ResultUtil.result(HttpStatus.BAD_REQUEST, ErrorCode.ONLY_FOR_TOKEN_CHECK.getMessage(), null);
        }

        String code = CharacterUtil.generateRandom6DigitNumber();
        daprEmailActuator.sendMessageCode(account, code, 1);
        //存储验证码，过期时间60s
        daprRedisActuator.saveState(redisKey, code, 60);
        return ResultUtil.result(HttpStatus.OK, "success", null);
    }

    @Override
    public ResponseEntity verifyCode(CodeInfoDTO codeInfoDTO, UserInfoDTO userInfoDTO) {
        if (!codeInfoDTO.getAccount().equals(userInfoDTO.getEmail())) {
            throw new CustomException(ErrorCode.EMAIL_NOT_BELONG_USER);
        }
        String codeRedisKey = RedisUtil.getSendEmailCodeRedisKey(codeInfoDTO.getAccount().toLowerCase());
        String code = daprRedisActuator.getState(codeRedisKey);
        if (StringUtils.isBlank(code)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED, "code is wrong or invalid");
        }
        String emailTokenKey = RedisUtil.getEmailTokenRedisKey(codeInfoDTO.getAccount().toLowerCase());
        String emailToken = CharacterUtil.generate16UUID();
        daprRedisActuator.saveState(emailTokenKey, emailToken, 600);
        JSONObject emailTokenObj = new JSONObject();
        emailTokenObj.put("emailToken", emailToken);
        return ResultUtil.result(HttpStatus.OK, "success", emailTokenObj);
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
}
