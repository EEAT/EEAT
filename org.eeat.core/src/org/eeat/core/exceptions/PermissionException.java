/*
 * $Author: wnr $
 * $Date: 2007/02/25 21:20:33 $
 * $Header: /open/reqmon/src/main/java/org/reqmon/exceptions/PermissionException.java,v 1.1 2007/02/25 21:20:33 wnr Exp $
 * $Revision: 1.1 $
 */
package org.eeat.core.exceptions;

/**
 * This exception is used to mark access violations.
 * 
 * @author Christian Bauer <christian@hibernate.org>
 */
public class PermissionException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2746738957905217704L;

	public PermissionException() {
	}

	public PermissionException(String message) {
		super(message);
	}

	public PermissionException(String message, Throwable cause) {
		super(message, cause);
	}

	public PermissionException(Throwable cause) {
		super(cause);
	}
}
