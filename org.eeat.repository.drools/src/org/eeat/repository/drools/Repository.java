package org.eeat.repository.drools;

import java.io.File;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseConfiguration;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderConfiguration;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.conf.EventProcessingOption;
import org.drools.io.ResourceFactory;
import org.drools.logger.KnowledgeRuntimeLogger;
import org.drools.logger.KnowledgeRuntimeLoggerFactory;
import org.drools.rule.EntryPoint;
import org.drools.runtime.KnowledgeSessionConfiguration;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.rule.WorkingMemoryEntryPoint;
import org.eeat.core.Constants;
import org.eeat.core.plugin.IAnalysisComponent;
import org.eeat.core.plugin.IRepository;
import org.eeat.core.plugin.RepositoryException;
import org.eeat.repository.hibernate.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Repository implements IRepository {
	KnowledgeBase knowledgeBase;
	StatefulKnowledgeSession session;
	WorkingMemoryEntryPoint eventStream;
	RepositoryComponent component;
	KnowledgeRuntimeLogger logger;
	Thread consoleThread;
	ConsoleCommandReader consoleCommandReader;

	// Hard coded, so does not depend on Eclipse properties lookup to start.
	// Used for minimal start
	final String resourceFolder = "/resources";
	final String loadFile = resourceFolder + "/repository/load-changeset.xml";

	static final String RUN_AFTER_INSERT = "runAfterInsert";
	boolean runAfterInsert = true;

	public Repository() {
	}

	protected void addRuleResource(String path) {
		Properties kbuilderProperties = Constants.loadPropertyResource(resourceFolder
				+ "/kbuilder.properties");
		KnowledgeBuilderConfiguration builderConf = KnowledgeBuilderFactory
				.newKnowledgeBuilderConfiguration(kbuilderProperties, null);
		KnowledgeBuilder knowledgeBuilder = KnowledgeBuilderFactory
				.newKnowledgeBuilder(builderConf);
		if (path.endsWith("changeset.xml")) {
			// TODO If xml-apis.jar is in classpath, then CHANGE_SET fails...
			knowledgeBuilder.add(ResourceFactory.newClassPathResource(path, this.getClass()),
					ResourceType.CHANGE_SET);
		} else {
			knowledgeBuilder.add(ResourceFactory.newClassPathResource(path, this.getClass()),
					ResourceType.DRL);
		}
		if (knowledgeBuilder.hasErrors()) {
			throw new RuntimeException(knowledgeBuilder.getErrors().toString());
		}
		knowledgeBase.addKnowledgePackages(knowledgeBuilder.getKnowledgePackages());
		getLog().info("Added rule package: " + path);
	}

	@Override
	public void assertObject(Object object, boolean dynamic) {
		try {
			insert(object, dynamic);
		} catch (RepositoryException ex) {
			new RuntimeException(ex.toString());
		}
	}

	@Override
	public void assertTemplate(String ModuleName, Class aClass, String parent)
			throws RepositoryException {
		// TODO Auto-generated method stub
		throw new RuntimeException("Not implemented.");
	}

	@Override
	public void batch(String commandFile) throws RepositoryException {
		addRuleResource(commandFile);
	}

	@Override
	public void batch(String commandFile, ClassLoader classLoader) throws RepositoryException {
		// TODO Auto-generated method stub
		throw new RuntimeException("Not implemented.");
	}

	@Override
	public void clearSession() throws RepositoryException {
		Session s = HibernateUtil.getSession();
		s.flush();
		s.clear();
	}

	@Override
	public void close() throws RepositoryException {
		// TODO Auto-generated method stub
		this.closeAudit();
		session.halt();
		stopConsole();
		// closeSession();
		session.dispose();
	}

	public void closeAudit() {
		logger.close();
	}

	@Override
	public void closeSession() throws RepositoryException {
		HibernateUtil.closeSession();
	}

	@Override
	public void commitTransaction() throws RepositoryException {
		HibernateUtil.commitTransaction();
	}

	@Override
	public void console(boolean doPrompt, PrintWriter writer, Reader reader)
			throws RepositoryException {
		// TODO Auto-generated method stub
		throw new RuntimeException("Not implemented.");
	}

	@Override
	public int countQueryResults(String query, Integer value) throws RepositoryException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int countQueryResults(String query, Object... args) throws RepositoryException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int countQueryResults(String query, String name, Integer value)
			throws RepositoryException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Object delete(Object object) throws RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object execute(String command) throws RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void finalize() throws Throwable {
		try {
			logger.close();
		} finally {
			super.finalize();
		}
	}

	public Collection findByCriteria(Class objectClass, Criterion... criterion) {
		return HibernateUtil.findByCriteria(objectClass, criterion);
	}

	@Override
	public Collection findByExample(Object object) throws RepositoryException {
		return HibernateUtil.findByExample(object);
	}

	public RepositoryComponent getComponent() {
		return component;
	}

	@Override
	public Object getEngine() {
		return session;
	}

	@Override
	public int getIdRBS() {
		return session.hashCode();
	}

	public Logger getLog() {
		Logger logger;
		if (component == null) {
			logger = LoggerFactory.getLogger(this.getClass());
		} else {
			logger = component.getLog();
		}
		return logger;
	}

	@Override
	public Object getPluginProperty(String plugIn, String property) {
		return getComponent().getPluginProperty(plugIn, property);
	}

	@Override
	public List<IAnalysisComponent> getPlugins() {
		return getComponent().getPlugins();
	}

	public StatefulKnowledgeSession getRuleSession() {
		return session;
	}

	@Override
	public void halt() throws RepositoryException {
		session.halt();
	}

	protected void initKB() {
		Properties kbaseProperties = Constants.loadPropertyResource(resourceFolder
				+ "/kbase.properties");
		// If properties not found, the load default...
		if (kbaseProperties == null) {
			kbaseProperties = Constants.loadPropertyResource(resourceFolder
					+ "/kbase-default.properties");
			getLog().info("Loading default: " + resourceFolder + "/kbase-default.properties");
		}
		KnowledgeBaseConfiguration config = KnowledgeBaseFactory.newKnowledgeBaseConfiguration(
				kbaseProperties, null);
		// Stream option should be set in property file
		config.setOption(EventProcessingOption.STREAM);
		knowledgeBase = KnowledgeBaseFactory.newKnowledgeBase(config);
	}

	protected void initSession() {
		Properties sessionProperties = Constants.loadPropertyResource(resourceFolder
				+ "/session.properties");
		// If properties not found, the load default...
		if (sessionProperties == null) {
			sessionProperties = Constants.loadPropertyResource(resourceFolder
					+ "/session-default.properties");
			getLog().info("Loading default: " + resourceFolder + "/session-default.properties");
		}
		KnowledgeSessionConfiguration conf = KnowledgeBaseFactory
				.newKnowledgeSessionConfiguration(sessionProperties);
		session = knowledgeBase.newStatefulKnowledgeSession(conf, null);
		logger = createLogFile(session);
	}
	
	protected KnowledgeRuntimeLogger createLogFile(StatefulKnowledgeSession session) {
		KnowledgeRuntimeLogger newLogger = null;
		// BUG This seems to always create a logfile, even when none in bundle.properties
		String logFile = null;
		if (getComponent() == null) { // This occurs when run outside of OSGi
			logFile = "eeatAudit";
		} else {
			logFile = (String) getComponent().getProperty("logFile");
		}
		// TODO fix the log
		// logFile = null;
		// Seems to be working now.
		if (logFile != null) {
			String logFilename = logFile;
			File f = new File(logFilename);
			if (!f.exists()) {
				logFilename = System.getProperty("java.io.tmpdir") + "eeatAudit";
			}
			newLogger = KnowledgeRuntimeLoggerFactory.newFileLogger(session, logFilename);
			getLog().info("Drools logging to file: " + logFilename);
		} else {
			getLog().warn("Running without Drools logfile. No log file set in bundle.properties.");
		}
		return newLogger;
	}

	@Override
	public Object insert(Object object) throws RepositoryException {
		return insert(object, true);
	}

	@Override
	public Object insert(Object object, boolean dynamic) throws RepositoryException {
		getLog().debug("Inserting: " + object);
		if (eventStream != null) {
			eventStream.insert(object);
		} else {
			session.insert(object);
		}
		if (isRunAfterInsert())
			session.fireAllRules();
		return object;
	}

	public boolean isRunAfterInsert() {
		return runAfterInsert;
	}

	@Override
	public Object makePersistent(Object object) throws RepositoryException {
		HibernateUtil.beginTransaction();
		return HibernateUtil.makePersistent(object);
	}

	@Override
	public void makeTransient(Object object) throws RepositoryException {
		HibernateUtil.makeTransient(object);
	}

	@Override
	public void notify(Object object) {
		// TODO Auto-generated method stub

	}

	@Override
	public Session getSession() throws RepositoryException {
		return HibernateUtil.getSession();
	}

	public List<Object> query(String query) throws RepositoryException {
		return query(query, QueryType.HQL);
	}

	@Override
	public List<Object> query(String query, QueryType type, Object... args)
			throws RepositoryException {
		// TODO Address arguments in query.
		List l = null;
		if (type == QueryType.HQL) {
			l = HibernateUtil.getSession().createQuery(query).list();
		} else if (type == QueryType.SQL) {
			l = HibernateUtil.getSession().createSQLQuery(query).list();
		} else {
			getLog().error("Unknown query type.");
		}
		return l;
	}

	@Override
	public void reset() throws RepositoryException {
		getLog().warn("Repository reset.");
		if ((getComponent() != null)
				&& (getComponent().getProperty(RUN_AFTER_INSERT) != null)
				&& ((String) getComponent().getProperty(RUN_AFTER_INSERT))
						.equalsIgnoreCase("false"))
			setRunAfterInsert(false);
		initKB();
		initSession();
		eventStream = session.getWorkingMemoryEntryPoint(EntryPoint.DEFAULT.getEntryPointId());
		if (getComponent() == null) {
			getLog().warn("Running minimal start because OSGi is absent.");
			batch(loadFile);
		} else {
			batch((String) getComponent().getProperty(IAnalysisComponent.LOADFILE));
			// setStream("events");
		}
		try {
			// Globals set only after their specification in the rules...
			session.setGlobal("repository", this);
			// TODO if auto start then...
			// session.fireAllRules();
		} catch (Exception e) {
			getLog().error("Initialization error: " + e.toString());
		}
		startConsole();
	}

	@Override
	public void run() throws RepositoryException {
		session.fireAllRules();
		// TODO consider a better way to update the Drools log
		// Create a new log after each run
		// logger.close();
	}

	public void setComponent(RepositoryComponent component) {
		this.component = component;
	}

	@Override
	public Object setPluginProperty(String plugIn, String property, Object value) {
		return getComponent().setPluginProperty(plugIn, property, value);
	}

	public void setRunAfterInsert(boolean runAfterInsert) {
		this.runAfterInsert = runAfterInsert;
	}

	// This was created with loading the changeset did not work. Now, should not be needed.
	// protected void batchLoadFiles() throws RepositoryException {
	// String files = (String) getComponent().getProperty("loadFiles");
	// if (files!=null) {
	// String[] fileList = files.split(",");
	// for (String s : fileList) {
	// batch(s);
	// }
	// }
	// }

	public void setStream(String name) {
		eventStream = session.getWorkingMemoryEntryPoint(name);
	}

	protected void startConsole() {
		consoleCommandReader = null; // end any prior thread
		getLog().debug("Repository console starting.");
		consoleCommandReader = new ConsoleCommandReader(this);
		// TODO If in OSGi, then run, else start new thread
		if (getComponent() != null) {
			consoleCommandReader.run();
		} else {
			consoleThread = new Thread(consoleCommandReader);
			consoleThread.start();
		}
	}

	protected void stopConsole() {
		consoleCommandReader.stop();
	}

	@Override
	public Object update(Object object) throws RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object update(Object object, boolean dynamic) throws RepositoryException {
		return update(object, false);
	}

}
