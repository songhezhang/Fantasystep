package com.fantasystep.persistence.exception;

public class IdNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1548644364333642630L;

	public IdNotFoundException() {
	}

	public IdNotFoundException(String message) {
		super(message);
	}

	public IdNotFoundException(Throwable cause) {
		super(cause);
	}

	public IdNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}
}
