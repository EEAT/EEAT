package org.eeat.repository.drools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Appender;
import org.eeat.core.plugin.Component;
import org.eeat.core.plugin.IAnalysisComponent;
import org.eeat.core.plugin.IRepository;
import org.eeat.core.plugin.IRepositoryComponent;
import org.eeat.core.plugin.RepositoryException;

public class RepositoryComponent extends Component implements Runnable, IRepositoryComponent {
	public boolean startConsole = false;
	LoadPluginThread pluginLoadThread = null;
	long loadSleepPeriod = 3000;
	Thread thread;
	IRepository repository = null;
	// TODO remove this list of appenders
	protected List<Appender> appenders = new ArrayList<Appender>();

	static RepositoryComponent instance = null;

	static protected RepositoryComponent getInstance() {
		return instance;
	}

	List<IAnalysisComponent> plugins = Collections.synchronizedList(new ArrayList<IAnalysisComponent>());

	public RepositoryComponent() {
		super();
		instance = this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eeat.repository.jess.IRepositoryComponent#addAppender(org.apache
	 * .log4j.Appender)
	 */
	public Appender addAppender(Appender appender) {
		appenders.add(appender);
		return appender;
	}


	@Override
	public synchronized void addPlugin(IAnalysisComponent plugin) {
		getLog().info(this + " addPlugin " + plugin);
		plugins.add(plugin);
		if (repository != null) {
			plugin.setRepository(repository);
			delayedLoadOfPlugins();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eeat.repository.jess.IRepositoryComponent#close()
	 */
	public void close() throws RepositoryException {
		getLog().debug("Closing repository " + this);
		repository.close();
		// Make a copy, because we're modifying the list...
		for (Appender appender : new ArrayList<Appender>(appenders)) {
			appender.close();
			this.removeAppender(appender);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eeat.repository.jess.IRepositoryComponent#console()
	 */
	public void console() {
		if (thread != null) {
			getLog().warn("Existing thread for repository is running.");
		}
		thread = new Thread(this);
		thread.start();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eeat.repository.jess.IRepositoryComponent#convertStreamToString
	 * (java.io.InputStream)
	 */
	public String convertStreamToString(InputStream is) {
		/*
		 * To convert the InputStream to String we use the
		 * BufferedReader.readLine() method. We iterate until the BufferedReader
		 * return null which means there's no more data to read. Each line will
		 * appended to a StringBuilder and returned as String.
		 */
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return sb.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eeat.repository.jess.IRepositoryComponent#delayedLoadOfPlugins()
	 */
	public void delayedLoadOfPlugins() {
		if (pluginLoadThread != null) {
			// Tell this thread to not load the plugins
			pluginLoadThread.setLoad(false);
		}
		if (getLoadSleepPeriod() > 0) {
			// Wait for the loadSleepPeriod (to collect all plugins) and then
			// load them in sort order.
			pluginLoadThread = new LoadPluginThread(this, getLoadSleepPeriod());
			pluginLoadThread.start();
		} else {
			loadPlugins();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eeat.repository.jess.IRepositoryComponent#description(java.lang
	 * .Object)
	 */
	public String description(Object notificaiton) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void finalize() throws Throwable {
		try {
			this.close();
		} finally {
			super.finalize();
		}
	}

	@Override
	public String getBundleSymbolName() {
		return "org.eeat.repository.drools";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eeat.repository.jess.IRepositoryComponent#getLoadSleepPeriod()
	 */
	public long getLoadSleepPeriod() {
		return loadSleepPeriod;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eeat.repository.jess.IRepositoryComponent#getPlugin(java.lang.String
	 * )
	 */
	public IAnalysisComponent getPlugin(String plugIn) {
		for (IAnalysisComponent p : getPlugins()) {
			if (p.getBundle().getSymbolicName().equals(plugIn)) {
				return p;
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eeat.repository.jess.IRepositoryComponent#getPluginProperty(java
	 * .lang.String, java.lang.String)
	 */
	public Object getPluginProperty(String plugIn, String property) {
		return getPlugin(plugIn).getProperty(property);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eeat.repository.jess.IRepositoryComponent#getPlugins()
	 */
	@Override
	public List<IAnalysisComponent> getPlugins() {
		return plugins;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eeat.repository.jess.IRepositoryComponent#getRepository()
	 */
	public IRepository getRepository() {
		return repository;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext
	 * )
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eeat.repository.jess.IRepositoryComponent#init(org.osgi.framework
	 * .BundleContext)
	 */
	@Override
	public void init() {
		super.init();
		getLog().debug("Bundle " + getBundle());
		try {
			loadSleepPeriod = (Integer.parseInt((String) getProperty("loadSleepPeriod")));
		} catch (NumberFormatException e) {
			getLog().error(e.toString());
		}
		startConsole = Boolean.parseBoolean((String) getProperty("startConsole"));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eeat.repository.jess.IRepositoryComponent#loadPlugins()
	 */
	public void loadPlugins() {
		getLog().debug("Loading plugins");
		if (plugins.size() == 0) {
			getLog().error("No plugins found: " + this);
		}
		Collections.sort(plugins);
		for (IAnalysisComponent p : plugins) {
			if (p.getRepository() != getRepository()) {
				// Only load the recently added (non-loaded) plugins...
				p.setRepository(getRepository());
			}
			p.load();
		}
	}

	protected IRepository newRepository() {
		Repository repo = new Repository();
		repo.setComponent(this);
		return repo;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eeat.repository.jess.IRepositoryComponent#removeAppender(org.apache
	 * .log4j.Appender)
	 */
	public void removeAppender(Appender appender) {
		appenders.remove(appender);
	}


	@Override
	public synchronized void removePlugin(IAnalysisComponent r) {
		plugins.remove(r);
		r.setRepository(null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eeat.repository.jess.IRepositoryComponent#run()
	 */
	public synchronized void run() {
		getLog().debug("Running " + this.getClass().getName());
		getLog().info(String.format("%n%s%nVersion: %s%n", getProperty("banner"), getVersion()));
		setRepository(newRepository());
		try {
			getRepository().reset();
		} catch (RepositoryException e1) {
			e1.printStackTrace();
			getLog().error(e1.toString());
		}
		if (pluginLoadThread == null) {
			loadPlugins();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eeat.repository.jess.IRepositoryComponent#setLoadSleepPeriod(long)
	 */
	public void setLoadSleepPeriod(long loadSleepPeriod) {
		this.loadSleepPeriod = loadSleepPeriod;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eeat.repository.jess.IRepositoryComponent#setPluginProperty(java
	 * .lang.String, java.lang.String, java.lang.Object)
	 */
	public Object setPluginProperty(String plugIn, String property, Object value) {
		return getPlugin(plugIn).setProperty(property, value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eeat.repository.jess.IRepositoryComponent#setRepository(org.reqmon
	 * .plugin.IRepository)
	 */
	public void setRepository(IRepository repository) {
		this.repository = repository;
		// Will be set with loadPlugins...
		// getLog().debug("setRepository: Updating repository plugin references.");
		// Collections.sort(plugins);
		// for (IJessComponent p : plugins) {
		// p.setRepository(repository);
		// }
		delayedLoadOfPlugins();
	}

}
