package com.acleda.company.student.utils;

import java.security.SecureRandom;

public class RandomNumberPasswordGenerator {

	private static final String NUMBERS = "0123456789";
	public static final int PASSWORD_LENGTH = 6;
	private static SecureRandom random = new SecureRandom();

	public static void main(String[] args) {
		String password = generateRandomPassword();
		System.out.println("Random Password: " + password);
	}

	public synchronized static String generateRandomPassword(int length) {

		StringBuilder password = new StringBuilder(length);

		for (int i = 0; i < length; i++) {
			int index = random.nextInt(NUMBERS.length());
			password.append(NUMBERS.charAt(index));
		}
		return password.toString();
	}

	public synchronized static String generateRandomPassword() {
		return generateRandomPassword(PASSWORD_LENGTH);
	}

}
