package com.fantasystep.exception;

public class ValidationFailedException extends Exception {

	private static final long serialVersionUID = -6070291041269693379L;

	public ValidationFailedException() {
	}

	public ValidationFailedException(String message) {
		super(message);
	}

	public ValidationFailedException(Throwable cause) {
		super(cause);
	}

	public ValidationFailedException(String message, Throwable cause) {
		super(message, cause);
	}
}
