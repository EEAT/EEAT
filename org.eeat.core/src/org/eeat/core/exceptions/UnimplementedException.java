/*
 * $Author: wnr $
 * $Date: 2007/02/25 21:20:33 $
 * $Header: /open/reqmon/src/main/java/org/reqmon/exceptions/BusinessException.java,v 1.1 2007/02/25 21:20:33 wnr Exp $
 * $Revision: 1.1 $
 */

package org.eeat.core.exceptions;

public class UnimplementedException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7583281758750983719L;

	public UnimplementedException() {
	}

	public UnimplementedException(String message) {
		super(message);
	}

	public UnimplementedException(String message, Throwable cause) {
		super(message, cause);
	}

	public UnimplementedException(Throwable cause) {
		super(cause);
	}
}
