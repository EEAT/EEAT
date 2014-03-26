package org.eeat.repository.drools;

import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eeat.core.plugin.IRepositoryComponent;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Activator implements BundleActivator, IApplication {
	static protected BundleContext bundleContext;
	private static Logger logger;

	protected IRepositoryComponent getRepositoryComponent(BundleContext context) {
		ServiceTracker tracker = new ServiceTracker(context, IRepositoryComponent.class.getName(), null);		
		return (IRepositoryComponent) tracker.getService();
	}

	protected void registerRepositoryComponent(BundleContext context) throws InvalidSyntaxException {
		RepositoryComponent repository = new RepositoryComponent();
		context.registerService(IRepositoryComponent.class.getName(), repository, null);
	}
	
	protected Logger getLogger() {
		return logger != null ? logger :  LoggerFactory.getLogger(Activator.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext
	 * )
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		org.slf4j.impl.OSGILogFactory.initOSGI(context);
		// TODO referencing logger before initialized in Component causes warnings
		getLogger().debug("Starting bundle: " + this);
		bundleContext = context;
		// Manually register, rather than using declarative services (until DS bug fix)
		// TODO fix DS
//        registerRepositoryComponent(context);
	}

	@Override
	public Object start(IApplicationContext context) throws Exception {
		// TODO referencing logger before initialized in Component causes warnings
//		getLogger().debug("Starting application: " + this);
		try {
			startServiceConsole();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			getLogger().error(e.toString());
		}
		
//		try {
//		Repository repository = new Repository();
//		repository.batch("testLTL.ocl.drl");
////		repository.batch("property.drl");
//		StatefulKnowledgeSession session = (StatefulKnowledgeSession) repository.getEngine();
//		}
//		catch (Exception ex) {
//			getLogger().error(ex.toString());
//			ex.printStackTrace();
//		}
//		getLogger().debug("Rule systems successfuly loaded: " + this);
		return IApplication.EXIT_OK;
	}

	protected void startServiceConsole() {
		// Wait until bundle activation completes...
		int count = 0;
		while (RepositoryComponent.getInstance() == null && count++ < 10) {
			try {
				Thread.currentThread();
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
				getLogger().error(e.toString());
			}
		}
		RepositoryComponent repo = RepositoryComponent.getInstance();
		if (repo == null) {
			getLogger().error("RepositoryComponent not created by dynamic services: " + this);
			repo = new RepositoryComponent();
		}
		repo.run();
	}

	@Override
	public void stop() {
		getLogger().debug("Stopping application: " + this);
		RepositoryComponent.instance = null;
	}

	@Override
	public void stop(BundleContext arg0) throws Exception {
		getLogger().debug("Stopping bundle: " + this);
	}

}
