package org.eeat.core.model;

import java.util.ArrayList;
import java.util.Date;

/**
 * Extension of an Event. Supporting events are a sequence. If the PropertyEvent is serving as a
 * scope, then the first supporting event is the opening event and the last is the closing event.
 * 
 * @author wnr
 * 
 */
public interface PropertyEvent extends Event {
	public static final int TRUE = 1;
	public static final int FALSE = -1;
	public static final int NONFALSE = 0;

	public Date getCloseDate();

	/**
	 * The event closed this property. Is is not a Support event. More typically, it is a scoping
	 * event.
	 * 
	 * @return
	 */
	public abstract Event getCloseEvent();

	/**
	 * @return  Time of the last event in support of property, not current time of analysis.
	 */
	public long getCloseTimestamp();

	public Date getOpenDate();

	/**
	 * The event that activated this property. It is the first event of Support
	 * 
	 * @return
	 */
	public abstract Event getOpenEvent();

	public long getOpenTimestamp();

	public abstract String getProperty();

	public abstract String getReason();

	public abstract int getSatisfied();

	public abstract PropertyEvent getScopeEvent();

	public abstract ArrayList<Event> getSupport();

	public void setCloseEvent(Event event);

	public abstract void setReason(String reason);

	public abstract void setSatisfied(int satisfied);

	void setSupport(ArrayList<Event> support);
	
	public Date getSupportFirstDate();
	
	public Date getSupportLastDate();

}