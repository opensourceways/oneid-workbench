package com.oneid.infrastructure.dapr;

import com.oneid.common.constant.DaprConstant;
import io.dapr.client.DaprClient;
import io.dapr.client.domain.State;
import io.dapr.client.domain.StateOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Component
public class DaprRedisActuator {
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
     * 通过dapr存储到对应Redis
     * @param key key值
     * @param value value值
     */
    public void saveState(String key, String value) {
        daprClient.saveState(DaprConstant.DAPR_REDIS_BINDING, key, value).block();
        LOGGER.info("Save state key:{}, value:{}", key, value);
    }

    /**
     * 通过dapr存储到对应Redis
     *
     * @param key          key值
     * @param value        value值
     * @param ttlInSeconds TTL（过期时间，单位：秒）
     */
    public void saveState(String key, String value, int ttlInSeconds) {
        // 创建元数据，设置 TTL
        Map<String, String> metadata = new HashMap<>();
        metadata.put("ttlInSeconds", String.valueOf(ttlInSeconds));

        // 创建 StateOptions 对象，传入元数据
        StateOptions options = new StateOptions(StateOptions.Consistency.STRONG, StateOptions.Concurrency.FIRST_WRITE);

        // 创建 State 对象，传入 key、value 和 StateOptions
        State<String> state = new State<>(key, value, null, metadata, options);

        // 保存状态
        daprClient.saveBulkState(DaprConstant.DAPR_REDIS_BINDING, Collections.singletonList(state)).block();
        LOGGER.info("Save state key:{}, value:{}, TTL:{} seconds", key, value, ttlInSeconds);
    }

    /**
     * 通过dapr获取Redis中存储内容
     * @param key 对应key值
     * @return
     */
    public String getState(String key) {
        State<String> value = daprClient.getState(DaprConstant.DAPR_REDIS_BINDING, key, String.class).block();
        LOGGER.info("Get state key:{}, value:{}", key, value);
        if (value != null && value.getValue() != null) {
            return value.getValue();
        } else {
            LOGGER.error("Get state error, key:{}, value:{}", key, value);
            return null;
        }
    }

    /**
     * 通过dapr删除Redis中存储内容
     * @param key
     */
    public void deleteState(String key) {
        daprClient.deleteState(DaprConstant.DAPR_REDIS_BINDING, key).block();
        LOGGER.info("Delete state key:{}", key);
    }
}
