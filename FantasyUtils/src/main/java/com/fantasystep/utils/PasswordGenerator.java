package com.fantasystep.utils;

import java.util.Random;

public class PasswordGenerator {
	private static final String alphabet = "abcdefghijklmnopqrstuvwxyz";
	private static final String passChars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ!#%&?";
	private static final int passLength = 8;
	private static final Random rn = new Random();

	public static String getRandomString(int length) {
		StringBuilder sb = new StringBuilder(length);
		for (int i = 0; i < length; i++)
			sb.append(alphabet.charAt(rn.nextInt(alphabet.length())));
		return sb.toString();
	}

	public static String getRandomPassword() {
		StringBuilder sb = new StringBuilder(passLength);
		for (int i = 0; i < 8; i++)
			sb.append(passChars.charAt(rn.nextInt(passChars.length())));
		return sb.toString();
	}
}
