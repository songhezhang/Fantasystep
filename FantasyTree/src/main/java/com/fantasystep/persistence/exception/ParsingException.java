package com.fantasystep.persistence.exception;

public class ParsingException extends Exception {
	
	private static final long serialVersionUID = 309455647484930887L;

	public ParsingException() {
	}

	public ParsingException(String message) {
		super(message);
	}

	public ParsingException(Throwable cause) {
		super(cause);
	}

	public ParsingException(String message, Throwable cause) {
		super(message, cause);
	}
}
