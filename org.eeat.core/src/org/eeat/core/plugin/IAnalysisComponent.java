package org.eeat.core.plugin;

public interface IAnalysisComponent extends IComponent, Comparable<IAnalysisComponent> {
	static public final String NOLOAD = "noLoad";
	static public final String LOADLEVEL = "loadLevel";
	static public final int DEFAULT_LOAD_LEVEL = 100;
	static public final Boolean DEFAULT_NOLOAD = false;
	static public final String LOADFILE = "loadFile";

	public String getLoadFile();

	public int getLoadLevel();

	public IRepository getRepository();

	public void load();

	public void reset();

	public void setLoadFile(String loadFile);

	public void setLoadLevel(int level);

	public void setRepository(IRepository repository);
}
