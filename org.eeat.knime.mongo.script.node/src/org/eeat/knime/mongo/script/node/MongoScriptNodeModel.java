package org.eeat.knime.mongo.script.node;

import java.nio.charset.Charset;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bson.BSONException;
import org.knime.core.data.DataTableSpec;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.defaultnodesettings.SettingsModelBoolean;
import org.knime.core.node.defaultnodesettings.SettingsModelInteger;
import org.knime.core.node.defaultnodesettings.SettingsModelString;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCursor;
import com.mongodb.DBEncoder;
import com.mongodb.DBObject;
import com.mongodb.DefaultDBEncoder;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.MongoException.CursorNotFound;
import com.mongodb.ReadPreference;
import com.mongodb.ServerAddress;
import com.mongodb.WriteConcern;
import com.mongodb.util.JSON;

/**
 * This is the model implementation of BigQuery. Node for reading from bigquery
 * 
 * @author
 */
public class MongoScriptNodeModel extends NodeModel {

	private static final NodeLogger logger = NodeLogger.getLogger(MongoScriptNodeModel.class);

	
	static final String CFG_MONGO_COPY = "opCopy";
	protected final SettingsModelBoolean opCopy = new SettingsModelBoolean(CFG_MONGO_COPY, false);
	static final String CFG_MONGO_NOOP = "noOp";
	protected final SettingsModelBoolean noOp = new SettingsModelBoolean(CFG_MONGO_NOOP, false);

	static final String CFG_ID = "ID";
	protected final SettingsModelString mongoID = new SettingsModelString(CFG_ID, "admin");
	static final String CFG_PASSWORD = "Password";
	protected final SettingsModelString mongoPassword = new SettingsModelString(CFG_PASSWORD, "1234");
	static final String CFG_HOST1 = "Host1";
	protected final SettingsModelString host1 = new SettingsModelString(CFG_HOST1, "localhost");
	static final String CFG_PORT1 = "Port1";
	protected final SettingsModelInteger port1 = new SettingsModelInteger(CFG_PORT1, 27017);
	static final String CFG_DB1 = "DB1";
	protected final SettingsModelString dB1 = new SettingsModelString(CFG_DB1, "test");
	static final String CFG_SCRIPT = "Script";
	protected final SettingsModelString script = new SettingsModelString(CFG_SCRIPT, "db.serverStatus();");
	static final String CFG_MONGO_COLL = "collectionName";
	protected final SettingsModelString collectionName = new SettingsModelString(CFG_MONGO_COLL, "");

	static final String CFG_HOST2 = "Host2";
	protected final SettingsModelString host2 = new SettingsModelString(CFG_HOST2, "localhost");
	static final String CFG_PORT2 = "Port2";
	protected final SettingsModelInteger port2 = new SettingsModelInteger(CFG_PORT2, 27018);
	static final String CFG_DB2 = "DB2";
	protected final SettingsModelString dB2 = new SettingsModelString(CFG_DB2, "test");

