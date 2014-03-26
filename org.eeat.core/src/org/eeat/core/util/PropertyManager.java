package org.eeat.core.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

//import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertyManager {
	private static Logger logger = LoggerFactory
			.getLogger(PropertyManager.class);

	static public String[] toArray(final String commaSeparated) {
		if (commaSeparated != null) {
			return commaSeparated.split(",");
		} else {
			return null;
		}
	}

	String PropertyPath = "/bundle.properties";
	Properties properties;
	Bundle bundle;

	public PropertyManager(final Bundle bundle) {
		this.bundle = bundle;
		try {
			init();
		} catch (final Exception e) {
			logger.error(e.toString());
		}
	}

	public Bundle getBundle() {
		// TODO deal with bundles
//		return Platform.getBundle(getBundleSymbolName());
		return null;
	}

	private String getBundleSymbolName() {
		return bundle.getSymbolicName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eeat.core.plugin.IActivator#getProperties()
	 */
	public Properties getProperties() {
		return properties;
	}

	public Object getProperty(final String name) {
		final Properties p = getProperties();
		if (p != null) {
			return p.get(name);
		}
		logger.debug("Null properties while accessing: " + name);
		return null;
	}

	public String getPropertyPath() {
		return PropertyPath;
	}

	public String getVersion() {
		return getBundle().getHeaders().get(Constants.BUNDLE_VERSION);
	}

	public void init() {
		loadProperties();
	}

	protected void loadProperties() {
		if (getBundle() == null) {
			logger.error(String
					.format("Cannot load properites because Eclipse Bundle is null in %s (%s) with path %s.",
							getBundleSymbolName(), this, getPropertyPath()));
			return;
		}
		InputStream propsStream;
		try {
			final URL url = getBundle().getEntry(getPropertyPath());
			if (url != null) {
				propsStream = url.openStream();
				properties = new Properties();
				properties.load(propsStream);
			}
		} catch (final IOException e) {
			logger.error(String
					.format("Property file not found for %s (%s) with path %s.",
							getBundleSymbolName(), this, getPropertyPath()));
		}

	}

	public Object setProperty(final String name, final Object value) {
		final Properties p = getProperties();
		p.put(name, value);
		return value;
	}

	public void setPropertyPath(final String propertyPath) {
		PropertyPath = propertyPath;
	}
}
