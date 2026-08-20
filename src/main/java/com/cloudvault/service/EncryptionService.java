package com.cloudvault.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

@Service
public class EncryptionService {

    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";
    private static final int KEY_SIZE = 256;
    private static final int IV_SIZE = 16; // 128 bits for CBC mode

    @Value("${encryption.algorithm:AES}")
    private String encryptionAlgorithm;

    /**
     * Encrypts bytes using AES-256-CBC with a master key
     */
    public String encryptBytes(byte[] data, String masterKey) throws Exception {
        SecretKey key = deriveKeyFromMasterKey(masterKey);
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        
        SecureRandom random = new SecureRandom();
        byte[] iv = new byte[IV_SIZE];
        random.nextBytes(iv);
        
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);
        
        byte[] encryptedData = cipher.doFinal(data);
        
        // Prepend IV to encrypted data
        ByteBuffer buffer = ByteBuffer.allocate(iv.length + encryptedData.length);
        buffer.put(iv);
        buffer.put(encryptedData);
        
        return Base64.getEncoder().encodeToString(buffer.array());
    }

    /**
     * Decrypts bytes using AES-256-CBC with a master key
     */
    public byte[] decryptBytes(String encryptedData, String masterKey) throws Exception {
        SecretKey key = deriveKeyFromMasterKey(masterKey);
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        
        byte[] decodedData = Base64.getDecoder().decode(encryptedData);
        ByteBuffer buffer = ByteBuffer.wrap(decodedData);
        
        byte[] iv = new byte[IV_SIZE];
        buffer.get(iv);
        byte[] encrypted = new byte[buffer.remaining()];
        buffer.get(encrypted);
        
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);
        
        return cipher.doFinal(encrypted);
    }

    /**
     * Derives a key from the master key using SHA-256
     */
    private SecretKey deriveKeyFromMasterKey(String masterKey) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(masterKey.getBytes(StandardCharsets.UTF_8));
        return new SecretKeySpec(hash, 0, hash.length, ALGORITHM);
    }

    /**
     * Generates a random encryption key (for future use)
     */
    public String generateRandomKey() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance(ALGORITHM);
        keyGen.init(KEY_SIZE);
        SecretKey secretKey = keyGen.generateKey();
        return Base64.getEncoder().encodeToString(secretKey.getEncoded());
    }

    /**
     * Encrypts a string message
     */
    public String encryptString(String message, String masterKey) throws Exception {
        return encryptBytes(message.getBytes(StandardCharsets.UTF_8), masterKey);
    }

    /**
     * Decrypts a string message
     */
    public String decryptString(String encryptedMessage, String masterKey) throws Exception {
        byte[] decryptedBytes = decryptBytes(encryptedMessage, masterKey);
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }

}