	/**
	 * Constructor for the node model.
	 */
	protected MongoScriptNodeModel() {
		super(0, 0);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected DataTableSpec[] configure(final DataTableSpec[] inSpecs) throws InvalidSettingsException {

		// TODO: check if user settings are available, fit to the incoming
		// table structure, and the incoming types are feasible for the node
		// to execute. If the node can execute in its current state return
		// the spec of its output data table(s) (if you can, otherwise an array
		// with null elements), or throw an exception with a useful user message

		return new DataTableSpec[] { null };
	}

	DB connectDB(final MongoClient client, final String databaseName, final boolean secondaryPreferred) {
		DB db;
		final String host = client.getAddress().getHost();
		final int port = client.getAddress().getPort();
		logger.info(String.format("Connecting to mongo database: %s on host %s at port %s", databaseName,
				host, port));
		db = client.getDB(databaseName);
		if (secondaryPreferred) {
			db.setReadPreference(ReadPreference.secondaryPreferred());
			logger.debug("Secondary preferred on database: " + databaseName);
		} else {
			db.setReadPreference(ReadPreference.primaryPreferred());
		}
		return db;
	}

	BasicDBObject createBasicObject(final String v1, final String v2) {
		BasicDBObject result = null;
		try {
			final int n = Integer.parseInt(v2);
			result = new BasicDBObject(v1, n);
		} catch (final NumberFormatException e) {
			try {
				final float f = Float.parseFloat(v2);
				result = new BasicDBObject(v1, f);
			} catch (final NumberFormatException e2) {
				// Default: return string
				result = new BasicDBObject(v1, v2);
			}
		}
		return result;
	}
	
	List<DBObject> ensureUTF8(List<DBObject> objects) {
		List<DBObject> result = new ArrayList<DBObject>();
		for (DBObject o : objects) {
			final DBObject qo = (DBObject) JSON.parse(stripControlChars(o.toString()));
			result.add(qo);
		}
		return result;
	}
	
	
	String stripControlChars(String s) {
		int length = s.length();
		char[] oldChars = new char[length];
		s.getChars(0, length, oldChars, 0);
		int newLen = 0;
		for (int j = 0; j < length; j++) {
		    char ch = oldChars[j];
		    if (ch >= ' ') {
		        oldChars[newLen] = ch;
		        newLen++;
		    }
		}
		return new String(oldChars, 0, newLen);
	}



	/**
	 * {@inheritDoc}
	 */
	@Override
	protected BufferedDataTable[] execute(final BufferedDataTable[] inData, final ExecutionContext exec)
			throws Exception {
		logger.debug("Starting mongo shell execution.");
		if (noOp.getBooleanValue()) {
			logger.warn("Mongodb copy: No Operation is true, so no execution.");
			return null; // EARLY EXIT
		}

		final MongoClient client = newMongoClient(host1.getStringValue(), port1.getIntValue(), dB1.getStringValue());
		// If copy, then read only for source db1
		final DB db = connectDB(client, dB1.getStringValue(), opCopy.getBooleanValue());
		// logger.info("Mongo execution result: " + db1.eval("db.serverStatus()")); // Test
		if (opCopy.getBooleanValue()) {
			final MongoClient client2 = newMongoClient(host2.getStringValue(), port2.getIntValue(), dB2.getStringValue());
			final DB db2 = connectDB(client2, dB2.getStringValue(), false);

			final BasicDBObject qo = (BasicDBObject) JSON.parse(script.getStringValue());
			logger.debug(String.format("Text %s as query %s", script.getStringValue(), qo));
			DBCursor cursor = db.getCollection(collectionName.getStringValue()).find(qo);
			final int OBJECT_BUFFER_SIZE = 4000;
			int rowNumber = 0;
			List<DBObject> objects;
			final int totalRows = cursor.size();
			try {
				// Loop design based on this:
				// http://stackoverflow.com/questions/18525348/better-way-to-move-mongodb-collection-to-another-collection/20889762#20889762
				// Use multiple threads to improve
				do {
					logger.debug(String.format("%s processing rows %d - %d of %d for copy into %s",
							collectionName.getStringValue(), rowNumber, (rowNumber + OBJECT_BUFFER_SIZE) - 1,
							totalRows, dB2.getStringValue()));
					cursor = db.getCollection(collectionName.getStringValue()).find(qo)
							.sort(new BasicDBObject("$natural", 1)).skip(rowNumber).limit(OBJECT_BUFFER_SIZE);
					// Some data is messed up
					objects = ensureUTF8(cursor.toArray());
					try {
						if (objects.size() > 0) {
							db2.getCollection(collectionName.getStringValue()).insert(objects);
						}
					} catch (final BSONException e) {
						logger.warn(String.format(
								"Copy %s %s: mongodb error.The rows between %d - %d will be skipped.",
								dB1.getStringValue(), collectionName.getStringValue(), rowNumber, rowNumber
										+ OBJECT_BUFFER_SIZE));
						logger.error(e);
					}
					rowNumber = rowNumber + objects.size();
					// check if the execution monitor was canceled
					exec.checkCanceled();
					exec.setProgress(rowNumber / (double) totalRows, "Adding rows " + rowNumber);
				} while (rowNumber < totalRows);
			} catch (final CursorNotFound e) {
				logger.warn(String.format("Copy %s %s: mongodb fatal error. Ending at row %d.",
						dB1.getStringValue(), collectionName.getStringValue(), rowNumber));
				logger.error(e);
			}
		} else {
			logger.debug("Execution script: " + script.getStringValue());
			logger.info("Execution result: " + db.eval(script.getStringValue()));
		}
		// return new BufferedDataTable[] {};
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void loadInternals(final File internDir, final ExecutionMonitor exec) throws IOException,
			CanceledExecutionException {

		// TODO load internal data.
		// Everything handed to output ports is loaded automatically (data
		// returned by the execute method, models loaded in loadModelContent,
		// and user settings set through loadSettingsFrom - is all taken care
		// of). Load here only the other internals that need to be restored
		// (e.g. data used by the views).

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void loadValidatedSettingsFrom(final NodeSettingsRO settings) throws InvalidSettingsException {
		// It can be safely assumed that the settings are valided by the
		// method below.
		host1.loadSettingsFrom(settings);
		port1.loadSettingsFrom(settings);
		dB1.loadSettingsFrom(settings);
		script.loadSettingsFrom(settings);
		collectionName.loadSettingsFrom(settings);

		host2.loadSettingsFrom(settings);
		port2.loadSettingsFrom(settings);
		dB2.loadSettingsFrom(settings);
		
		mongoID.loadSettingsFrom(settings);
		mongoPassword.loadSettingsFrom(settings);

		opCopy.loadSettingsFrom(settings);
		noOp.loadSettingsFrom(settings);
	}

	MongoClient newMongoClient(final String host,  final int port, final String db) {
		logger.debug(String.format("Opening mongo client on %s : %s for ID %s.", host, port, mongoID.getStringValue()));
		MongoClient mongoClient = null;
		MongoCredential credential = null;
		if (mongoID.getStringValue().length() > 0) {
			credential = MongoCredential.createMongoCRCredential(mongoID.getStringValue(),
					db, mongoPassword.getStringValue().toCharArray());
		}
		final MongoClientOptions options = MongoClientOptions.builder().autoConnectRetry(true).build();
		try {
			if (credential != null) {
				mongoClient = new MongoClient(new ServerAddress(host,
						port), Arrays.asList(credential), options);

			} else {
				mongoClient = new MongoClient(new ServerAddress(host,
						port), options);
			}
		} catch (final Exception e) {
			logger.info("Cannot open mongo client.");
			logger.info(e.toString());
		}
		return mongoClient;
	}
			

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void reset() {
		// TODO Code executed on reset.
		// Models build during execute are cleared here.
		// Also data handled in load/saveInternals will be erased here.

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void saveInternals(final File internDir, final ExecutionMonitor exec) throws IOException,
			CanceledExecutionException {

		// TODO save internal models.
		// Everything written to output ports is saved automatically (data
		// returned by the execute method, models saved in the saveModelContent,
		// and user settings saved through saveSettingsTo - is all taken care
		// of). Save here only the other internals that need to be preserved
		// (e.g. data used by the views).

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void saveSettingsTo(final NodeSettingsWO settings) {
		host1.saveSettingsTo(settings);
		port1.saveSettingsTo(settings);
		dB1.saveSettingsTo(settings);
		script.saveSettingsTo(settings);
		collectionName.saveSettingsTo(settings);

		host2.saveSettingsTo(settings);
		port2.saveSettingsTo(settings);
		dB2.saveSettingsTo(settings);
		
		mongoID.saveSettingsTo(settings);
		mongoPassword.saveSettingsTo(settings);

		opCopy.saveSettingsTo(settings);
		noOp.saveSettingsTo(settings);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void validateSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
		// Do not actually set any values of any member variables.
		host1.validateSettings(settings);
		port1.validateSettings(settings);
		dB1.validateSettings(settings);
		script.validateSettings(settings);
		collectionName.validateSettings(settings);

		host2.validateSettings(settings);
		port2.validateSettings(settings);
		dB2.validateSettings(settings);

		mongoID.validateSettings(settings);
		mongoPassword.validateSettings(settings);
		
		opCopy.validateSettings(settings);
		noOp.validateSettings(settings);
	}

}
