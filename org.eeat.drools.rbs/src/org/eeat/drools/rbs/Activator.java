package org.eeat.drools.rbs;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Activator implements BundleActivator {

	private static Logger logger;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext )
	 */
	@Override
	public void start(final BundleContext context) throws Exception {
		org.slf4j.impl.OSGILogFactory.initOSGI(context);
		logger = LoggerFactory.getLogger(Activator.class);
		logger.debug(String.format("%s is started", this));
//		testInit();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(final BundleContext context) throws Exception {
		logger.debug(String.format("%s is stopped", this));
	}
	
	public void testInit() throws Exception {
		Drools drools = new Drools();
    	drools.setGlobalVariable("knimeInputData", null);
    	drools.setGlobalVariable("knimeOutputData", null);
    	drools.execute();
	}

}