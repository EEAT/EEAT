package org.eeat.core.util;

/*******************************************************************************
 * Copyright (c) 2009 Paul VanderLei, Simon Archer, Jeff McAffer and others. All
 * rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Toast is an Equinox/OSGi system developed for the book Eclipse, Equinox and
 * OSGi - Creating Highly Modular Java Applications See http://equinoxosgi.org
 * 
 * Contributors: Paul VanderLei, Simon Archer and Jeff McAffer - initial API and
 * implementation
 *******************************************************************************/

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SystemPropertyManager {
	private static Logger logger = LoggerFactory
			.getLogger(SystemPropertyManager.class);

	private static final SystemPropertyManager INSTANCE = new SystemPropertyManager();

	public static boolean getBooleanProperty(final String property) {
		return SystemPropertyManager.INSTANCE.instanceGetBooleanProperty(property);
	}

	public static boolean getBooleanProperty(final String property,
			final boolean defaultValue) {
		return SystemPropertyManager.INSTANCE.instanceGetBooleanProperty(property,
				defaultValue);
	}

	public static String getProperty(final String property) {
		return SystemPropertyManager.INSTANCE.instanceGetProperty(property);
	}

	public static String getProperty(final String property,
			final String defaultValue) {
		return SystemPropertyManager.INSTANCE.instanceGetProperty(property,
				defaultValue);
	}

	private final Map cache;

	private SystemPropertyManager() {
		super();
		cache = new HashMap(11);
	}

	private boolean instanceGetBooleanProperty(final String property) {
		final boolean value = instanceGetBooleanProperty(property, false);
		return value;
	}

	private boolean instanceGetBooleanProperty(final String property,
			final boolean defaultValue) {
		final String defaultValueString = String.valueOf(defaultValue);
		final String value = instanceGetProperty(property, defaultValueString);
		final Boolean wrapper = Boolean.valueOf(value);
		final boolean state = wrapper.booleanValue();
		return state;
	}

	private String instanceGetProperty(final String property) {
		final String value = instanceGetProperty(property, null);
		return value;
	}

	private String instanceGetProperty(final String property,
			final String defaultValue) {
		if (property == null) {
			throw new IllegalArgumentException("property must not be null");
		}
		String value;
		synchronized (cache) {
			value = (String) cache.get(property);
			if (value == null) {
				value = System.getProperty(property, defaultValue);
				logger.debug("Property: -D" + property + '=' + value);
				if (value != null) {
					cache.put(property, value);
				}
			}
		}
		return value;
	}
}
