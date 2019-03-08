/**
 * Copyright &copy; 2010 Kontorsplatsen Business Group AB
 * @author Eddie Olsson<eddie.olsson@kontorsplatsen.se>
 *
 * All rights reserved
 */

package com.fantasystep.persistence.ldap.exception;

/**
 * @author Eddie Olsson<eddie.olsson@kontorsplatsen.se>
 *
 */
public class MissingParameterException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6854061278401670425L;

	/**
	 * 
	 */
	public MissingParameterException() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 */
	public MissingParameterException(String arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 */
	public MissingParameterException(Throwable arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public MissingParameterException(String arg0, Throwable arg1) {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}

}
