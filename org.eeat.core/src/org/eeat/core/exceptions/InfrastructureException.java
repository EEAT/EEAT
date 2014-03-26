/*
 * $Author: wnr $
 * $Date: 2007/02/25 21:20:33 $
 * $Header: /open/reqmon/src/main/java/org/reqmon/exceptions/InfrastructureException.java,v 1.1 2007/02/25 21:20:33 wnr Exp $
 * $Revision: 1.1 $
 */

package org.eeat.core.exceptions;

/**
 * This exception is used to mark (fatal) failures in infrastructure and system code.
 * 
 * @author Christian Bauer <christian@hibernate.org>
 */
public class InfrastructureException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -197557663043497685L;

	public InfrastructureException() {
	}

	public InfrastructureException(String message) {
		super(message);
	}

	public InfrastructureException(String message, Throwable cause) {
		super(message, cause);
	}

	public InfrastructureException(Throwable cause) {
		super(cause);
	}
}
