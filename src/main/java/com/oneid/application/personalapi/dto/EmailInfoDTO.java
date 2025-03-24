package com.oneid.application.personalapi.dto;

import com.oneid.common.constant.ParamValidConstant;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailInfoDTO {

    /**
     * 邮箱地址
     */
    @Size(max = ParamValidConstant.EMAIL_MAX, min = ParamValidConstant.EMAIL_MIN)
    @Pattern(regexp = ParamValidConstant.EMAIL_REG, message = ParamValidConstant.VALID_MESSAGE)
    @NotNull
    private String account;

    /**
     * 验证信息
     */
    @NotNull
    private String captchaVerification;

    /**
     *使用渠道
     */
    @NotNull
    private String channel;
}
