package org.eeat.knime.mongo.node;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.RowKey;
import org.knime.core.data.def.DefaultRow;
import org.knime.core.data.def.StringCell;
import org.knime.core.node.BufferedDataContainer;
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
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ReadPreference;
import com.mongodb.ServerAddress;
import com.mongodb.util.JSON;

/**
 * This is the model implementation of MongoNode. Node for reading from MongoNode
 * 
 * @author
 */
public class MongoNodeModel extends NodeModel {

	private static final NodeLogger logger = NodeLogger.getLogger(MongoNodeModel.class);

	// TODO: Remove custom (simpler?) query format:
	static final String CFG_MONGO_QUERY_AND = "queryAnd";
	protected final SettingsModelBoolean queryAnd = new SettingsModelBoolean(CFG_MONGO_QUERY_AND, true);
	// Now the format defaults to the simpler query format
	static final String CFG_MONGO_QUERY_FORMAT = "mongoQueryFormat";
	protected final SettingsModelBoolean mongoQueryFormat = new SettingsModelBoolean(CFG_MONGO_QUERY_FORMAT,
			false);

	// Parameters from GUI
	static final String CFG_ID = "ID";
	protected final SettingsModelString mongoID = new SettingsModelString(CFG_ID, "admin");
	static final String CFG_PASSWORD = "Password";
	protected final SettingsModelString mongoPassword = new SettingsModelString(CFG_PASSWORD, "1234");
	static final String CFG_HOST = "Host";
	protected final SettingsModelString mongoHost = new SettingsModelString(CFG_HOST, "localhost");
	static final String CFG_PORT = "Port";
	protected final SettingsModelInteger mongoPort = new SettingsModelInteger(CFG_PORT, 27017);
	static final String CFG_MONGO_COLL = "mongoColl";
	protected final SettingsModelString mongoColl = new SettingsModelString(CFG_MONGO_COLL, null);
	static final String CFG_USER_QUERY = "userQuery";
	protected final SettingsModelString userQuery = new SettingsModelString(CFG_USER_QUERY, null);
	static final String CFG_MONGO_DB = "mongoDB";
	protected final SettingsModelString mongoDB = new SettingsModelString(CFG_MONGO_DB, "localhost");
	static final String CFG_MONGO_LIMIT = "mongoLimit";
	protected final SettingsModelInteger mongoLimit = new SettingsModelInteger(CFG_MONGO_LIMIT, 0);
	static final String CFG_MONGO_BATCH = "mongoBatch";
	protected final SettingsModelInteger mongoBatch = new SettingsModelInteger(CFG_MONGO_BATCH, 100);
	static final String CFG_MONGO_SECONDARY = "secondary";
	protected final SettingsModelBoolean secondaryPreferred = new SettingsModelBoolean(CFG_MONGO_SECONDARY,
			true);
	static final String CFG_MONGO_INCREMENTAL = "incrementalOutput";
	protected final SettingsModelBoolean mongoIncremental = new SettingsModelBoolean(CFG_MONGO_INCREMENTAL,
			true);
	static final String CFG_MONGO_INCREMENT_SIZE = "incrementSize";
	protected final SettingsModelInteger mongoIncrementSize = new SettingsModelInteger(
			CFG_MONGO_INCREMENT_SIZE, 100);

	MongoClient mongoClient = null;
	DB db = null;
	int rowNumber = 0;
	DBCursor cursor = null;
	DataTableSpec outputSpec;
	boolean firstExecuteQuery = true;

