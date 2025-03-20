package com.oneid.common.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.InvalidKeySpecException;

import static com.google.common.net.MediaType.JWT;

@Component
public class AuthUtil {

    @Value("${rsa.authing.privateKey}")
    public String rsaAuthingPrivateKey;

    /**
     * 获取自定义token中的user id.
     *
     * @param token
     * @return String
     */
    public String getUserIdFromToken(String token) throws InvalidKeySpecException, NoSuchAlgorithmException,
            InvalidKeyException, NoSuchPaddingException {
        DecodedJWT decode = com.auth0.jwt.JWT.decode(rsaDecryptToken(token));
        return decode.getAudience().get(0);
    }

    /**
     * 解密RSA加密过的token.
     *
     * @param token
     * @return String
     */
    public String rsaDecryptToken(String token) throws InvalidKeySpecException, NoSuchAlgorithmException,
            InvalidKeyException, NoSuchPaddingException {
        RSAPrivateKey privateKey = RSAUtil.getPrivateKey(rsaAuthingPrivateKey);
        return RSAUtil.privateDecrypt(token, privateKey);
    }
}
