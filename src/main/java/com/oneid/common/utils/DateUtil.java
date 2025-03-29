package com.oneid.common.utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtil {
    /**
     * 将毫秒级时间戳转换为ISO 8601格式的UTC时间字符串
     * @param timestamp 毫秒级时间戳
     * @return ISO 8601格式的UTC时间字符串，如"2025-03-28T17:25:56Z"
     */
    public static String convertTimestampToISO8601(long timestamp) {
        // 从时间戳创建Instant对象
        Instant instant = Instant.ofEpochMilli(timestamp);

        // 使用DateTimeFormatter格式化输出
        DateTimeFormatter formatter = DateTimeFormatter.ISO_INSTANT;

        // 返回格式化后的字符串
        return formatter.format(instant);
    }

    /**
     * 将ISO 8601格式时间字符串转换为 yyyy/MM/dd HH:mm 格式
     * @param iso8601Time ISO 8601格式时间字符串（如"2025-03-28T17:25:56Z"）
     * @return 格式化后的时间字符串（如"2025/03/28 17:25"）
     */
    public static String convertISO8601ToSlashFormat(String iso8601Time) {
        Instant instant = Instant.parse(iso8601Time);
        ZonedDateTime zonedDateTime = instant.atZone(ZoneId.systemDefault());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
        return formatter.format(zonedDateTime);
    }

    /**
     * 将时间戳直接转换为 yyyy/MM/dd HH:mm 格式
     * @param timestamp 毫秒级时间戳
     * @return 格式化后的时间字符串（如"2025/03/28 17:25"）
     */
    public static String convertTimestampToSlashFormat(long timestamp) {
        Instant instant = Instant.ofEpochMilli(timestamp);
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
        return formatter.format(localDateTime);
    }
}
