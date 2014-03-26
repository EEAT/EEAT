package org.eeat.core.model.ocl;

import java.util.List;

import org.eeat.core.model.Event;

public interface OclMessage extends Event {

	public Object getArgument(int index);

	public Object getArgument(String name);
	
	public String[] getArgumentStrings();

	public Object[] getArguments();

	public String getClassName();

	/**
	 * @return the ID of the object that generated the event.
	 */
	public String getInstanceId();

	public String getMethodName();

	public String[] getParameters();

	public String getParameterType(int index);

	public String getParameterType(String name);

	public String[] getParameterTypes();

	/**
	 * @return the process ID of the object that generated the event.
	 */
	public String getProcessId();

	public String getReferredOperation();

	public OclMessage getReturnMessage();

	public String getReturnType();

	public Object getTarget();

	/**
	 * @return the thread ID of the object that generated the event.
	 */
	public String getThreadId();

	public boolean isOperationCall();

	public boolean isReturn();

	public boolean isSignalSent();

	public void setArguments(List<Object> arguments);

	// Jess must use arrays rather than lists...
	public void setArguments(Object[] arguments);

	public void setClassName(String name);

	public void setInstanceId(String instanceId);

	public void setOperationCall(boolean isOperationCall);

	public void setProcessId(String processId);

	public void setReferredOperation(String referredOperation);

	public void setReturnMessage(OclMessage returnMessage);

	public void setSignalSent(boolean isSignalSent);

	public void setTarget(Object target);

	public void setThreadId(String threadId);

}