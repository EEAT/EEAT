package org.eeat.core.model;

import org.eeat.core.model.ocl.OclMessage;

public class ContextVariableImpl extends EventImpl implements ContextVariable {
	private final String name;
	private final String variable;
	

	public ContextVariableImpl(String name, String variable, Event object, long timestamp) {
		super(object, timestamp);
		this.name = name;
		this.variable = variable;
	}
	

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eeat.api.model.IContextVariable#getName()
	 */
	public String getName() {
		return name;
	}

	@Override
	public OclMessage getObject() {
		return (OclMessage) super.getObject();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eeat.api.model.IContextVariable#getVariable()
	 */
	public String getVariable() {
		return variable;
	}

	@Override
	public String toString() {
		return String.format("ContextVariable@%s(%s,%s,%s),%d", hashCode(), name, variable,
				getObject(), getTimestamp());
	}

}
