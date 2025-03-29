package com.oneid.application.personalapi;

import com.oneid.application.personalapi.dto.CodeInfoDTO;
import com.oneid.application.personalapi.dto.EmailInfoDTO;
import com.oneid.application.personalapi.dto.UserInfoDTO;
import org.springframework.http.ResponseEntity;

public interface CommonApiService {
    /**
     * 发送邮件
     *
     * @param emailInfoDTO 邮件信息
     * @return ResponseEntity 响应实体
     */
    ResponseEntity sendCodeV3(EmailInfoDTO emailInfoDTO, UserInfoDTO userInfoDTO);

    /**
     * 验证验证码
     *
     * @param codeInfoDTO 验证码信息
     * @return ResponseEntity 响应实体
     */
    ResponseEntity verifyCode(CodeInfoDTO codeInfoDTO, UserInfoDTO userInfoDTO);
}
