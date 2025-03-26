package com.oneid.common.exception;

/**
 * ErrorCode
 */
public enum ErrorCode {
    // 通用错误
    SUCCESS(200, "Success", "成功"),
    BAD_REQUEST(400, "Bad Request", "错误的请求"),
    UNAUTHORIZED(401, "Unauthorized", "未经授权"),
    FORBIDDEN(403, "Forbidden", "禁止访问"),
    NOT_FOUND(404, "Not Found", "资源不存在"),
    INTERNAL_SERVER_ERROR(500, "Internal Server Error", "服务器内部错误"),

    // 自定义业务错误
    PERMISSION_ID_ERROR(1001, "PermissionId wrong", "权限ID错误"),
    SQL_OPT_ERROR(1002, "SQL operate error", "SQL操作错误"),
    TOKEN_NAME_REPEATED(1003, "Token name repeated", "Token名称重复"),
    TOKEN_ERROR(1004, "Token error", "Token错误"),
    ENCRYPT_TOKEN_ERROR(1005, "Encrypt token error", "Token加密错误"),
    TOTAL_TOKEN_EXCEED(1006, "The total number of tokens does not exceed 20", "Token总数不超过20"),

    // 获取用户账号信息错误
    GET_ACCOUNT_INFO_ERROR(2001, "Get Account Info Error", "获取账户信息错误"),
    EMAIL_NOT_EXIST(2002, "Email Not Exist, Please Bind Email", "邮箱不存在，请绑定邮箱"),
    EMAIL_NOT_BELONG_USER(2003, "Email does not Belong to user, Please check", "邮箱不属于该用户，请检查"),
    EMAIL_TOKEN_ERROR(2004, "Email Token Error", "邮箱Token错误"),
    EMAIL_TOKEN_EXPIRED(2005, "Email Token Expired", "邮箱Token已过期"),

    // 通用服务错误码
    VERIFY_CODE_HAS_BEEN_SENT(3001, "Verify Code has been sent", "验证码已发送"),
    ONLY_FOR_TOKEN_CHECK(3002, "For token check use only", "仅用于Token检查");



    private final int code;
    private final String messageEn;
    private final String messageZh;

    ErrorCode(int code, String messageEn, String messageZh) {
        this.code = code;
        this.messageEn = messageEn;
        this.messageZh = messageZh;
    }

    public int getCode() {
        return code;
    }

    public String getMessageEn() {
        return messageEn;
    }

    public String getMessageZh() {
        return messageZh;
    }

    // 根据 code 查找对应的 ErrorCode
    public static ErrorCode fromCode(int code) {
        for (ErrorCode errorCode : ErrorCode.values()) {
            if (errorCode.getCode() == code) {
                return errorCode;
            }
        }
        return null;
    }
}
