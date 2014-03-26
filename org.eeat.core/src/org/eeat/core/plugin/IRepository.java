package org.eeat.core.plugin;

import java.io.PrintWriter;
import org.hibernate.Session;
import java.io.Reader;
import java.util.Collection;
import java.util.List;


public interface IRepository {
	public enum QueryType {
		Jess, SQL, HQL
	}

	public void assertObject(Object object, boolean dynamic);

	@SuppressWarnings("rawtypes")
	public void assertTemplate(String ModuleName, Class aClass, String parent)
			throws RepositoryException;

	public void batch(String commandFile) throws RepositoryException;

	public void batch(String commandFile, ClassLoader classLoader) throws RepositoryException;

	public void clearSession() throws RepositoryException;

	public void close() throws RepositoryException;

	public void closeSession() throws RepositoryException;

	public void commitTransaction() throws RepositoryException;

	public void console(boolean doPrompt, PrintWriter writer, Reader reader)
			throws RepositoryException;

	public int countQueryResults(String query, Integer value) throws RepositoryException;

	public int countQueryResults(String query, Object... args) throws RepositoryException;

	public int countQueryResults(String query, String name, Integer value)
			throws RepositoryException;

	public Object delete(Object object) throws RepositoryException;

	public Object execute(String command) throws RepositoryException;

	public Collection findByExample(Object object) throws RepositoryException;

	public Object getEngine();

	public int getIdRBS();

	public Object getPluginProperty(String plugIn, String property);

	public List<IAnalysisComponent> getPlugins();

	public void halt() throws RepositoryException;

	public Object insert(Object object) throws RepositoryException;

	public Object insert(Object object, boolean dynamic) throws RepositoryException;

	public boolean isRunAfterInsert();

	public Object makePersistent(Object object) throws RepositoryException;

	public void makeTransient(Object object) throws RepositoryException;

	public void notify(Object object);

	public List<Object> query(String query, QueryType type, Object... args)
			throws RepositoryException;

	public void reset() throws RepositoryException;

	public void run() throws RepositoryException;

	public Object setPluginProperty(String plugIn, String property, Object value);

	public void setRunAfterInsert(boolean runAfterInsert);

	public void setStream(String name);

	public Object update(Object object) throws RepositoryException;

	public Object update(Object object, boolean dynamic) throws RepositoryException;

	Session getSession() throws RepositoryException;

}
