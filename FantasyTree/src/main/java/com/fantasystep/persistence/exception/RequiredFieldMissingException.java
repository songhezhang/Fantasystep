package com.fantasystep.persistence.exception;

public class RequiredFieldMissingException extends Exception {

	private static final long serialVersionUID = -1610028485486489752L;

	public RequiredFieldMissingException() {
	}

	public RequiredFieldMissingException(String message) {
		super(message);
	}

	public RequiredFieldMissingException(Throwable cause) {
		super(cause);
	}

	public RequiredFieldMissingException(String message, Throwable cause) {
		super(message, cause);
	}
}
