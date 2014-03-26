package org.eeat.core.plugin;

import java.util.Properties;
import org.slf4j.Logger;
import org.osgi.framework.Bundle;

public interface IComponent {

	public abstract Bundle getBundle();

	public abstract String getBundleSymbolName();

	public abstract Logger getLog();

	public abstract Properties getProperties();

	public abstract Object getProperty(String name);

	public abstract String getVersion();

	public abstract Object setProperty(String name, Object value);

}