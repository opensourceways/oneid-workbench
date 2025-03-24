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

import com.alibaba.fastjson2.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oneid.common.constant.DaprConstant;
import io.dapr.client.DaprClient;
import io.dapr.utils.TypeRef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class DaprSqlActuator {
    /**
     * 日志.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(DaprSqlActuator.class);

    /**
     * dapr客户端.
     */
    @Autowired
    private DaprClient daprClient;

    private final ObjectMapper objectMapper = new ObjectMapper();
    /**
     * 查询数据.
     *
     * @param sql sql
     * @param params 参数
     * @return
     * @param <T> 自定义实体类
     */
    public <T> List<T> query(String sql, List<String> params, Class<T> clazz) {
        try {
            Map<String, String> metadata = buildSqlMetadata(sql, params);
            TypeRef<List<LinkedHashMap>> responseType = new TypeRef<>() {};
            List<LinkedHashMap> result = daprClient.invokeBinding(DaprConstant.DAPR_SQL_BINDING,
                    DaprConstant.DAPR_SQL_QUERY, null, metadata, responseType).block();

            // 将 LinkedHashMap 转换为目标类型
            return result.stream()
                    .map(map -> objectMapper.convertValue(map, clazz))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            LOGGER.error("query failed {}", e.getMessage());
            return null;
        }
    }



    /**
     * 执行创建/更新/删除.
     *
     * @param sql sql
     * @param params 参数
     * @return 返回值
     */
    public boolean exeSql(String sql, List<String> params) {
        try {
            Map<String, String> request = buildSqlMetadata(sql, params);
            daprClient.invokeBinding(DaprConstant.DAPR_SQL_BINDING,
                    DaprConstant.DAPR_SQL_EXE, null, request, String.class).block();
            return true;
        } catch (Exception e) {
            LOGGER.error("exe sql failed {}", e.getMessage());
            return false;
        }
    }

    /**
     * 构建sql元数据.
     *
     * @param sql sql
     * @param params param
     * @return 返回值
     */
    private Map<String, String> buildSqlMetadata(String sql, List<String> params) {
        Map<String, String> request = new HashMap<>();
        request.put("sql", sql);
        if (params.size() != 0) {
            request.put("params", JSONObject.toJSONString(params));
        }
        return request;
    }
}
