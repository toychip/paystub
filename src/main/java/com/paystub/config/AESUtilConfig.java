package com.paystub.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
@Slf4j
public class AESUtilConfig {
    @Value("${aes.secretKey}")
    private String secretKey;

    // 주어진 문자열을 암호화하는 메서드
    public String encrypt(String strToEncrypt) {

        try {
            // AES/ECB/PKCS5Padding 방식으로 암호화 인스턴스를 생성
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            // 주어진 비밀 키를 바탕으로 SecretKeySpec 객체를 생성
            SecretKeySpec secretKey = new SecretKeySpec(this.secretKey.getBytes(), "AES");
            // 암호화 모드로 초기화
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            // 문자열을 암호화하여 바이트 배열로 변환
            byte[] encrypted = cipher.doFinal(strToEncrypt.getBytes(StandardCharsets.UTF_8));
            // URL 안전 Base64 인코딩을 사용하여 암호화된 바이트 배열을 문자열로 변환
            return Base64.getUrlEncoder().withoutPadding().encodeToString(encrypted);
        } catch (Exception e) {
            log.error("Error while encrypting: {}", e.toString());
        }
        return null;
    }

    // 암호화된 문자열을 복호화하는 메서드
    public String decrypt(String strToDecrypt) {
        try {
            // AES/ECB/PKCS5Padding 방식으로 암호화 인스턴스를 생성
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
            // 주어진 비밀 키를 바탕으로 SecretKeySpec 객체를 생성
            SecretKeySpec secretKey = new SecretKeySpec(this.secretKey.getBytes(), "AES");
            // 복호화 모드로 초기화
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            // 암호화된 문자열을 복호화하여 바이트 배열로 변환하고, 이를 다시 문자열로 변환
            return new String(cipher.doFinal(Base64.getUrlDecoder().decode(strToDecrypt)));
        } catch (Exception e) {
            log.error("Error while decrypting: {}", e.toString());
        }
        return null;
    }
}