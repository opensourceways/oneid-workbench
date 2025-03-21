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

import lombok.Data;

@Data
public class UserIdentifyTokenDTO {
    /**
     * 用户登录时携带的token：_U_T_
     */
    private String utToken;
    /**
     * 用户登录时携带的token：_Y_G_
     */
    private String ygToken;
}
