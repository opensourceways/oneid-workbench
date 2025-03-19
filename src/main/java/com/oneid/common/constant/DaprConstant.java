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

package com.oneid.common.constant;

public class DaprConstant {
    /**
     * 数据库绑定.
     */
    public static final String DAPR_SQL_BINDING = "oneid-workbench-sql";

    /**
     * 数据库查询.
     */
    public static final String DAPR_SQL_QUERY = "query";

    /**
     * 数据库执行.
     */
    public static final String DAPR_SQL_EXE = "exec";

    /**
     * 邮件服务绑定.
     */
    public static final String DAPR_EMAIL_BINDING = "oneid-workbench-email";

    /**
     * 邮件发送.
     */
    public static final String DAPR_EMAIL_SEND = "create";

    /**
     * 邮件发送人.
     */
    public static final String DAPR_EMAIL_SENDER = "emailTo";

    /**
     * 邮件主题.
     */
    public static final String DAPR_EMAIL_SUBJECT = "subject";

    /**
     * 邮件优先级.
     */
    public static final String DAPR_EMAIL_PRIORITY = "priority";
}
