package com.fantasystep.persistence.exception;

public class InvalidSessionException extends Exception {

	private static final long serialVersionUID = 690050199203392610L;

	public InvalidSessionException() {
	}

	public InvalidSessionException(String message) {
		super(message);
	}

	public InvalidSessionException(Throwable cause) {
		super(cause);
	}

	public InvalidSessionException(String message, Throwable cause) {
		super(message, cause);
	}
}
