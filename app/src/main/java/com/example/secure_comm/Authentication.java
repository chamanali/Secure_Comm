package com.example.secure_comm;

import android.util.Base64;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by hp on 12/30/2016.
 */

public class Authentication
{
    String encryptedpass;
    private  static byte[] SecretKey = {0x0f, 0x0f, 0x0f, 0x0f, 0x0f, 0x0f, 0x0f, 0x0f,
            0x0f, 0x0f, 0x0f, 0x0f, 0x0f, 0x0f, 0x0f, 0x0f};
    private static  byte[] keybytes;

    public Authentication()
    {

    }

    public byte[] encrypt(String password,String key)
            throws java.io.UnsupportedEncodingException,
            NoSuchAlgorithmException,
            NoSuchPaddingException,
            InvalidKeyException,
            InvalidAlgorithmParameterException,
            IllegalBlockSizeException,
            BadPaddingException {

        byte[] textBytes = password.getBytes("UTF-8");
        byte[] keybytes=key.getBytes("UTF-8");
        AlgorithmParameterSpec ivSpec = new IvParameterSpec(SecretKey);
        SecretKeySpec newKey = new SecretKeySpec(keybytes, "AES");
        Cipher cipher = null;
        cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, newKey, ivSpec);
        return cipher.doFinal(textBytes);
    }


    public byte[] decrypt(String password,String key)
            throws java.io.UnsupportedEncodingException,
            NoSuchAlgorithmException,
            NoSuchPaddingException,
            InvalidKeyException,
            InvalidAlgorithmParameterException,
            IllegalBlockSizeException,
            BadPaddingException {


        byte[] textBytes = Base64.decode(password,Base64.DEFAULT);
        byte[] keybytes=key.getBytes("UTF-8");
        AlgorithmParameterSpec ivSpec = new IvParameterSpec(SecretKey);
        SecretKeySpec newKey = new SecretKeySpec(keybytes, "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, newKey, ivSpec);
        return cipher.doFinal(textBytes);
    }
}
