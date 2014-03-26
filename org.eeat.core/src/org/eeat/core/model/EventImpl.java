package org.eeat.core.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Date;

/**
 * A default implementation for Event
 * 
 */
public class EventImpl implements Event {
	private final long timestamp;
	private final Object object;
	protected int id;

	protected PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

	public EventImpl() {
		super();
		this.timestamp = new Date().getTime();
		this.object = null;
	}

	public EventImpl(long timestamp) {
		super();
		this.timestamp = timestamp;
		this.object = null;
	}

	public EventImpl(Object object, long timestamp) {
		super();
		this.timestamp = timestamp;
		this.object = object;
	}

	/**
	 * @param p
	 */
	public void addPropertyChangeListener(PropertyChangeListener p) {
		propertyChangeSupport.addPropertyChangeListener(p);
	}

	@Override
	public Date getDate() {
		return new Date(this.timestamp);
	}

	@Override
	public int getId() {
		return id;
	}

	@Override
	public Object getObject() {
		return object;
	}

	@Override
	public long getTimestamp() {
		return timestamp;
	}

	@Override
	public String getType() {
		return (getObject() != null) ? getObject().getClass().toString() : null;
	}

	/**
	 * @param p
	 */
	public void removePropertyChangeListener(PropertyChangeListener p) {
		propertyChangeSupport.removePropertyChangeListener(p);
	}

	@Override
	public String toString() {
		return toStringCustom("Event");
	}

	protected String toStringCustom(String name) {
		return String.format("%s@%s,%d", name, hashCode(), getTimestamp());
	}

}
