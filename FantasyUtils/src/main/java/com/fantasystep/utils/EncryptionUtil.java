package com.fantasystep.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import sun.misc.BASE64Encoder;

@SuppressWarnings("restriction")
public class EncryptionUtil {

	public final static String SHA1 = "SHA1";
	public final static String MD5 = "MD5";

	public static Object aesEncrypt(String password) {
		return digest("MD5", password);
	}

	public static String digest(String algorithm, String password) {
		if(password.startsWith(String.format("{%s}", MD5)) || password.startsWith(String.format("{%s}", SHA1)))
			return password;
		String r = null;
		byte[] b = null;
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance(algorithm);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return password;
		}
		BASE64Encoder encoder;

		md.update(password.getBytes());
		b = md.digest();

		encoder = new BASE64Encoder();

		r = encoder.encode(b);

		return String.format("{%s}%s", algorithm, r);
	}

	public static boolean testPass(String password, String encrypted) {
		if (password == null)
			return false;
		if (encrypted.toString().startsWith(String.format("{%s}", MD5)))
			return digest(MD5, password).equals(encrypted);
		else if (encrypted.toString().startsWith(String.format("{%s}", SHA1)))
			return digest(SHA1, password).equals(encrypted);
		else
			return password.equals(encrypted);
	}
}
