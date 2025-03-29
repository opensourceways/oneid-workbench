package com.oneid.application.personalapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EncryptTokenDTO {
    /**
     * 生成token
     */
    private String token;

    /**
     * 加密后的token结果
     */
    private String encryptedToken;
}
