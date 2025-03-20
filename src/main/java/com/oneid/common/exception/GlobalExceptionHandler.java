package com.oneid.common.exception;

import com.oneid.common.exception.CustomException;
import com.oneid.common.utils.ResultUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    // 处理自定义异常
    @ExceptionHandler(CustomException.class)
    public ResponseEntity handleCustomException(CustomException e) {

        return ResultUtil.result(resolveHttpStatus(e.getErrorCode().getCode()), e.getMessage(), e);
    }

    // 处理其他运行时异常
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity handleRuntimeException(RuntimeException e) {

        return ResultUtil.result(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), null);
    }

    // 处理所有其他异常
    @ExceptionHandler(Exception.class)
    public ResponseEntity handleException(Exception e) {
        return ResultUtil.result(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), null);
    }

    /**
     * 根据错误码解析 HTTP 状态码
     *
     * @param errorCode 错误码
     * @return 对应的 HTTP 状态码
     */
    private HttpStatus resolveHttpStatus(int errorCode) {
        switch (errorCode) {
            case 401:
                return HttpStatus.UNAUTHORIZED;
            case 403:
                return HttpStatus.FORBIDDEN;
            case 404:
                return HttpStatus.NOT_FOUND;
            case 400:
                return HttpStatus.BAD_REQUEST;
            default:
                return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }
}