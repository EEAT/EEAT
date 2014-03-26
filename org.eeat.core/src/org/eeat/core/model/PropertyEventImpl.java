/**
 * 
 */
package org.eeat.core.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import org.eeat.core.exceptions.UnimplementedException;

/**
 * @author wnr
 * 
 */
public class PropertyEventImpl extends EventImpl implements PropertyEvent, Serializable {

	protected int id;

	private static final long serialVersionUID = 1L;

	private ArrayList<Event> support = new ArrayList<Event>();

	private String reason;

	private String property;

	private int satisfied;

	private PropertyEvent scopeEvent;

	private boolean closed = false;	

	protected PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

	static public long DEFAULT_OPENTIME = 0;

	static public long DEFAULT_CLOSETIME = Long.MAX_VALUE;

	public PropertyEventImpl() {
	}

	public PropertyEventImpl(String property, Event event, PropertyEvent scopeEvent, int satisfied,
			String reason) {
		this.property = property;
		this.support.add(event);
		this.scopeEvent = scopeEvent;
		this.satisfied = satisfied;
		this.reason = reason;
	}
	
	public PropertyEventImpl(String property, long timestamp) {
		this.property = property;
		this.support.add(new EventImpl(timestamp));
		this.scopeEvent = null;
		this.satisfied = PropertyEvent.TRUE;
		this.reason = "";
	}

	/**
	 * @param p
	 */
	@Override
	public void addPropertyChangeListener(PropertyChangeListener p) {
		propertyChangeSupport.addPropertyChangeListener(p);
	}

	public String describe() {
		return String.format("PropertyEvent@%s(%s,%s,%s),%d", hashCode(), property, satisfied,
				support, getTimestamp());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eeat.api.model.Event#getCloseDate()
	 */
	@Override
	public Date getCloseDate() {
		return (getCloseEvent() != null) ? new Date(getCloseEvent().getTimestamp()) : null;
	}
	


	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eeat.api.model.PropertyEvent#getCloseEvent()
	 */
	@Override
	public Event getCloseEvent() {
		return (closed && (support != null) && (support.size() > 0)) ? support
				.get(support.size() - 1) : null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eeat.api.model.Event#getCloseTimestamp()
	 */
	@Override
	public long getCloseTimestamp() {
		return (getCloseEvent() != null) ? getCloseEvent().getTimestamp() : DEFAULT_CLOSETIME;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eeat.api.model.Event#getDate()
	 */
	@Override
	public Date getDate() {
		return (getOpenEvent() != null) ? new Date(getOpenEvent().getTimestamp()) : null;
	}

	@Override
	public Date getOpenDate() {
		return (getTimestamp() < 0) ? new Date(getTimestamp()) : null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eeat.api.model.PropertyEvent#getOpenEvent()
	 */
	@Override
	public Event getOpenEvent() {
		return ((support != null) && (support.size() > 0)) ? support.get(0) : null;
	}

	@Override
	public long getOpenTimestamp() {
		return getTimestamp();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eeat.api.model.PropertyEvent#getProperty()
	 */
	@Override
	public String getProperty() {
		return property;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eeat.api.model.PropertyEvent#getReason()
	 */
	@Override
	public String getReason() {
		return reason;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eeat.api.model.PropertyEvent#getSatisfied()
	 */
	@Override
	public int getSatisfied() {
		return satisfied;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eeat.api.model.PropertyEvent#getScopeEvent()
	 */
	@Override
	public PropertyEvent getScopeEvent() {
		return scopeEvent;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eeat.api.model.PropertyEvent#getSupport()
	 */
	@Override
	public ArrayList<Event> getSupport() {
		return support;
	}
	
	public Date getSupportFirstDate() {
		if (support !=null && support.size()>0)
			return support.get(0).getDate();
		else
			return null;
	}
	
	public Date getSupportLastDate() {
		if (support !=null && support.size()>0)
			return support.get(support.size()-1).getDate();
		else
			return null;
	}
	
	public long setSupportLastDate(Date date) {
		throw new UnimplementedException("Cannot set support date. Only available to enable Hibernate. Instead, set the support events.");
	}
	

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eeat.api.model.Event#getTimestamp()
	 */
	@Override
	public long getTimestamp() {
		return (getOpenEvent() != null) ? getOpenEvent().getTimestamp() : DEFAULT_OPENTIME;
	}

	/**
	 * @param p
	 */
	@Override
	public void removePropertyChangeListener(PropertyChangeListener p) {
		propertyChangeSupport.removePropertyChangeListener(p);
	}

	protected String satisfied(int satisfied) {
		String res = "nonfalse";
		if (satisfied < 0) {
			res = "false";
		} else if (satisfied > 0) {
			res = "true";
		}
		return res;
	}

	@Override
	public void setCloseEvent(Event event) {
		Object oldValue = getCloseEvent();
		closed = true;
		support.add(event);
		propertyChangeSupport.firePropertyChange("closeEvent", oldValue, event);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eeat.api.model.PropertyEvent#setReason(java.lang.String)
	 */
	@Override
	public void setReason(String reason) {
		Object oldValue = this.reason;
		this.reason = reason;
		propertyChangeSupport.firePropertyChange("reason", oldValue, reason);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eeat.api.model.PropertyEvent#setSatisfied(int)
	 */
	@Override
	public void setSatisfied(int satisfied) {
		Object oldValue = this.satisfied;
		this.satisfied = satisfied;
		propertyChangeSupport.firePropertyChange("satisfied", oldValue, satisfied);
	}

	@Override
	public void setSupport(ArrayList<Event> support) {
		this.support = support;
	}

	@Override
	public String toString() {
		return String.format("PropertyEvent@%s(%s,%s,%s),%d", hashCode(), property,
				satisfied(satisfied), scopeEvent != null ? scopeEvent.getProperty() : "",
				getTimestamp());
	}

}
