package com.minidouban.cachemgr.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Service
public class SafetyUtils {
    private static final PasswordEncoder passwordEncoder;

    private static final byte[] tokenKey;

    private static final String AES_ALGORITHM;

    static {
        passwordEncoder = new BCryptPasswordEncoder();
        AES_ALGORITHM = "AES/ECB/PKCS5Padding";
        tokenKey = "token_key123".getBytes();
    }

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

    public String encrypt(byte[] data) throws Exception {
        Cipher cipher = getCipher(Cipher.ENCRYPT_MODE);
        return Base64.getEncoder().encodeToString(cipher.doFinal(data));
    }

    public byte[] decrypt(byte[] data) throws Exception {
        Cipher cipher = getCipher(Cipher.DECRYPT_MODE);
        return cipher.doFinal(Base64.getDecoder().decode(data));
    }
}
