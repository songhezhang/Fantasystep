package com.fantasystep.persistence.exception;

public class UniqueViolateException extends Exception {

	private static final long serialVersionUID = -3787422060382647320L;
	
	public UniqueViolateException() {
	}

	public UniqueViolateException(String message) {
		super(message);
	}

	public UniqueViolateException(Throwable cause) {
		super(cause);
	}

	public UniqueViolateException(String message, Throwable cause) {
		super(message, cause);
	}
}
