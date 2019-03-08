package com.fantasystep.persistence.exception;

public class PermissionDeniedException extends Exception {

	private static final long serialVersionUID = -4019228704268354018L;
	
	public PermissionDeniedException() {
	}

	public PermissionDeniedException(String message) {
		super(message);
	}

	public PermissionDeniedException(Throwable cause) {
		super(cause);
	}

	public PermissionDeniedException(String message, Throwable cause) {
		super(message, cause);
	}
}
