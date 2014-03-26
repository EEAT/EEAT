package org.eeat.core.plugin;

public abstract class AnalysisComponent extends Component implements IAnalysisComponent {
	IRepository repository;
	int loadLevel = -1;
	Boolean noLoad;

	String loadFile;

	static public final String LOADFILE = "loadFile";

	@Override
	public int compareTo(IAnalysisComponent plugin) {
		if (this.getLoadLevel() < plugin.getLoadLevel()) {
			return -1;
		}
		if (this.getLoadLevel() > plugin.getLoadLevel()) {
			return 1;
		}
		return this.toString().compareTo(plugin.toString());
	}

	@Override
	public String getLoadFile() {
		return loadFile;
	}

	@Override
	public int getLoadLevel() {
		String value = (String) getProperty(LOADLEVEL);
		if (loadLevel < 0) {
			try {
				setLoadLevel(Integer.parseInt(value));
			} catch (NumberFormatException e) {
				getLog().error(
						String.format("Improper format for bundle property (%s=%s) in %s.",
								LOADLEVEL, value, this));
				loadLevel = DEFAULT_LOAD_LEVEL;
			}
		}
		return loadLevel;
	}

	@Override
	public IRepository getRepository() {
		return repository;
	}

	@Override
	public void init() {
		super.init();
		setLoadFile((String) getProperty(LOADFILE));
		// Manually register, rather than using declarative services (until DS
		// bug fix)
		// TODO remove this when Declarative Services BUG of duplicate class
		// instantiation is fixed.
		// context.registerService(IJessComponent.class.getName(), this, null);
	}

	public boolean isNoLoad() {
		if (noLoad == null) {
			try {
				setNoLoad(Boolean.parseBoolean((String) getProperty(NOLOAD)));
			} catch (Exception e) {
				getLog().error(
						String.format("Improper format for bundle property (%s=%s) in %s.", NOLOAD,
								getProperty(NOLOAD), this));
				noLoad = DEFAULT_NOLOAD;
			}
		}
		return noLoad;
	}

	@Override
	public void load() {
		if (isNoLoad()) {
			getLog().warn("Because of 'no load', skipping load for " + this);
			return;
		}
		if (getLoadFile() == null) {
			getLog().warn("No repository load file for " + this);
			return;
		}
		if (getRepository() == null) {
			getLog().error("No repository in which to load for " + this);
			return;
		}
		getLog().debug(
				String.format("%s loading batch file %s into repository %s.", this, getLoadFile(),
						repository));
		try {
			repository.batch(getLoadFile());
		} catch (RepositoryException e) {
			getLog().debug(e.toString());
			e.printStackTrace();
		}
	}

	@Override
	public void reset() {
		getLog().debug("reseting " + this);
	}

	@Override
	public void setLoadFile(String loadFile) {
		this.loadFile = loadFile;
	}

	@Override
	public void setLoadLevel(int loadLevel) {
		this.loadLevel = loadLevel;
	}

	public void setNoLoad(boolean autoLoad) {
		this.noLoad = autoLoad;
	}

	@Override
	public void setRepository(IRepository repository) {
		this.repository = repository;
	}
}
