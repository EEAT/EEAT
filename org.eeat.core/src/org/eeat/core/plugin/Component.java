package org.eeat.core.plugin;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Component implements IComponent {
	
	

	static public String[] toArray(String commaSeparated) {
		if (commaSeparated != null)
			return commaSeparated.split(",");
		else
			return null;
	}

	String PropertyPath = "/bundle.properties";
	Logger logger;
	Properties properties;

	public Component() {
		getLog().debug("Creating component: " + this);
		try {
			init();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.err.println(e.toString());
			e.printStackTrace();
		}
	}

	public Bundle getBundle() {
		return Platform.getBundle(getBundleSymbolName());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eeat.core.plugin.IActivator#getLog()
	 */
	public Logger getLog() {
		if (logger == null)
			logger = LoggerFactory.getLogger(this.getClass());
		return logger;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eeat.core.plugin.IActivator#getProperties()
	 */
	public Properties getProperties() {
		return properties;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eeat.core.plugin.IActivator#getProperty(java.lang.String)
	 */
	public Object getProperty(String name) {
		Properties p = getProperties();
		if (p != null)
			return p.get(name);
		getLog().debug("Null properties while accessing: " + name);
		return null;
	}

	public String getPropertyPath() {
		return PropertyPath;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eeat.core.plugin.IActivator#getVersion()
	 */
	public String getVersion() {
		return (String) getBundle().getHeaders().get(Constants.BUNDLE_VERSION);
	}

	public void init() {
		logger = LoggerFactory.getLogger(getBundleSymbolName());
		loadProperties();
	}

	protected void loadProperties() {
		if (getBundle() == null) {
			getLog().error(
					"Cannot load properites because Eclipse Bundle is null in " + this
							+ " with bundle named " + getBundleSymbolName());
			return;
		}
		InputStream propsStream;
		try {
			URL url = getBundle().getEntry(getPropertyPath());
			if (url != null) {
				propsStream = url.openStream();
				properties = new Properties();
				properties.load(propsStream);
			}
		} catch (IOException e) {
			getLog().warn("Property file not found: " + getPropertyPath());
		}

	}

	@Override
	public Object setProperty(String name, Object value) {
		Properties p = getProperties();
		p.put(name, value);
		return value;
	}

	public void setPropertyPath(String propertyPath) {
		PropertyPath = propertyPath;
	}

}
