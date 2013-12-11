package com.secure.nfcsecure;

import java.io.ByteArrayOutputStream;

import java.io.UnsupportedEncodingException;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class MyCryptography {
	
	Cipher cipher;
	SecretKeySpec secretKey;
	private static int SALT_ZISE = 16;
	private static int IV_SIZE = 16;
	byte[] iv;
	
	public MyCryptography(char[] password) throws NoSuchAlgorithmException,InvalidKeySpecException, NoSuchPaddingException {
	
	//salt is a fixed byte[] of 16 bytes!!! It is bad and we should say salt to other side with an out of band method
	byte[] salt = new byte[SALT_ZISE];
	Arrays.fill(salt, (byte) 0);
	SecretKeyFactory factory = SecretKeyFactory.getInstance("PBEWITHSHA256AND256BITAES-CBC-BC");
	KeySpec spec = new PBEKeySpec(password, salt, 1024, 256);
	secretKey = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
	cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
	}
	
	/////////for test
	public static SecretKey generateKey(byte[] salt, char[] password) throws NoSuchAlgorithmException, InvalidKeySpecException {
		SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
		KeySpec spec = new PBEKeySpec(password, salt, 65536, 256);
		SecretKey tmp = factory.generateSecret(spec);
		SecretKey secret = new SecretKeySpec(tmp.getEncoded(), "AES");
		return secret;
	}

	///////for test
	public byte[] encryptPBKDF(String cleartext, SecretKey secret) throws InvalidKeyException,
    IllegalBlockSizeException, BadPaddingException,
    UnsupportedEncodingException, InvalidParameterSpecException, NoSuchAlgorithmException, NoSuchPaddingException {
		/* Encrypt the message. */
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		cipher.init(Cipher.ENCRYPT_MODE, secret);
		AlgorithmParameters params = cipher.getParameters();
		iv = params.getParameterSpec(IvParameterSpec.class).getIV();
		byte[] ciphertext = cipher.doFinal("Hello, World!".getBytes("UTF-8"));
		return ciphertext;
	}
	
	
	//encrypts the input and add the IV and its length to the beginning of it to create a message to be sent to other side.
	public byte[] encrypt(byte[] byteClearText) throws InvalidKeyException,
    IllegalBlockSizeException, BadPaddingException,
    UnsupportedEncodingException, InvalidParameterSpecException {


	    cipher.init(Cipher.ENCRYPT_MODE, secretKey);

//	    byte[] encrptedText = cipher.doFinal(cleartext.getBytes("UTF-8"));
	    byte[] encrptedText = cipher.doFinal(byteClearText);
	    byte[] iv = cipher.getParameters().getParameterSpec(IvParameterSpec.class).getIV();

	    byte[] encryptedMessage = new byte[IV_SIZE + encrptedText.length];

	    for (int i = 0; i < encryptedMessage.length; i++) {
	        if (i < IV_SIZE)
	            encryptedMessage[i] = iv[i];
	        else if (i < encryptedMessage.length)
	            encryptedMessage[i] = encrptedText[i - IV_SIZE];
	    }

	    return encryptedMessage;
	}
	
	
	//receives the encrypted message, extracts IV, and decrypts it with IV to create the clear text 
	public byte[] decrypt(byte[] encryptedText) throws InvalidKeyException,
	    InvalidAlgorithmParameterException, UnsupportedEncodingException,
	    IllegalBlockSizeException, BadPaddingException {

	    byte[] iv = new byte[IV_SIZE];
	    byte[] dec = new byte[encryptedText.length - IV_SIZE];

	    for (int i = 0; i < encryptedText.length; i++) {
	        if (i < IV_SIZE)
	            iv[i] = encryptedText[i];
	        else if (i < encryptedText.length)
	            dec[i - IV_SIZE] = encryptedText[i];
	    }

	    cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(iv));

	    return cipher.doFinal(dec);
	    
//	    return new String(cipher.doFinal(dec), "UTF-8");
	}
	
	
	public static int byteArrayToInt(byte[] b) 
	{
	    int value = 0;
	    for (int i = 0; i < 4; i++) {
	        int shift = (4 - 1 - i) * 8;
	        value += (b[i] & 0x000000FF) << shift;
	    }
	    return value;
	}
	
	public static byte[] intToByteArray(int a)
	{
	    byte[] ret = new byte[4];
	    ret[3] = (byte) (a & 0xFF);   
	    ret[2] = (byte) ((a >> 8) & 0xFF);   
	    ret[1] = (byte) ((a >> 16) & 0xFF);   
	    ret[0] = (byte) ((a >> 24) & 0xFF);
	    return ret;
	}
}

