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

package com.oneid.common.utils;

import com.alibaba.fastjson2.JSON;
import com.oneid.common.exception.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.HtmlUtils;

import java.util.HashMap;
import java.util.Map;

public final class ResultUtil {
    private ResultUtil() {
        throw new AssertionError("ResultUtil class cannot be instantiated.");
    }

    /**
     * 构建响应实体方法.
     *
     * @param status  HTTP状态
     * @param msg     消息
     * @param data    数据对象
     * @return ResponseEntity 响应实体
     */
    public static ResponseEntity result(HttpStatus status, String msg, Object data) {
        HashMap<String, Object> res = new HashMap<>();
        res.put("code", status.value());
        res.put("data", data);
        res.put("msg", msg);
        ResponseEntity<HashMap<String, Object>> responseEntity =
                new ResponseEntity<>(JSON.parseObject(
                        HtmlUtils.htmlUnescape(JSON.toJSONString(res)), HashMap.class), status);
        return responseEntity;
    }

    /**
     * 构建响应实体方法.
     *
     * @param status  HTTP状态
     * @param errorCode 消息代码配置
     * @param msg     消息
     * @param data    数据对象
     * @return ResponseEntity 响应实体
     */
    public static ResponseEntity result(HttpStatus status, ErrorCode errorCode, String msg, Object data) {
        HashMap<String, Object> res = new HashMap<>();
        res.put("code", status.value());
        res.put("data", data);
        res.put("msg", msg);
        if (errorCode != null) {
            HashMap<String, Object> msgMap = new HashMap<>();
            msgMap.put("code", errorCode.getCode());
            msgMap.put("message", errorCode.getMessage());
            res.put("msg", msgMap);
        }
        ResponseEntity<HashMap<String, Object>> responseEntity =
                new ResponseEntity<>(JSON.parseObject(HtmlUtils.htmlUnescape(JSON.toJSONString(res)),
                        HashMap.class), status);
        return responseEntity;
    }
}
