package com.fantasystep.persistence.exception;

public class InvalidCredentialsException extends Exception {
	
	private static final long serialVersionUID = -4857359962909608727L;
	
	public InvalidCredentialsException() {
	}

	public InvalidCredentialsException(String message) {
		super(message);
	}

	public InvalidCredentialsException(Throwable cause) {
		super(cause);
	}

	public InvalidCredentialsException(String message, Throwable cause) {
		super(message, cause);
	}
}