	/**
	 * Constructor for the node model.
	 */
	protected MongoNodeModel() {
		super(0, 1);
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

	BasicDBObject createRegExBasicObject(final String v1, final String v2) {
		final Pattern p = Pattern.compile(v2);
		return new BasicDBObject(v1, p);
	}

	void establishDBConnection(final String databaseName) {
		if (db == null) {
			db = connectDB(mongoClient, databaseName, secondaryPreferred.getBooleanValue());
		}
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
	
	MongoClient newMongoClient(final String host, final int port, final String db) {
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

	void establishMongoClient() {
		if (mongoClient == null) {
			mongoClient=  newMongoClient(mongoHost.getStringValue(), mongoPort.getIntValue(), mongoDB.getStringValue());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected BufferedDataTable[] execute(final BufferedDataTable[] inData, final ExecutionContext exec)
			throws Exception {
		logger.info("Starting mongo execution.");
		logger.info("query is: " + userQuery.getStringValue() + " on " + mongoColl.getStringValue() + " in "
				+ mongoDB.getStringValue());

		firstExecuteQuery = true;
		RowKey key = null;
		BufferedDataTable out = null;
		BufferedDataContainer container = null;
		try {
			establishMongoClient();
			establishDBConnection(mongoDB.getStringValue());
			cursor = updateDBCursor();
			if (outputSpec == null) {
				outputSpec = specifyDataContainer(cursor.copy());
			}
			if (outputSpec == null) {
				logger.error("Query results in empty set.");
				throw new RuntimeException("Query result is empty"); // EARLY
																		// EXIT
			}

			// the execution context will provide us with storage capacity, in this
			// case a data container to which we will add rows sequentially
			// Note, this container can also handle arbitrary big data tables, it
			// will buffer to disc if necessary.
			container = exec.createDataContainer(outputSpec);

			while (moreData()) {
				final DBObject obj = cursor.next();
				key = new RowKey(rowNumber + "");
//				logger.debug(rowNumber + ": " + obj);
				final DataCell[] newcells = new DataCell[outputSpec.getNumColumns()];
				int j = 0;
				for (final String k : outputSpec.getColumnNames()) {
					if (obj.containsField(k)) {
						newcells[j++] = new StringCell(obj.get(k) != null ? obj.get(k).toString() : "");
					}
					// If the new row has more columns than the data spec, then stop adding columns
					// Possible because mongo records may not be the same length.
					if (j >= outputSpec.getNumColumns()) {
						break;
					}
				}
				// Fill the remaining with empty, when there are blank columns.
				for (int idx = j; idx < outputSpec.getNumColumns(); idx++) {
					newcells[idx] = new StringCell("");
				}
				container.addRowToTable(new DefaultRow(key, newcells));

				// check if the execution monitor was canceled
				exec.checkCanceled();
				exec.setProgress(rowNumber / (double) cursor.size(), "Adding row " + rowNumber);

				// next
				rowNumber = rowNumber + 1;
			}
		} catch (final Exception e) {
			logger.error(e.toString());
			e.printStackTrace();

		} finally {
			// once we are done, we close the container and return its table
			if (container != null) {
				container.close();
				out = container.getTable();
			}
		}
		return new BufferedDataTable[] { out };
	}

	private void initVars() {
		mongoClient = null;
		db = null;
		rowNumber = 0;
		cursor = null;
		outputSpec = null;
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
		// TODO load (valid) settings from the config object.
		// It can be safely assumed that the settings are valided by the
		// method below.
		userQuery.loadSettingsFrom(settings);
		mongoDB.loadSettingsFrom(settings);
		mongoColl.loadSettingsFrom(settings);
		mongoLimit.loadSettingsFrom(settings);
		queryAnd.loadSettingsFrom(settings);
		secondaryPreferred.loadSettingsFrom(settings);
		mongoBatch.loadSettingsFrom(settings);
		mongoIncremental.loadSettingsFrom(settings);
		mongoIncrementSize.loadSettingsFrom(settings);
		mongoQueryFormat.loadSettingsFrom(settings);
		
		mongoHost.loadSettingsFrom(settings);
		mongoPort.loadSettingsFrom(settings);
		mongoDB.loadSettingsFrom(settings);
		mongoID.loadSettingsFrom(settings);
		mongoPassword.loadSettingsFrom(settings);
	}

	boolean moreData() {
		boolean mData;
		if (firstExecuteQuery || !mongoIncremental.getBooleanValue()) {
			mData = cursor.hasNext();
		} else {
			// If incremental, then stop when reach mongoIncrementSize
			// Next, execute will restart here.
			// firstExecuteQuery ensure that it will get at least one record
			mData = cursor.hasNext() && ((rowNumber % mongoIncrementSize.getIntValue()) != 0);
		}
		firstExecuteQuery = false;
		return mData;
	}

	BasicDBObject parseQuery(final String query, final boolean mongoFormat) {
		if (mongoFormat) {
			final BasicDBObject qo = (BasicDBObject) JSON.parse(query);
			logger.debug(String.format("Text %s as query %s", query, qo));
			return qo;
		} else {
			return parseQuerySimple(query);
		}
	}

	// query:: COND ; COND ; ...
	// cond:: ATT , VALUE
	// VALUE:: att, value || att , op , value
	// value is considered regular expression if op matches: $regex
	// parenthesis indicates a subdocument
	// http://docs.mongodb.org/manual/tutorial/query-documents/
	// See http://docs.mongodb.org/manual/reference/operator/query/
	BasicDBObject parseQuerySimple(final String query) {
		// logger.debug("parsing: " + query);
		final ArrayList<BasicDBObject> opList = new ArrayList<BasicDBObject>();
		BasicDBObject result = null;
		for (final String cond : Arrays.asList(query.split(";"))) {
			// TODO: Allow split on ":" to address subdocument.
			final List<String> atts = Arrays.asList(cond.split(","));
			if (atts.size() == 3) {
				if (atts.get(1).equals("$regex")) {
					result = createRegExBasicObject(atts.get(0), atts.get(2));
				} else {
					result = new BasicDBObject(atts.get(0), createBasicObject(atts.get(1), atts.get(2)));
				}
			} else if (atts.size() == 2) {
				// Parenthesis TODO: not working
				// For now, use the dot notation for subdocuments, e.g.,
				// doc.subatt, value
				// if (atts.get(1).contains("(") && atts.get(1).contains(")")) {
				// String v = atts.get(1);
				// v= v.substring(v.indexOf("(") + 1, v.lastIndexOf(")") );
				// result = new BasicDBObject(atts.get(0),
				// parseQuery(v.replaceAll(":", ",")));
				// }
				// else {
				result = createBasicObject(atts.get(0), atts.get(1));
				// }
			}
			opList.add(result);
		}
		if (opList.size() > 1) {
			String op;
			op = queryAnd.getBooleanValue() ? "$and" : "$or";
			result = new BasicDBObject(op, opList);
		}
		logger.info("Parsed conditions: " + result);
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void reset() {
		// TODO Code executed on reset.
		// Models build during execute are cleared here.
		// Also data handled in load/saveInternals will be erased here.
		if (cursor != null) {
			cursor.close();
		}
		if (mongoClient != null) {
			mongoClient.close();
		}
		initVars();
	}

	DBCursor runQuery() {
		// Run query
		logger.info("Accessing mongo collection: " + mongoColl.getStringValue());
		final DBCollection coll = db.getCollection(mongoColl.getStringValue());
		DBCursor cursor;
		final int batchSize = 100;
		logger.info("Running mongo query: " + userQuery.getStringValue());
		logger.info("... and query limit: " + mongoLimit.getIntValue());
		if (userQuery.getStringValue().length() > 0) {
			if (mongoLimit.getIntValue() > 0) {
				cursor = coll.find(parseQuery(userQuery.getStringValue(),mongoQueryFormat.getBooleanValue())).limit(mongoLimit.getIntValue());
			} else {
				cursor = coll.find(parseQuery(userQuery.getStringValue(),mongoQueryFormat.getBooleanValue())).batchSize(batchSize);
			}
		} else {
			if (mongoLimit.getIntValue() > 0) {
				cursor = coll.find().limit(mongoLimit.getIntValue());
			} else {
				cursor = coll.find().batchSize(batchSize);
			}
		}
		logger.info("Result size: " + cursor.size());
		return cursor;
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

		// TODO save user settings to the config object.

		userQuery.saveSettingsTo(settings);
		mongoDB.saveSettingsTo(settings);
		mongoColl.saveSettingsTo(settings);
		mongoLimit.saveSettingsTo(settings);
		queryAnd.saveSettingsTo(settings);
		secondaryPreferred.saveSettingsTo(settings);
		mongoBatch.saveSettingsTo(settings);
		mongoIncremental.saveSettingsTo(settings);
		mongoIncrementSize.saveSettingsTo(settings);
		mongoQueryFormat.saveSettingsTo(settings);
		
		mongoHost.saveSettingsTo(settings);
		mongoPort.saveSettingsTo(settings);
		mongoDB.saveSettingsTo(settings);
		mongoID.saveSettingsTo(settings);
		mongoPassword.saveSettingsTo(settings);

	}

	DataTableSpec specifyDataContainer(final DBCursor cursor) {
		DataTableSpec outputSpec = null;
		int skips = 0;
		if (cursor.size() > 2) {
			skips = cursor.size() / 2;
			// Skip over some records, in an attempt to get to core (common)
			// records.
			// Such records ideally have most columns.
			// (Assumes first records may be odd, not having all columns.)
			cursor.skip(skips);
		}
		if (cursor.hasNext()) {
			final DBObject obj = cursor.next();
			final int columnNumber = obj.keySet().size();
			// Create the data spec
			final DataColumnSpec[] allColSpecs = new DataColumnSpec[columnNumber];
			int k = 0;
			for (final String name : obj.keySet()) {
				allColSpecs[k++] = new DataColumnSpecCreator(name.toString(), StringCell.TYPE).createSpec();
			}
			outputSpec = new DataTableSpec(allColSpecs);
		} else {
			logger.error("Cursor has 0 records.");
		}
		return outputSpec;
	}

	DBCursor updateDBCursor() {
		if ((cursor == null) || !mongoIncremental.getBooleanValue()) {
			cursor = runQuery();
		}
		return cursor;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void validateSettings(final NodeSettingsRO settings) throws InvalidSettingsException {

		// TODO check if the settings could be applied to our model
		// e.g. if the count is in a certain range (which is ensured by the
		// SettingsModel).
		// Do not actually set any values of any member variables.

		userQuery.validateSettings(settings);
		mongoDB.validateSettings(settings);
		mongoColl.validateSettings(settings);
		mongoLimit.validateSettings(settings);
		queryAnd.validateSettings(settings);
		secondaryPreferred.validateSettings(settings);
		mongoBatch.validateSettings(settings);
		mongoIncremental.validateSettings(settings);
		mongoIncrementSize.validateSettings(settings);
		mongoQueryFormat.validateSettings(settings);
		
		mongoHost.validateSettings(settings);
		mongoPort.validateSettings(settings);
		mongoDB.validateSettings(settings);
		mongoID.validateSettings(settings);
		mongoPassword.validateSettings(settings);

	}

}
