package com.oneid.common.exception;

/**
 * ErrorCode
 */
public enum ErrorCode {
    // 通用错误
    SUCCESS(200, "Success"),
    BAD_REQUEST(400, "Bad Request"),
    UNAUTHORIZED(401, "Unauthorized"),
    FORBIDDEN(403, "Forbidden"),
    NOT_FOUND(404, "Not Found"),
    INTERNAL_SERVER_ERROR(500, "Internal Server Error"),

    // 自定义业务错误
    PERMISSION_ID_ERROR(1001, "PermissionId wrong"),
    SQL_OPT_ERROR(1002, "SQL operate error"),
    TOKEN_NAME_REPEATED(1003, "Token name repeated"),
    TOKEN_ERROR(1004, "Token error"),

    //获取用户账号信息错误
    GET_ACCOUNT_INFO_ERROR(2001, "Get Account Info Error"),
    EMAIL_NOT_EXIST(2002, "Email Not Exist, Please Bind Email"),
    EMAIL_NOT_BELONG_USER(2003, "Email does not Belong to user, Please check"),
    EMAIL_TOKEN_ERROR(2004, "Email Token Error"),
    EMAIL_TOKEN_EXPIRED(2005, "Email Token Expired"),

    //通用服务错误码
    VERIFY_CODE_HAS_BEEN_SENT(3001, "Verify Code has been sent"),
    ONLY_FOR_TOKEN_CHECK(3002, "For token check use only"),;



    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
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
