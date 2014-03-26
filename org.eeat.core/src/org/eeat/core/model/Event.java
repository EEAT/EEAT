package org.eeat.core.model;

import java.beans.PropertyChangeListener;
import java.util.Date;

/**
 * Basic event. If an assertion time is needed, consider rule that asserts the object and its
 * acquisition time.
 * 
 */
public interface Event {

	public void addPropertyChangeListener(PropertyChangeListener p);

	/**
	 * This is the same as getTimestamp, but returns a Date object instead
	 * 
	 * @return
	 */
	public Date getDate();

	/**
	 * Returns this event's actual object
	 * 
	 * @return
	 */
	public Object getObject();

	/**
	 * Returns the timestamp from this event
	 * 
	 * @return
	 */
	public long getTimestamp();

	/**
	 * Returns the event type
	 * 
	 * @return
	 */
	public String getType();
	
	public int getId();

	public void removePropertyChangeListener(PropertyChangeListener p);

}
