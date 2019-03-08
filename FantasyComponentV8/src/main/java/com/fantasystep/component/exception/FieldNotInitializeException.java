package com.fantasystep.component.exception;

public class FieldNotInitializeException extends Exception
{
	private static final long serialVersionUID = -5085950438876791228L;

	public FieldNotInitializeException()
	{
		super();
	}

	public FieldNotInitializeException( String message )
	{
		super( message );
	}

	public FieldNotInitializeException( String message, Throwable cause )
	{
		super( message, cause );
	}

	public FieldNotInitializeException( Throwable cause )
	{
		super( cause );
	}
}
