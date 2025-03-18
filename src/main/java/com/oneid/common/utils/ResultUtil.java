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
import com.oneid.common.constant.MessageCodeConstant;
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
     * @param msgCode 消息代码配置
     * @param msg     消息
     * @param data    数据对象
     * @return ResponseEntity 响应实体
     */
    public static ResponseEntity result(HttpStatus status, MessageCodeConstant msgCode, String msg, Object data) {
        return setResult(status, msgCode, msg, data, MessageCodeConstant.getErrorCode());
    }

    /**
     * 设置响应实体，包括HTTP状态、消息代码配置、消息内容、数据和错误码映射.
     *
     * @param status     HTTP状态
     * @param msgCode    消息代码配置
     * @param msg        消息内容
     * @param data       数据
     * @param error2code 错误码映射
     * @return ResponseEntity 响应实体
     */
    private static ResponseEntity setResult(HttpStatus status, MessageCodeConstant msgCode, String msg, Object data,
                                            Map<String, MessageCodeConstant> error2code) {
        HashMap<String, Object> res = new HashMap<>();
        res.put("code", status.value());
        res.put("data", data);
        res.put("msg", msg);

        if (status.value() == 400 && msgCode == null) {
            for (Map.Entry<String, MessageCodeConstant> entry : error2code.entrySet()) {
                if (msg.contains(entry.getKey())) {
                    msgCode = entry.getValue();
                    break;
                }
            }
        }

        if (msgCode != null) {
            HashMap<String, Object> msgMap = new HashMap<>();
            msgMap.put("code", msgCode.getCode());
            msgMap.put("message_en", msgCode.getMsgEn());
            msgMap.put("message_zh", msgCode.getMsgZh());
            res.put("msg", msgMap);
        }
        ResponseEntity<HashMap<String, Object>> responseEntity =
                new ResponseEntity<>(JSON.parseObject(HtmlUtils.htmlUnescape(JSON.toJSONString(res)),
                        HashMap.class), status);
        return responseEntity;
    }
}
