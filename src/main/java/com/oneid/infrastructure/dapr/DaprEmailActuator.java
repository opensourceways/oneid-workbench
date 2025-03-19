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

package com.oneid.infrastructure.dapr;

import com.oneid.common.constant.DaprConstant;
import com.oneid.common.constant.PakageConstant;
import io.dapr.client.DaprClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.util.HashMap;
import java.util.Map;

@Component
public class DaprEmailActuator {
    /**
     * 日志.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(DaprEmailActuator.class);

    /**
     * dapr客户端.
     */
    @Autowired
    private DaprClient daprClient;

    /**
     * 发送邮件验证码.
     *
     * @param account 邮件接受者
     * @param code 验证码
     * @param expire 有效期，分钟
     * @return 发送结果
     */
    public boolean sendMessageCode(String account, String code, int expire) {
        try {
            // 邮件元数据
            Map<String, String> metadata = new HashMap<>();
            // 收件人
            metadata.put(DaprConstant.DAPR_EMAIL_SENDER, account);
            // 邮件主题
            metadata.put(DaprConstant.DAPR_EMAIL_SUBJECT, PakageConstant.EMAIL_TEMPLATE_SUBJECT);
            // 优先级（可选）
            metadata.put(DaprConstant.DAPR_EMAIL_PRIORITY, PakageConstant.EMAIL_TEMPLATE_PRIORITY);

            // 构造模板引擎
            ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
            resolver.setPrefix(PakageConstant.EMAIL_TEMPLATE_PREFIX);
            resolver.setSuffix(PakageConstant.EMAIL_TEMPLATE_SUFFIX);
            TemplateEngine templateEngine = new TemplateEngine();
            templateEngine.setTemplateResolver(resolver);

            // 注入变量值
            Context context = new Context();
            context.setVariable(PakageConstant.EMAIL_TEMPLATE_ACCOUNT, account);
            context.setVariable(PakageConstant.EMAIL_TEMPLATE_CODE, code);
            context.setVariable(PakageConstant.EMAIL_TEMPLATE_EXPIRE, String.valueOf(expire));

            String emailContent = templateEngine.process(PakageConstant.EMAIL_TEMPLATE_FILE, context);
            daprClient.invokeBinding(DaprConstant.DAPR_EMAIL_BINDING, DaprConstant.DAPR_EMAIL_SEND,
                    emailContent.getBytes(), metadata).block();
            return true;
        } catch (Exception e) {
            LOGGER.error("send email failed {}", e.getMessage());
            return false;
        }
    }
}
