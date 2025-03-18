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
public class PersonalApiTokenIdDTO {
    /**
     * ID.
     */
    @Size(min = ParamValidConstant.API_TOKEN_ID_MIN, max = ParamValidConstant.API_TOKEN_ID_MAX)
    @Pattern(regexp = ParamValidConstant.API_TOKEN_ID_REG, message = ParamValidConstant.VALID_MESSAGE)
    @NotNull
    private String id;

    /**
     * 邮箱身份验证.
     */
    @Size(max = ParamValidConstant.EMAIL_TOKEN_MAX, min = ParamValidConstant.EMAIL_TOKEN_MIN)
    @Pattern(regexp = ParamValidConstant.EMAIL_TOKEN_REG, message = ParamValidConstant.VALID_MESSAGE)
    @NotNull
    private String emailToken;
}
