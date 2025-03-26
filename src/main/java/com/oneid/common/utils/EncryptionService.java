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

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.DrbgParameters;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * 类描述：加密服务类.
 */
@Component
public class EncryptionService {
    /**
     * GCM模式下的IV长度.
     */
    private static final int GCM_IV_LENGTH = 12;

    /**
     * GCM模式下的TAG长度.
     */
    private static final int GCM_TAG_LENGTH = 16;

    /**
     * 密钥生成器.
     */
    private SecretKeySpec secretKeySpec;

    /**
     * 安全随机数.
     */
    private SecureRandom secureRandom;

    /**
     * 静态日志记录器，用于记录 EncryptionService 类的日志信息.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(EncryptionService.class);

    /**
     * token的盐值.
     */
    @Value("${token.sha256.salt: }")
    private String tokenSalt;


    /**
     * sha256加密.
     *
     * @param data 数据
     * @return 加密后数据
     * @throws NoSuchAlgorithmException 异常
     */
    public String encrypt(String data) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            LOGGER.error("encryptSha256 failed {}", e.getMessage());
            return null;
        }
        // 将盐值和数据拼接后进行哈希计算
        String combinedData = data + tokenSalt;
        byte[] hashBytes = md.digest(combinedData.getBytes(StandardCharsets.UTF_8));
        StringBuilder hexString = new StringBuilder();
        for (byte b : hashBytes) {
            String hex = String.format("%02X", b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
