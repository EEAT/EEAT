package org.eeat.drools.rbs;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseConfiguration;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderConfiguration;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.conf.EventProcessingOption;
import org.drools.io.Resource;
import org.drools.io.ResourceFactory;
import org.drools.logger.KnowledgeRuntimeLogger;
import org.drools.logger.KnowledgeRuntimeLoggerFactory;
import org.drools.rule.EntryPoint;
import org.drools.runtime.KnowledgeSessionConfiguration;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.rule.WorkingMemoryEntryPoint;
import org.eeat.core.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO change logger.error to logger.warn|debug|etc as appropriate (after logback.xml if realized in KNIME).
public class Drools {
	private static Logger logger = LoggerFactory.getLogger(Drools.class);

	static public Properties loadPropertyResource(final String name) {
		Properties properties = null;
		final InputStream is = Drools.class.getResourceAsStream(name);
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

	KnowledgeBase knowledgeBase;
	StatefulKnowledgeSession session;
	WorkingMemoryEntryPoint eventStream;

	KnowledgeRuntimeLogger droolsLogger;
	public final String DROOLS_AUDIT = "DroolsAudit";
	public final String DROOLS_RESOURCE_FILE = "DroolsResourceFile";

	public final String DROOLS_VARIABLE_PARENT = "parent";
	// Hard coded, so does not depend on Eclipse properties lookup to start.
	// Used for minimal start
	final String resourceFolder = "/resources";

	final String loadFile = resourceFolder + "/repository/property.drl";

	private String droolsResourceFile;

	private String droolsAuditFile;

	public Drools() {
		initDroolsResourceFile();
		reset();
	}
	
	public Drools(String DroolsBatchFilePath) {
		setDroolsResourceFile(DroolsBatchFilePath);
		reset();
	}

	protected void addRuleResource(final String path) {
		final Properties kbuilderProperties = Constants.loadPropertyResource(resourceFolder
				+ "/kbuilder.properties");
		final KnowledgeBuilderConfiguration builderConf = KnowledgeBuilderFactory
				.newKnowledgeBuilderConfiguration(kbuilderProperties, null);
		final KnowledgeBuilder knowledgeBuilder = KnowledgeBuilderFactory
				.newKnowledgeBuilder(builderConf);
		logger.debug("Trying to add rule package: " + path);
		Resource resource;
		resource = ResourceFactory.newFileResource(path);
		try {
			resource.getInputStream();
		} catch (IOException e) {
			// Not found as a file resource
			logger.debug("Rule resource not found in file system: " + path);
			resource = null;
		}
		if (resource == null) {
			resource = ResourceFactory.newClassPathResource(path, this.getClass());
		}		
		try {
			if (path.endsWith("changeset.xml")) {
				logger.warn("Change-set being depcreated in Drools v6... trying to load Drools resource file: "
						+ resource.toString());
				knowledgeBuilder.add(resource, ResourceType.CHANGE_SET);
			} else {
				knowledgeBuilder.add(resource, ResourceType.DRL);
			}
			knowledgeBase.addKnowledgePackages(knowledgeBuilder.getKnowledgePackages());
			logger.info("Added rule package: " + path);
		} catch (RuntimeException e) {
			logger.error("While adding rule resource: '" + path + "' exception: " + e.toString());
		}		
		if (knowledgeBuilder.hasErrors()) {
			logger.error("Cannot load Drools resource file: " + resource.toString());
			throw new RuntimeException(knowledgeBuilder.getErrors().toString());
		}
	}
	
	

	public void close() {
		logger.debug("closing: " + this);
		this.closeAudit();
		session.halt();
		session.dispose();
	}

	public void closeAudit() {
		if (droolsLogger != null)
			droolsLogger.close();
	}

	public void execute() {
		logger.debug(String.format("Executing %s", this));
		// TODO run in separate thread?
		session.fireAllRules();
	}

	public String getDroolsAuditFile() {
		return droolsAuditFile;
	}

	public String getDroolsResourceFile() {
		return droolsResourceFile;
	}

	public Object getGlobalVariable(final String name) {
		return session.getGlobal(name);
	}

	public int getIdRBS() {
		return session.hashCode();
	}

	public Logger getLog() {
		return logger;
	}

	public StatefulKnowledgeSession getRuleSession() {
		return session;
	}
	
	public KnowledgeBase getRuleKnowledgeBase() {
		return knowledgeBase;
	}

	public void halt() {
		logger.debug(String.format("Stopping %s", this));
		session.halt();
	}

	protected KnowledgeRuntimeLogger initDroolsAuditFile(final StatefulKnowledgeSession session) {
		KnowledgeRuntimeLogger newLogger = null;
		final Properties bundleProperties = Constants.loadPropertyResource("/bundle.properties",
				this.getClass());
		String logFilename;

		logFilename = getDroolsAuditFile();
		if ((logFilename == null) || logFilename.isEmpty()) {
			logFilename = bundleProperties.getProperty(DROOLS_AUDIT);
			if ((logFilename == null) || logFilename.isEmpty()) {
				
				logFilename = System.getProperty("java.io.tmpdir") + DROOLS_AUDIT;
			}
		}
		if ((logFilename != null) && !logFilename.isEmpty()) {
			File logfile = new File(logFilename);
			if (logfile.canWrite()) {
				newLogger = KnowledgeRuntimeLoggerFactory.newFileLogger(session, logFilename);
				logger.info("Drools logging to file: " + logFilename);
			}
			else {
				logger.error("Cannot write to Drools logging to file: " + logFilename);
			}
		}
		return newLogger;
	}

	public void initDroolsResourceFile() {
		final Properties bundleProperties = Constants.loadPropertyResource("/bundle.properties",
				this.getClass());
		final String batchFilename = bundleProperties.getProperty(DROOLS_RESOURCE_FILE);

		if (batchFilename != null) {
			setDroolsResourceFile(batchFilename);
		} else {
			logger.warn("No resource file specified in bundle.properties");
		}
	}

	protected void initKB() {
		Properties kbaseProperties = Constants.loadPropertyResource(resourceFolder
				+ "/kbase.properties", this.getClass());
		// If properties not found, the load default...
		if (kbaseProperties == null) {
			kbaseProperties = Constants.loadPropertyResource(resourceFolder
					+ "/kbase-default.properties", this.getClass());
			logger.info("Loading default: " + resourceFolder + "/kbase-default.properties");
		}
		final KnowledgeBaseConfiguration config = KnowledgeBaseFactory
				.newKnowledgeBaseConfiguration(kbaseProperties, null);
		// Stream option should be set in property file
		config.setOption(EventProcessingOption.STREAM);
		knowledgeBase = KnowledgeBaseFactory.newKnowledgeBase(config);
	}

	protected void initSession() {
		Properties sessionProperties = Constants.loadPropertyResource(resourceFolder
				+ "/session.properties", this.getClass());
		// If properties not found, the load default...
		if (sessionProperties == null) {
			sessionProperties = Constants.loadPropertyResource(resourceFolder
					+ "/session-default.properties", this.getClass());
			logger.info("Loading default: " + resourceFolder + "/session-default.properties");
		}
		final KnowledgeSessionConfiguration conf = KnowledgeBaseFactory
				.newKnowledgeSessionConfiguration(sessionProperties);
		session = knowledgeBase.newStatefulKnowledgeSession(conf, null);
		droolsLogger = initDroolsAuditFile(session);
	}

	public Object insert(final Object object)  {
		logger.debug("Inserting: " + object);
		session.insert(object);
		return object;
	}

	public void reset() {
		logger.debug(String.format("Resetting %s", this));
		initKB();
		initSession();
		eventStream = session.getWorkingMemoryEntryPoint(EntryPoint.DEFAULT.getEntryPointId());
		final String batchPath = getDroolsResourceFile();
		if (batchPath != null) {
			addRuleResource(batchPath);
		}
		try {
			// Globals set only after their specification in the rules...
			session.setGlobal(DROOLS_VARIABLE_PARENT, this);
		} catch (final Exception e) {
			logger.error("Initialization error: " + e.toString());
		}
	}

	public void setDroolsAuditFile(final String droolsAuditFile) {
		this.droolsAuditFile = droolsAuditFile;
	}

	public void setDroolsResourceFile(final String droolsResourceFile) {
		this.droolsResourceFile = droolsResourceFile;
	}

	public void setGlobalVariable(final String name, final Object value) {
		session.setGlobal(name, value);
	}

}
