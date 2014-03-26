package org.eeat.core;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Constants {
	public static Logger logger = LoggerFactory.getLogger(Constants.class);

	static public Properties loadPropertyResource(final String name) {
		return loadPropertyResource(name, Constants.class);
	}

	@SuppressWarnings("rawtypes")
	static public Properties loadPropertyResource(final String name, final Class clazz) {
		Properties properties = null;
		final InputStream is = clazz.getResourceAsStream(name);
		if (is != null) {
			properties = new Properties();
			try {
				properties.load(is);
			} catch (final IOException e) {
				logger.error("Cannot load contents of property file: " + name);
			}
		} else {
			logger.warn("Cannot find property file: " + name);
		}
		return properties;
	}

	protected String filename = "constants.properties";

	private static Constants instance;

	protected static boolean loaded = false;

	public static List<String> getClasspathString() {
		// ClassLoader classLoader = this.getClass().getClassLoader();
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		if (classLoader == null) {
			classLoader = ClassLoader.getSystemClassLoader();
		}
		final List<String> list = new ArrayList<String>();
		final URL[] urls = ((URLClassLoader) classLoader).getURLs();
		for (final URL url : urls) {
			list.add(url.getFile().toString());
		}
		return list;
	}

	public static synchronized Constants getInstance() {
		if (instance == null) {
			instance = new Constants();
		}
		return instance;
	}

	static public String[] toArray(final String commaSeparated) {
		if (commaSeparated != null) {
			return commaSeparated.split(",");
		} else {
			return null;
		}
	}

	protected Properties properties;

	protected Constants() {
	}

	public String getFilename() {
		return filename;
	}

	public Properties getProperties() {
		if (!loaded) {
			properties = loadProperties();
		}
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

	public Properties loadProperties() {
		final Properties prop = loadPropertyResource(getFilename());
		if (prop != null) {
			loaded = true;
		}
		return prop;
	}

	public void setFilename(final String filename) {
		this.filename = filename;
	}

	public void setProperties(final Properties newProperties) {
		properties = newProperties;
	}

}
