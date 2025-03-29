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

package com.oneid.common.constant;

public class ParamValidConstant {
    /**
     * 参数错误.
     */
    public static final String VALID_MESSAGE = "param is invalid";

    /**
     * 私人令牌name正则.
     */
    public static final String API_TOKEN_NAME_REG = "^[0-9a-zA-Z_\\-\u4e00-\u9fa5]{1,20}$";

    /**
     * 私人令牌name最小长度.
     */
    public static final int API_TOKEN_NAME_MIN = 1;

    /**
     * 私人令牌name最大长度.
     */
    public static final int API_TOKEN_NAME_MAX = 20;

    /**
     * 私人令牌权限正则.
     */
    public static final String API_TOKEN_PERMISSIONIDS_REG = "^[0-9a-zA-Z,]{1,500}$";

    /**
     * 私人令牌权限最小长度.
     */
    public static final int API_TOKEN_PERMISSIONIDS_MIN = 1;

    /**
     * 私人令牌权限最大长度.
     */
    public static final int API_TOKEN_PERMISSIONIDS_MAX = 500;

    /**
     * 私人令牌最大有效时长.
     */
    public static final int API_TOKEN_DAYNUM_MIN = 1;

    /**
     * 私人令牌最小有效时长.
     */
    public static final int API_TOKEN_DAYNUM_MAX = 365;

    /**
     * 私人令牌ID正则.
     */
    public static final String API_TOKEN_ID_REG = "^[0-9a-zA-Z-]{1,40}$";

    /**
     * 私人令牌ID最小长度.
     */
    public static final int API_TOKEN_ID_MIN = 1;

    /**
     * 私人令牌ID最大长度.
     */
    public static final int API_TOKEN_ID_MAX = 40;

    /**
     * 邮箱身份验证正则.
     */
    public static final String EMAIL_TOKEN_REG = "^[0-9a-zA-Z]{1,32}$";

    /**
     * 邮箱身份验证最小长度.
     */
    public static final int EMAIL_TOKEN_MIN = 1;

    /**
     * 邮箱身份验证最大长度.
     */
    public static final int EMAIL_TOKEN_MAX = 32;

    /**
     * 邮箱验证正则
     */
    public static final String EMAIL_REG = "^[A-Za-z0-9-._\\u4e00-\\u9fa5]{1,40}"
            + "@[a-zA-Z0-9_-]{1,20}(\\.[a-zA-Z0-9_-]{1,20}){1,10}$";

    /**
     * 邮箱最小长度
     */
    public static final int EMAIL_MIN = 1;

    /**
     *邮箱最大长度
     */
    public static final int EMAIL_MAX = 80;

    /**
     * 验证码正则
     */
    public static final String CODE_REG = "^[0-9a-zA-Z]{1,20}$";

    /**
     *验证码最小长度
     */
    public static final int CODE_MIN = 1;

    /**
     * 验证码最大长度
     */
    public static final int CODE_MAX = 20;
}
