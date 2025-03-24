package com.oneid.application.personalapi.dto;

import com.oneid.common.constant.ParamValidConstant;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CodeInfoDTO {

    /**
     * 验证码
     */
    @Size(max = ParamValidConstant.CODE_MAX, min = ParamValidConstant.CODE_MIN)
    @Pattern(regexp = ParamValidConstant.CODE_REG, message = ParamValidConstant.VALID_MESSAGE)
    @NotNull
    private String code;

    /**
     * 邮箱地址
     */
    @Size(max = ParamValidConstant.EMAIL_MAX, min = ParamValidConstant.EMAIL_MIN)
    @Pattern(regexp = ParamValidConstant.EMAIL_REG, message = ParamValidConstant.VALID_MESSAGE)
    @NotNull
    private String account;
}
