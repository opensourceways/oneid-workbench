/* This project is licensed under the Mulan PSL v2.
 You can use this software according to the terms and conditions of the Mulan PSL v2.
 You may obtain a copy of Mulan PSL v2 at:
     http://license.coscl.org.cn/MulanPSL2
 THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR
 PURPOSE.
 See the Mulan PSL v2 for more details.
 Create: 2024
*/

package com.oneid.common.utils;

import com.alibaba.fastjson2.JSON;
import com.oneid.common.constant.CommonConstant;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public final class LogUtil {

    private LogUtil() {
        throw new AssertionError("Utility class. Not intended for instantiation.");
    }

    /**
     * 日志记录器，用于记录 LogUtil 类的日志信息.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(LogUtil.class);

    /**
     * 不记录日志的 URL 白名单列表.
     */
    public static final List<String> URL_NO_LOG_WHITE_LIST = Collections.unmodifiableList(new ArrayList<>() {
        {
            add("/oneid-workbench/checkOmService");
        }
    });


    /**
     * format logging parameter.
     *
     * @param input The input pramater
     * @return formatedOutput The safe output logging parmeter
     */
    public static String formatCodeString(String input) {
        if (input == null) {
            return input;
        }
        String formatedOutput = input.replace("\r", "\\r").replace("\n", "\\n").replace("\u0008", "\\u0008")
                .replace("\u000B", "\\u000B")
                .replace("\u000C", "\\u000C")
                .replace("\u007F", "\\u007F")
                .replace("\u0009", "\\u0009");
        return formatedOutput;
    }

    /**
     * 组装记录日志.
     *
     * @param userId 用户id
     * @param type 操作类型
     * @param module 模块名
     * @param detail 操作资源详情
     * @param ip 操作者ip
     * @param result 操作结果
     */
    public static void createLogs(String userId, String type, String module, String detail, String ip, String result) {
        StringBuilder account = new StringBuilder();
        if (StringUtils.isNotBlank(userId)) {
            if (userId.matches((CommonConstant.PHONEREGEX))) {
                account.append("****").append(userId.substring(userId.length() - 4));
            } else if (userId.matches(CommonConstant.EMAILREGEX)) {
                int atIndex = userId.indexOf('@');
                if (atIndex > 1) {
                    account.append(userId.charAt(0)).append("****").append(userId.charAt(atIndex - 1))
                            .append(userId.substring(atIndex));
                } else {
                    account.append(userId);
                }
            } else {
                account.append(userId);
            }
        }
        LOGGER.info(String.format("(Client ip:%s, User:%s, Module:%s, Type:%s) Detail:%s.--->Result:%s.",
                ip, account.toString(), module, type, detail, result));
    }
}
