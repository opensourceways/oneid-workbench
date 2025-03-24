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

    //获取用户账号信息错误
    GET_ACCOUNT_INFO_ERROR(1002, "Get Account Info Error"),;


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
