package com.minidouban.component;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Component
public class SafetyUtils {
    private static final byte[] tokenKey;
    private static final String AES_ALGORITHM;
    private static final Charset charset;

    static {
        AES_ALGORITHM = "AES/ECB/PKCS5Padding";
        charset = StandardCharsets.UTF_8;
        tokenKey = "token_key123".getBytes(charset);
    }

    @Resource
    private PasswordEncoder passwordEncoder;

    public String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    public boolean matches(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    public Cipher getCipher(int mode) {
        SecretKeySpec secretKeySpec = new SecretKeySpec(tokenKey, "AES");
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance(AES_ALGORITHM);
            cipher.init(mode, secretKeySpec);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e) {
            e.printStackTrace();
        }
        return cipher;
    }

    public String encrypt(String data) {
        Cipher cipher = getCipher(Cipher.ENCRYPT_MODE);
        try {
            return Base64.getEncoder().encodeToString(cipher.doFinal(data.getBytes(charset)));
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        }
    }

    public String decrypt(String data) {
        Cipher cipher = getCipher(Cipher.DECRYPT_MODE);
        try {
            return new String(cipher.doFinal(Base64.getDecoder().decode(data.getBytes(charset))), charset);
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        }
    }
}
