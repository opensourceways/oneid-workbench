package com.oneid.common.utils;


import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class CharacterUtil {

    private static final SecureRandom random = new SecureRandom();

    /**
     * 生成16位随机UUID
     *
     * @return UUID
     */
    public static String generate32UUID() {
        byte[] bytes = new byte[16]; // 16字节 = 128位，生成32字符十六进制
        random.nextBytes(bytes);
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString().substring(0, 32);
    }

    /**
     * 将逗号分隔的字符串转换为List<String>
     *
     * @param csvStr 输入的CSV格式字符串（例如："a,b,c,d,f,g"）
     * @return 包含字符串元素的List
     * @throws IllegalArgumentException 当输入包含无效字符或格式错误时抛出
     */
    public static List<String> csvToStringList(String csvStr) {
        if (csvStr == null || csvStr.isEmpty()) {
            throw new IllegalArgumentException("输入字符串不能为空");
        }

        List<String> result = new ArrayList<>();
        String[] elements = csvStr.split(",");

        for (String element : elements) {
            // 去除首尾空白字符（根据需求可选）
            String trimmed = element.trim();
            if (trimmed.isEmpty()) {
                // 处理连续逗号或空元素的情况
                continue;
            }
            result.add(trimmed);
        }

        return result;
    }

    /**
     * 将List<String>转换回逗号分隔的字符串
     *
     * @param stringList 输入的字符串列表
     * @return CSV格式的字符串
     * @throws NullPointerException 当输入列表为null时抛出
     */
    public static String listToCsv(List<String> stringList) {
        if (stringList == null || stringList.isEmpty()) {
            return "";
        }
        return String.join(",", stringList);
    }

    /**
     * 生成6位随机数字
     * @return 6位随机数字
     */
    public static String generateRandom6DigitNumber() {
        // 生成 100000 到 999999 之间的随机数
        int randomNumber = ThreadLocalRandom.current().nextInt(100000, 1000000);
        return String.valueOf(randomNumber);
    }
}


