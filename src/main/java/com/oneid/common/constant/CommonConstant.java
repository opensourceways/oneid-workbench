package com.oneid.common.constant;

public class CommonConstant {
    /**
     * emailTokenCheck的channelType常量
     */
    public static final String CHANNEL_TYPE_TOKEN_EMAIL_CHECK = "token_email_check";

    /**
     * 电话号码正则表达式，匹配以加号开头的数字.
     */
    public static final String PHONEREGEX = "^(\\+\\d{7,15})|(\\d{6,11})$";

    /**
     * 邮箱正则表达式，匹配常见邮箱格式.
     */
    public static final String EMAILREGEX = "^[A-Za-z0-9-._\\u4e00-\\u9fa5]{1,40}"
            + "@[a-zA-Z0-9_-]{1,20}(\\.[a-zA-Z0-9_-]{1,20}){1,10}$";
}
