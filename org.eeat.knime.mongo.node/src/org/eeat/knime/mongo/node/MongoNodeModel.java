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
import org.knime.core.data.DataRow;
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
import com.mongodb.ReadPreference;

/**
 * This is the model implementation of BigQuery. Node for reading from bigquery
 * 
 * @author
 */
public class MongoNodeModel extends NodeModel {

	// the logger instance
	private static final NodeLogger logger = NodeLogger.getLogger(MongoNodeModel.class);
	
	static final String CFG_USER_QUERY = "userQuery";
	static protected final SettingsModelString userQuery = new SettingsModelString(
			CFG_USER_QUERY, null);
	static final String CFG_MONGO_DB = "mongoDB";
	static protected final SettingsModelString mongoDB = new SettingsModelString(
			CFG_MONGO_DB, null);
	static final String CFG_MONGO_COLL = "mongoColl";
	static protected final SettingsModelString mongoColl = new SettingsModelString(
			CFG_MONGO_COLL, null);
	static final String CFG_MONGO_LIMIT = "mongoLimit";
	static protected final SettingsModelInteger mongoLimit = new SettingsModelInteger(
			CFG_MONGO_LIMIT, 100);
	static final String CFG_MONGO_QUERY_AND = "queryAnd";
	static protected final SettingsModelBoolean queryAnd = new SettingsModelBoolean(
			CFG_MONGO_QUERY_AND, true);
	static final String CFG_MONGO_READONLY = "readOnly";
	static protected final SettingsModelBoolean readOnly = new SettingsModelBoolean(
			CFG_MONGO_READONLY, true);

	

	/**
	 * the settings key which is used to retrieve and store the settings (from the dialog or from a
	 * settings file) (package visibility to be usable from the dialog).
	 */
	static final String CFGKEY_COUNT = "Count";

	/** initial default count value. */
	static final int DEFAULT_COUNT = 100;


	MongoClient mongoClient = null;
	DB db = null;
	
	String dbName = "github";
	String collectionName = "users";
	String conditions = "";
	int queryLimit = 100;

	/**
	 * Constructor for the node model.
	 */
	protected MongoNodeModel() {

		// TODO 0 incoming port and one outgoing port is assumed
		super(0, 1);
//		super(new PortType[] {  }, new PortType[] { BufferedDataTable.TYPE });
//		super(new PortType[] { BufferedDataTable.TYPE }, new PortType[] { BufferedDataTable.TYPE });

	}

	void createMongoClient() {
		logger.info("Opening mongo client.");
		try {
			// mongoClient = new MongoClient( "eeat.cis.gsu.edu" );
			// mongoClient = new MongoClient( "dutiht.st.ewi.tudelft.nl", 27017 );
//			mongoClient = new MongoClient( "130.161.159.246", 27017 );
			mongoClient = new MongoClient();
		} catch (Exception e) {
			logger.info("Cannot open mongo client.");
			logger.info(e.toString());
		}
	}

	void connectToDB(String databaseName) {
		logger.info("Connecting to mongo database: " + databaseName);
		db = mongoClient.getDB(databaseName);
		if (readOnly.getBooleanValue()) {
			db.setReadPreference(ReadPreference.primary());
		}
	}
		
	BasicDBObject createRegExBasicObject(String v1, String v2) {
		Pattern p = Pattern.compile(v2);
		return new BasicDBObject(v1, p);
	}

	BasicDBObject createBasicObject(String v1, String v2) {
		BasicDBObject result = null;
		try {
			int n = Integer.parseInt(v2);
			result = new BasicDBObject(v1, n);
		} catch (NumberFormatException e) {
			try {
				float f = Float.parseFloat(v2);
				result = new BasicDBObject(v1, f);
			} catch (NumberFormatException e2) {
				// Default: return string
				result = new BasicDBObject(v1, v2);
			}
		}
		return result;
	}
	
