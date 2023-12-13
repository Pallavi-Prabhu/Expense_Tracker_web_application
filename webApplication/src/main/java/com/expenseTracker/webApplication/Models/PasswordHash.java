package com.expenseTracker.webApplication.Models;

import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Component
public class PasswordHash {

    public String passwordHashing(String password) {
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");

        byte[] hashBytes = digest.digest(password.getBytes(StandardCharsets.UTF_8));

        StringBuilder hexString = new StringBuilder();
        for (byte hashByte : hashBytes) {
            String hex = Integer.toHexString(0xff & hashByte);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }

        //System.out.println("SHA-256 Hash: " + hexString.toString());
        String hashPass = hexString.toString();
        return hashPass;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error during password hashing: " + e.getMessage(), e);
        }
    }
}
