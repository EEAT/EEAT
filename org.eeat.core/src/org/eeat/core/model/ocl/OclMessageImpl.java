package org.eeat.core.model.ocl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eeat.core.model.EventImpl;

public class OclMessageImpl extends EventImpl implements OclMessage {
	private String classname;
	private String methodSignature;
	protected List<Object> arguments = new ArrayList<Object>();
	protected List<String> parameters = new ArrayList<String>();
	protected boolean isOperationCall = true;;
	protected boolean isReturn = false;

	protected Object returnValue;

	protected boolean isSignalSent;
	private Object target;

	private String instanceId;
	private String processId;

	private String threadId;
	
	public OclMessageImpl() {
		super();
	}

	public OclMessageImpl(String classname, String methodSignature, Object[] arguments,
			Object object, long timestamp) {
		super(object, timestamp);
		this.classname = classname;
		setMethodSignature(methodSignature);
		setArguments(arguments);
	}

	public OclMessageImpl(String classname, String methodSignature, Object[] arguments) {
		super();
		this.classname = classname;
		setMethodSignature(methodSignature);
		setArguments(arguments);
	}

	@Override
	public Object getArgument(int index) {
		return (index < arguments.size()) ? getArguments()[index] : null;
	}

	@Override
	public Object getArgument(String name) {
		if (Arrays.asList(getParameters()).indexOf(name) >= 0) {
			return getArgument(Arrays.asList(getParameters()).indexOf(name));
		}
		return null;
	}

	@Override
	public Object[] getArguments() {
		return arguments.toArray();
	}
	
    // TODO only needed to store arguments using Hibernate; update this.
    public String[] getArgumentStrings() {
        String[] sa = new String[arguments.size()];
        int i = 0;
        for (Object o : arguments)
        	if (o!=null) sa[i++] = o.toString();
        	else sa[i++] = "";
        return sa;
        // return (String [])arguments.toArray(new String [arguments.size ()]);   	
    }

	public OclMessageImpl(String classname, String methodSignature, Object object, long timestamp) {
		super(object, timestamp);
		this.classname = classname;
		setMethodSignature(methodSignature);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eeat.api.model.ocl.IOclMessage#getClassName()
	 */
	public String getClassName() {
		return classname;
	}

	@Override
	public String getInstanceId() {
		return instanceId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eeat.api.model.ocl.IOclMessage#getMethodName()
	 */
	// Assume that is standard TPTP method, which has parens; otherwise, just
	// return component.
	public String getMethodName() {
		if (getMethodSignature() == null) {
			return ("");
		}

		String s = null;
		if (getMethodSignature().indexOf("(") < 0) {
			s = getMethodSignature();
		} else {
			try {
				s = getMethodSignature().substring(0, getMethodSignature().indexOf("("));
				s = s.trim();
			} catch (IndexOutOfBoundsException e) {
			} catch (NullPointerException e2) {
			}
		}
		return s;
	}

	public String getMethodSignature() {
		return methodSignature;
	}

	@Override
	public String[] getParameters() {
		return parameters.toArray(new String[parameters.size()]);
	}

	public Object getParameters(int index) {
		return (index < parameters.size()) ? getParameters()[index] : null;
	}

	@Override
	public String getParameterType(int index) {
		return getParameterTypes()[index];
	}

	@Override
	public String getParameterType(String name) {
		return getParameterType(Arrays.asList(getParameters()).indexOf(name));
	}

	@Override
	public String[] getParameterTypes() {
		if (getMethodSignature() == null) {
			return new String[] {};
		}

		ArrayList<String> l = new ArrayList<String>();
		String sc = getMethodSignature();
		try {
			sc = sc.substring(sc.indexOf("(") + 1, sc.indexOf(")"));
			String[] params = sc.split(",");
			for (String p : params) {
				l.add(p);
			}
		} catch (IndexOutOfBoundsException e) {
		} catch (NullPointerException e2) {
		}
		return l.toArray(new String[] {});
	}

	@Override
	public String getProcessId() {
		return processId;
	}

	@Override
	public String getReferredOperation() {
		return null;
	}

	@Override
	public OclMessage getReturnMessage() {
		return null;
	}

	@Override
	// TODO The subComponent doesn't have the return type, so this doesn't work.
	public String getReturnType() {
		String s = null;
		try {
			s = getMethodSignature().substring(getMethodSignature().indexOf(":") + 1,
					getMethodSignature().indexOf(";"));
			s = s.trim();
		} catch (IndexOutOfBoundsException e) {
		} catch (NullPointerException e2) {
		}
		return s;
	}

	public Object getReturnValue() {
		return returnValue;
	}

	@Override
	public Object getTarget() {
		return target;
	}

	@Override
	public String getThreadId() {
		return threadId;
	}

	@Override
	public boolean isOperationCall() {
		return isOperationCall;
	}

	@Override
	public boolean isReturn() {
		return isReturn;
	}

	@Override
	public boolean isSignalSent() {
		return isSignalSent;
	}

	public void setArguments(List<Object> arguments) {
		this.arguments = arguments;
	}

	@Override
	public void setArguments(Object[] arguments) {
		ArrayList<Object> l = new ArrayList<Object>();
		l.addAll(Arrays.asList(arguments));
		setArguments(l);
	}

	@Override
	public void setClassName(String name) {
		this.classname = name;
	}

	public void setInstanceId(String instanceId) {
		this.instanceId = instanceId;
	}

	protected void setMethodSignature(String methodSignature) {
		this.methodSignature = methodSignature;
	}

	@Override
	public void setOperationCall(boolean isOperationCall) {
		this.isOperationCall = isOperationCall;
	}

	public void setParameters(List<String> parameters) {
		this.parameters = parameters;
	}

	public void setParameters(Object[] parameters) {
		ArrayList<String> l = new ArrayList<String>();
		for (Object o : parameters) {
			l.add(o.toString());
		}
		setParameters(l);
	}

	public void setParameters(String[] parameters) {
		ArrayList<String> l = new ArrayList<String>();
		l.addAll(Arrays.asList(parameters));
		setParameters(l);
	}

	public void setProcessId(String processId) {
		this.processId = processId;
	}

	@Override
	public void setReferredOperation(String referredOperation) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setReturnMessage(OclMessage returnMessage) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setSignalSent(boolean isSignalSent) {
		this.isSignalSent = isSignalSent;
	}

	@Override
	public void setTarget(Object target) {
		this.target = target;
	}

	public void setThreadId(String threadId) {
		this.threadId = threadId;
	}

	@Override
	public String toString() {
		return toStringCustom("OclMessage");
	}

	@Override
	public String toStringCustom(String name) {
		String id = (getInstanceId() == null) ?  hashCode() +"" : getInstanceId();
		return String.format("%s@%s(%s,%s),%d", name, id, classname, getMethodName(),
				getTimestamp());
	}
	
    // TODO only needed to store arguments using Hibernate; update this.
    public void setArgumentStrings(String[] arguments) {
        ArrayList<Object> l = new ArrayList<Object>();
        l.addAll(Arrays.asList(arguments));
        setArguments(l);
    }

}