	// query:: COND ; COND ; ...
	// cond::  ATT , VALUE
	// VALUE::  att, value || att , op , value
	// value is considered regular expression if op matches: $regex
	// parenthesis indicates a subdocument
	// http://docs.mongodb.org/manual/tutorial/query-documents/
	// See http://docs.mongodb.org/manual/reference/operator/query/
	BasicDBObject parseQuery(String query) {
//		logger.debug("parsing: " + query);
		ArrayList<BasicDBObject> opList = new ArrayList<BasicDBObject>();
		BasicDBObject result = null;
		for (String cond : Arrays.asList(query.split(";"))) {
			// TODO: Allow split on ":" to address subdocument.
			List<String> atts = Arrays.asList(cond.split(","));
			if (atts.size() == 3) {
				if (atts.get(1).equals("$regex")) {
					result = createRegExBasicObject(atts.get(0), atts.get(2));
				} else {
					result = new BasicDBObject(atts.get(0), createBasicObject(atts.get(1),
							atts.get(2)));
				}
			} else if (atts.size() == 2) {
				// Parenthesis TODO: not working
				// For now, use the dot notation for subdocuments, e.g., doc.subatt, value
//				if (atts.get(1).contains("(") && atts.get(1).contains(")")) {
//					String v = atts.get(1);
//					v= v.substring(v.indexOf("(") + 1, v.lastIndexOf(")") );
//					result = new BasicDBObject(atts.get(0), parseQuery(v.replaceAll(":", ",")));
//				} 
//				else {
					result = createBasicObject(atts.get(0), atts.get(1));
//				}
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
	
	DBCursor runQuery() {
		// Run query
		logger.info("Accessing mongo collection: " + collectionName);
		DBCollection coll = db.getCollection(collectionName);
		DBCursor cursor;
		logger.info("Running mongo query: " + conditions);
		logger.info("... and query limit: " + queryLimit);
		if (conditions.length() > 0) {
			cursor = coll.find(parseQuery(conditions)).limit(queryLimit);
		} else {
			cursor = coll.find().limit(queryLimit);
		}
		logger.info("Result size: " + cursor.size());
		return cursor;
	}
	
	DataTableSpec specifyDataContainer(DBCursor cursor) {
		DataTableSpec outputSpec = null;
		int skips = 0;
		if (cursor.size()>2) {
			skips = cursor.size()/2;
			// Skip over half the records, in an attempt to get to core (common) records.
			// Such records ideally have most columns.\
			// (Assumes first records may be odd, not having all columns.)
			cursor.skip(skips);
		}
		if (cursor.hasNext()) {
			DBObject obj = cursor.next();
			int columnNumber = obj.keySet().size();
			// Create the data spec
			DataColumnSpec[] allColSpecs = new DataColumnSpec[columnNumber];
			int k = 0;
			for (String name : obj.keySet()) {
				allColSpecs[k++] = new DataColumnSpecCreator(name.toString(), StringCell.TYPE)
						.createSpec();
			}
			outputSpec = new DataTableSpec(allColSpecs);
		} else {
			logger.error("Cursor has 0 records.");
		}
		return outputSpec;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected BufferedDataTable[] execute(final BufferedDataTable[] inData,
			final ExecutionContext exec) throws Exception {
		logger.info("Starting mongo execution.");

		if (userQuery.getStringValue().length() > 0) {
			conditions = userQuery.getStringValue();
		}
		if (mongoDB.getStringValue().length() > 0) {
			dbName = mongoDB.getStringValue();
		}
		if (mongoColl.getStringValue().length() > 0) {
			collectionName = mongoColl.getStringValue();
		}
		if (mongoLimit.getIntValue() > 0) {
			queryLimit = mongoLimit.getIntValue();
		}
		logger.info("query is: " + conditions);
	
		
		int i = 0;
		RowKey key = null;
		BufferedDataTable out = null;
		BufferedDataContainer container = null;
		DBCursor cursor = null;
		try {
			// /the main function
			createMongoClient();
			connectToDB(dbName);			
			DataTableSpec outputSpec = specifyDataContainer(runQuery());	
			if (outputSpec == null) {
				logger.error("Query results in empty set.");
				throw new RuntimeException("Query result is empty"); // EARLY EXIT
			}	
			
			// the execution context will provide us with storage capacity, in this
			// case a data container to which we will add rows sequentially
			// Note, this container can also handle arbitrary big data tables, it
			// will buffer to disc if necessary.
			container = exec.createDataContainer(outputSpec);	
									
			cursor = runQuery();
			while (cursor.hasNext()) {				
				DBObject obj = cursor.next();
				key = new RowKey(i+"");
				logger.debug(i + ": " + obj);
				DataCell[] newcells = new DataCell[outputSpec.getNumColumns()];
				int j = 0;
				for (String k : outputSpec.getColumnNames()) {
					if (obj.containsField(k)) { 
						newcells[j++] = new StringCell(obj.get(k) != null ? obj.get(k).toString() : "");
					}
					// If the new row has more columns than the data spec, then stop adding columns
					// Possible because mongo records may not be the same length.
					if (j>= outputSpec.getNumColumns()) break;
				}
				// Fill the remaining with empty, when there are blank columns.
				for (int idx = j; idx < outputSpec.getNumColumns(); idx++) {
					newcells[idx] = new StringCell("");
				}
				DataRow newrow = new DefaultRow(key, newcells);
				container.addRowToTable(newrow);

				// check if the execution monitor was canceled
				exec.checkCanceled();
				exec.setProgress(i / (double) cursor.size(), "Adding row " + i);

				// next
				i = i + 1;
			}
		} catch (Exception e) {
			logger.error(e.toString());
			e.printStackTrace();

		} finally {
			if (cursor != null)
				cursor.close();
			// once we are done, we close the container and return its table
			if (container != null) {
				container.close();
				out = container.getTable();
			}
		}
		return new BufferedDataTable[] { out };
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
	protected DataTableSpec[] configure(final DataTableSpec[] inSpecs)
			throws InvalidSettingsException {

		// TODO: check if user settings are available, fit to the incoming
		// table structure, and the incoming types are feasible for the node
		// to execute. If the node can execute in its current state return
		// the spec of its output data table(s) (if you can, otherwise an array
		// with null elements), or throw an exception with a useful user message

		return new DataTableSpec[] { null };
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
		readOnly.saveSettingsTo(settings);

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void loadValidatedSettingsFrom(final NodeSettingsRO settings)
			throws InvalidSettingsException {

		// TODO load (valid) settings from the config object.
		// It can be safely assumed that the settings are valided by the
		// method below.

		userQuery.loadSettingsFrom(settings);
		mongoDB.loadSettingsFrom(settings);
		mongoColl.loadSettingsFrom(settings);
		mongoLimit.loadSettingsFrom(settings);
		queryAnd.loadSettingsFrom(settings);
		readOnly.loadSettingsFrom(settings);


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
		readOnly.validateSettings(settings);

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void loadInternals(final File internDir, final ExecutionMonitor exec)
			throws IOException, CanceledExecutionException {

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
	protected void saveInternals(final File internDir, final ExecutionMonitor exec)
			throws IOException, CanceledExecutionException {

		// TODO save internal models.
		// Everything written to output ports is saved automatically (data
		// returned by the execute method, models saved in the saveModelContent,
		// and user settings saved through saveSettingsTo - is all taken care
		// of). Save here only the other internals that need to be preserved
		// (e.g. data used by the views).

	}

}
