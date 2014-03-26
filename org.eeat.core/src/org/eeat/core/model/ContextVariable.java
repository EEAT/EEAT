package org.eeat.core.model;

import java.util.Date;

import org.eeat.core.model.ocl.OclMessage;

// TODO Ensure that ContextVariable is in an Event (which will support its memory management)
public interface ContextVariable { // extends Event and overrides getObject (but
	// the extends is not understood by Drools
	// compiler

	/**
	 * This is the same as getTimestamp, but returns a Date object instead
	 * 
	 * @return
	 */
	public Date getDate();

	public String getName();

	/**
	 * Returns this event's actual object
	 * 
	 * @return
	 */
	// TODO This should be Event rather than OclMessage. Update when Drools (v
	// 6?) has casting in patterns:
	// http://community.jboss.org/wiki/DroolsPatternLanguage
	public OclMessage getObject();

	/**
	 * Returns the timestamp from this event
	 * 
	 * @return
	 */
	public long getTimestamp();

	public String getVariable();
	


}