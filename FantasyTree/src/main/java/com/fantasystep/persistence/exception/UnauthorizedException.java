package com.fantasystep.persistence.exception;

public class UnauthorizedException extends Exception {

	private static final long serialVersionUID = 2371172361892784971L;

	public UnauthorizedException() {
	}

	public UnauthorizedException(String message) {
		super(message);
	}

	public UnauthorizedException(Throwable cause) {
		super(cause);
	}

	public UnauthorizedException(String message, Throwable cause) {
		super(message, cause);
	}
}
