package com.oneid.common.utils;

public class RedisUtil {
    /**
     * redis中记录该账号发送的code,有效期1分钟
     * @param account 账号
     * @return RedisKey
     */
    public static String getSendEmailCodeRedisKey(String account) {
        return account + "_sendcodeV3";
    }

    /**
     * 记录该账号
     * @param account
     * @return
     */
    public static String getEmailTokenRedisKey(String account) {
        return account + "_emailToken";
    }
}
