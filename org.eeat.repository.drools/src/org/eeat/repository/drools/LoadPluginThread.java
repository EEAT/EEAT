package org.eeat.repository.drools;

public class LoadPluginThread extends Thread {
	RepositoryComponent component;
	long sleepPeriod;
	boolean load = true;

	public LoadPluginThread(RepositoryComponent component, long sleepPeriod) {
		this.component = component;
		this.sleepPeriod = sleepPeriod;
	}

	public boolean isLoad() {
		return load;
	}

	@Override
	public void run() {
		try {
			Thread.currentThread();
			Thread.sleep(sleepPeriod);
			if (isLoad()) {
				component.loadPlugins();
			}
		} catch (InterruptedException e) {
			component.getLog().error(e.toString());
			e.printStackTrace();
		}
	}

	public void setLoad(boolean load) {
		this.load = load;
	}

}
