package org.eeat.core.plugin;

import java.io.InputStream;
import java.util.List;


public interface IRepositoryComponent {

	public abstract void addPlugin(IAnalysisComponent plugin);

	public abstract void close() throws RepositoryException;

	public abstract void console();

	public abstract String convertStreamToString(InputStream is);

	public abstract void delayedLoadOfPlugins();

	public abstract String description(Object notificaiton);

	public abstract long getLoadSleepPeriod();

	public abstract IAnalysisComponent getPlugin(String plugIn);

	public abstract Object getPluginProperty(String plugIn, String property);

	public abstract List<IAnalysisComponent> getPlugins();

	public abstract IRepository getRepository();

	public abstract void init();

	public abstract void loadPlugins();

	public abstract void removePlugin(IAnalysisComponent r);

	public abstract void run();

	public abstract void setLoadSleepPeriod(long loadSleepPeriod);

	public abstract Object setPluginProperty(String plugIn, String property, Object value);

	public abstract void setRepository(IRepository repository);

}