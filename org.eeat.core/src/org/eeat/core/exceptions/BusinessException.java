/*
 * $Author: wnr $
 * $Date: 2007/02/25 21:20:33 $
 * $Header: /open/reqmon/src/main/java/org/reqmon/exceptions/BusinessException.java,v 1.1 2007/02/25 21:20:33 wnr Exp $
 * $Revision: 1.1 $
 */

package org.eeat.core.exceptions;

/**
 * This exception is used to mark business rule violations.
 * 
 * @author Christian Bauer <christian@hibernate.org>
 */
public class BusinessException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7583281758750983719L;

	public BusinessException() {
	}

	public BusinessException(String message) {
		super(message);
	}

	public BusinessException(String message, Throwable cause) {
		super(message, cause);
	}

	public BusinessException(Throwable cause) {
		super(cause);
	}
}
