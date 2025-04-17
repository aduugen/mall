package com.macro.mall.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * 数据加密工具类，用于敏感数据的加密和解密
 */
@Component
public class EncryptionUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(EncryptionUtil.class);

    private static final String AES_ALGORITHM = "AES";
    private static final String SECURE_RANDOM_ALGORITHM = "SHA1PRNG";

    @Value("${data.encryption.secret:macromall}")
    private String encryptionSecret;

    /**
     * 加密数据
     * 
     * @param data 需要加密的数据
     * @return 加密后的数据
     */
    public String encrypt(String data) {
        if (data == null || data.isEmpty()) {
            return data;
        }

        try {
            Key key = generateKey(encryptionSecret);
            Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] encryptedBytes = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            LOGGER.error("数据加密失败", e);
            return data; // 加密失败返回原数据
        }
    }

    /**
     * 解密数据
     * 
     * @param encryptedData 已加密的数据
     * @return 解密后的数据
     */
    public String decrypt(String encryptedData) {
        if (encryptedData == null || encryptedData.isEmpty()) {
            return encryptedData;
        }

        try {
            Key key = generateKey(encryptionSecret);
            Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] decodedBytes = Base64.getDecoder().decode(encryptedData);
            byte[] decryptedBytes = cipher.doFinal(decodedBytes);
            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            LOGGER.error("数据解密失败", e);
            return encryptedData; // 解密失败返回加密数据
        }
    }

    /**
     * 生成密钥
     * 
     * @param seed 密钥种子
     * @return 生成的密钥
     * @throws NoSuchAlgorithmException 如果没有这个算法
     */
    private Key generateKey(String seed) throws NoSuchAlgorithmException {
        SecureRandom secureRandom = SecureRandom.getInstance(SECURE_RANDOM_ALGORITHM);
        secureRandom.setSeed(seed.getBytes(StandardCharsets.UTF_8));

        KeyGenerator keyGenerator = KeyGenerator.getInstance(AES_ALGORITHM);
        keyGenerator.init(128, secureRandom);
        SecretKey secretKey = keyGenerator.generateKey();
        return new SecretKeySpec(secretKey.getEncoded(), AES_ALGORITHM);
    }

    /**
     * 加密手机号
     * 保留前三位和后四位，中间用*代替
     * 
     * @param phoneNumber 手机号
     * @return 加密后的手机号
     */
    public String maskPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.length() != 11) {
            return phoneNumber;
        }

        return phoneNumber.substring(0, 3) + "****" + phoneNumber.substring(7);
    }

    /**
     * 加密邮箱
     * 邮箱前缀仅显示第一个字符和最后一个字符，中间用*代替
     * 
     * @param email 邮箱
     * @return 加密后的邮箱
     */
    public String maskEmail(String email) {
        if (email == null || email.indexOf('@') <= 1) {
            return email;
        }

        int atIndex = email.indexOf('@');
        String prefix = email.substring(0, atIndex);
        String domain = email.substring(atIndex);

        if (prefix.length() <= 2) {
            return prefix + domain;
        }

        return prefix.charAt(0) +
                String.join("", java.util.Collections.nCopies(prefix.length() - 2, "*")) +
                prefix.charAt(prefix.length() - 1) +
                domain;
    }

    /**
     * 加密身份证号
     * 仅显示前三位和后四位，中间用*代替
     * 
     * @param idCard 身份证号
     * @return 加密后的身份证号
     */
    public String maskIdCard(String idCard) {
        if (idCard == null || idCard.length() < 8) {
            return idCard;
        }

        return idCard.substring(0, 3) +
                String.join("", java.util.Collections.nCopies(idCard.length() - 7, "*")) +
                idCard.substring(idCard.length() - 4);
    }
}