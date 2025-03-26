package com.oneid.common.exception;

import com.oneid.common.utils.ResultUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // 处理自定义异常
    @ExceptionHandler(CustomException.class)
    public ResponseEntity handleCustomException(CustomException e) {

        return ResultUtil.result(resolveHttpStatus(e.getErrorCode().getCode()), e.getErrorCode(), null, null);
    }

    // 处理其他运行时异常
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity handleRuntimeException(RuntimeException e) {
        log.error("运行时出现异常， httpStatus：500, messages:{}", e.getMessage(), e);
        return ResultUtil.result(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.INTERNAL_SERVER_ERROR.getMessageEn(), null);
    }

    // 处理所有其他异常
    @ExceptionHandler(Exception.class)
    public ResponseEntity handleException(Exception e) {
        log.error("运行时出现异常， httpStatus：500, messages:{}", e.getMessage(), e);
        return ResultUtil.result(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.INTERNAL_SERVER_ERROR.getMessageEn(), null);
    }

    /**
     * 根据错误码解析 HTTP 状态码
     *
     * @param errorCode 错误码
     * @return 对应的 HTTP 状态码
     */
    private HttpStatus resolveHttpStatus(int errorCode) {
        switch (errorCode) {
            case 401, 2002:
                return HttpStatus.UNAUTHORIZED;
            case 403, 1004:
                return HttpStatus.FORBIDDEN;
            case 404:
                return HttpStatus.NOT_FOUND;
            case 400, 1003:
                return HttpStatus.BAD_REQUEST;
            case 2004:
                return HttpStatus.OK;
            default:
                return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }
}