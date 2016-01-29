package org.eeat.knime.drools.node;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.drools.definition.type.FactField;
import org.drools.definition.type.FactType;
import org.drools.runtime.rule.QueryResultsRow;
import org.eeat.drools.rbs.Drools;
import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DataType;
import org.knime.core.data.RowKey;
import org.knime.core.data.def.DefaultRow;
import org.knime.core.data.def.DoubleCell;
import org.knime.core.data.def.IntCell;
import org.knime.core.data.def.LongCell;
import org.knime.core.data.def.StringCell;
import org.knime.core.data.def.TimestampCell;
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
import org.knime.core.node.defaultnodesettings.SettingsModelString;


/**
 * This is the model implementation of Drools.
 * 
 *
 * @author wrobinson.cis.gsu.edu
 */
public class DroolsNodeModel extends NodeModel {
	
	static final String CFG_USER_FILE_NAME = "droolsFile";
	public final String KNIME_ROW_OUT_KEY = "_key";
	public final String LOGGER = "logger";
	public final String INPUT_DATA = "knimeInputData";
	public final String OUTPUT_DATA = "knimeOutputData";
	public final String DROOLS_OBJECT = "parent";
	public final String KNIME_PACKAGE = "org.eeat.drools.rbs";
	public final String KNIME_CONTROL = "KnimeControl";
	public final String KNIME_CONTROL_MSG = "message"; 
	public final String KNIME_CONTROL_DATE = "date"; 
	public final String KNIME_ROW_IN = "KnimeInput";    
	public final String KNIME_ROW_OUT = "KnimeOutput";
	public final String KNIME_QUERRY =  "Knime results";
	static final String CFG_INCREMENTAL = "incremental";
	protected final SettingsModelBoolean incremental = new SettingsModelBoolean(
			CFG_INCREMENTAL, true);
	
	
    // the logger instance
    private static final NodeLogger logger = NodeLogger
            .getLogger(DroolsNodeModel.class);
        
    
	protected final SettingsModelString userFileSetting = new SettingsModelString(
			CFG_USER_FILE_NAME, null);

    public SettingsModelString getUserFileSetting() {
		return userFileSetting;
	}

	/**
     * Constructor for the node model.
     */
    protected DroolsNodeModel() {
    
        // one incoming port and one outgoing port
        super(1, 1);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected BufferedDataTable[] execute(final BufferedDataTable[] inData,
            final ExecutionContext exec) throws Exception {

        BufferedDataTable out = processWithDrools(inData[0],exec);        
        return new BufferedDataTable[]{out};
    }
    
    protected BufferedDataTable processWithDrools(final BufferedDataTable inData,
            final ExecutionContext exec) throws CanceledExecutionException {
    	logger.debug("Begin processing Drools node.");    	
    	Drools drools = userFileSetting.getStringValue() != null ? new Drools(userFileSetting.getStringValue()) : new Drools();
    	logger.debug("Drools created.");
    	drools.setGlobalVariable(INPUT_DATA, inData);
    	drools.setGlobalVariable(DROOLS_OBJECT, drools);
    	drools.setGlobalVariable(LOGGER, logger);
    	List<Object> dataList = transformData(inData,drools);
    	double listSize = (double)dataList.size();
    	int i=1;
    	for (Object o : dataList) {
    		logger.debug(String.format("%d/%d In: %.140s", i++, dataList.size(),o.toString()));
    		drools.insert(o);
    		// IMPORTANT: run drools execute immediately after insert
    		// In general, do not batch, which will prevent Drools from properly managing memory.
			if (incremental.getBooleanValue()) {
				drools.execute();
			}
			// check if the execution monitor was canceled
			exec.checkCanceled();
			exec.setProgress(i / listSize, "Adding row " + i);
    	}
    	insertControlMessage(drools,"Data complete");
		drools.execute();
    	BufferedDataTable outTable = collectDroolsResults(drools,exec);    	
    	drools.close();
    	logger.debug("End processing Drools node.");
    	return outTable;
    }
    
    protected BufferedDataTable collectDroolsResults(final Drools drools, final ExecutionContext exec) throws CanceledExecutionException {
    	BufferedDataTable out = null;
		DataTableSpec outputSpec = createTableSpec(drools);
    	List<Object> results = getDroolsResults(drools);    	
    	BufferedDataContainer container = exec.createDataContainer(outputSpec);
    	int rowNum =0;
		if (!results.isEmpty()) {			
			for (Object  r : results) {				
	            container.addRowToTable(createDataRow(drools,r,rowNum));
	            // check if the execution monitor was canceled
	            exec.checkCanceled();
	            exec.setProgress(rowNum / (double) results.size(), "Adding row " + rowNum++);
			}

		}
		else {
			logger.warn("Drool results in empty set.");
		}
        container.close();
        out = container.getTable();
		return out;
    }
    
    protected List<Object > getDroolsResults(Drools drools) {
    	ArrayList<Object> list = new ArrayList<Object>();
    	for (QueryResultsRow row : drools.getRuleSession().getQueryResults(KNIME_QUERRY)) {
    		list.add(row.get("out"));
    	}
    	return list; 		
    }
    
	protected DataRow createDataRow(Drools drools, Object droolsFact, int rowNum) {
		FactType objectType = drools.getRuleKnowledgeBase().getFactType(KNIME_PACKAGE,
				KNIME_ROW_OUT);
		RowKey key = new RowKey(String.valueOf(rowNum));
		DataCell[] cells = new DataCell[objectType.getFields().size()];
		List<FactField> fields = objectType.getFields();
		int i = 0;
		for (FactField f : fields) {
			Object value;
			value = getField(droolsFact, f.getName());
			String type = f.getType().getSimpleName();
			try {
				if (type.equalsIgnoreCase("Integer")) {
					cells[i++] = new IntCell((int) value);
				} else if (type.equalsIgnoreCase("Long")) {
					cells[i++] = new LongCell((long) value);
				} else if (type.equalsIgnoreCase("Double")) {
					cells[i++] = new DoubleCell((double) value);
				} else if (type.equalsIgnoreCase("String")) {
					cells[i++] = new StringCell((value != null) ? (String) value : "");
				} else if (type.equalsIgnoreCase("ArrayList")) {
					cells[i++] = new StringCell((value != null) ? Arrays.toString(((ArrayList)value).toArray()) : "");
				} else if (type.equalsIgnoreCase("Timestamp")) {
					try {
						cells[i++] = new TimestampCell((String) value);
					} catch (ParseException e) {
						cells[i++] = null;
						logger.error(e.toString());
						logger.error(String.format(
								"In row %s, for field %s, cannot parse %s as Timestamp.", rowNum,
								f.getName(), value));
					}
				} else {
					cells[i++] = new StringCell((value != null) ? (String) value : "");
					logger.warn(String.format(
							"Unknown type: In row %s, for field %s having type %s with value %s.",
							rowNum, f.getName(), type, value));
				}
			} catch (ClassCastException cce) {
				logger.error(cce.toString());
				logger.error(String.format(
						"In row %s, for field %s having type %s cannot cast %s.", rowNum,
						f.getName(), type, value));
			}
		}
		DataRow row = new DefaultRow(key, cells);
		return row;
	}
    
	protected Object getField(Object object, String field) {
		if (field.length() > 0) {
			String getField = "get" + field.substring(0, 1).toUpperCase();
			if (field.length() > 1) {
				getField = getField + field.substring(1);
			}
			try {
				Method m = object.getClass().getMethod(getField);
				if (m != null) {
					return m.invoke(object);
				}
			} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				logger.error(e.toString());
				logger.error(String.format("No such method %s for Drools object delaration %s.", getField,field));
			}
		}
		return null;
	}
    
	protected DataTableSpec createTableSpec(Drools drools) {
		FactType objectType = drools.getRuleKnowledgeBase().getFactType(KNIME_PACKAGE,
				KNIME_ROW_OUT);
		List<FactField> fields = objectType.getFields();
		DataColumnSpec[] allColSpecs = new DataColumnSpec[fields.size()];
		int i = 0;
		for (FactField f : fields) {
			String type = f.getType().getSimpleName();
			if (type.equalsIgnoreCase("Integer")) {
				allColSpecs[i++] = new DataColumnSpecCreator(f.getName(), IntCell.TYPE).createSpec();
			} else if (type.equalsIgnoreCase("Long")) {
				allColSpecs[i++] = new DataColumnSpecCreator(f.getName(), LongCell.TYPE).createSpec();
			} else if (type.equalsIgnoreCase("Double")) {
				allColSpecs[i++] = new DataColumnSpecCreator(f.getName(), DoubleCell.TYPE)
						.createSpec();
			} else if (type.equalsIgnoreCase("Timestamp")) {
				allColSpecs[i++] = new DataColumnSpecCreator(f.getName(), TimestampCell.TYPE)
						.createSpec();
			} else {
				allColSpecs[i++] = new DataColumnSpecCreator(f.getName(), StringCell.TYPE)
						.createSpec();
			}
		}
		DataTableSpec outputSpec = new DataTableSpec(allColSpecs);
		return outputSpec;
	}
    
    protected List<Object> transformData(final BufferedDataTable inData,
            final Drools drools) {
    	logger.debug("Converting data rows: " + inData.getRowCount());
    	ArrayList<Object> list = new ArrayList<Object>();
    	int i = 1; 
		for (DataRow row : inData) {
			//logger.debug(String.format("Transforming row: %s",i++));
			list.add(rowToObject(row, inData.getDataTableSpec(), drools));
        }			
		return list;
    }
    
    protected Object insertControlMessage(final Drools drools, String message) {    	
		FactType objectType = drools.getRuleKnowledgeBase().getFactType(KNIME_PACKAGE,
				KNIME_CONTROL);
		Object obj = null;
		try {
			obj = objectType.newInstance();
		} catch (InstantiationException e) {
			logger.error(e.toString());
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			logger.error(e.toString());
			e.printStackTrace();
		}
		objectType.set(obj,KNIME_CONTROL_MSG,message);
		objectType.set(obj,KNIME_CONTROL_DATE,new Date().getTime());
		drools.insert(obj);
		return obj;
    }
    
	protected Object rowToObject(DataRow row, DataTableSpec dataTableSpec, final Drools drools) {
		FactType objectType = drools.getRuleKnowledgeBase().getFactType(KNIME_PACKAGE, KNIME_ROW_IN);
		Object obj = null;
		int i = 0;
		try {
			obj = objectType.newInstance();
		} catch (InstantiationException e) {
			logger.error(e.toString());
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			logger.error(e.toString());
			e.printStackTrace();
		}
		try {
			// Set key value...
			objectType.set(obj, KNIME_ROW_OUT_KEY, row.getKey().getString());
		} catch (NullPointerException npe) {
			logger.error(npe.toString());
			logger.error("...check that the KNIME input Class has a field for the key named: " + KNIME_ROW_OUT_KEY);
		}
		Iterator<DataCell> itr = row.iterator();
		while (itr.hasNext()) {
			DataCell cell = itr.next();
			String columnName = dataTableSpec.getColumnSpec(i).getName();
			DataColumnSpec columnSpec = dataTableSpec.getColumnSpec(i);
			String v = cell.toString();
			// logger.debug(String.format("Name: %s, value: %s",columnName,v));
			if (cell.isMissing() || v.length() <= 0) {
				// Do nothing
			} else {
				setObjectType(objectType, obj, columnSpec, columnName, v);
			}
			i = i+1;
		}
		return obj;
	}
    
    Object setObjectType(FactType objectType, Object obj, DataColumnSpec columnSpec, String columnName, String value) {
    	DataType type = columnSpec.getType();
    	String typeName = type.toString();
		try {
			// WARNING: cell names must match Class field names
			if (type.toString().equals("StringCell")) {
				objectType.set(obj, columnName, value);
			} else if (type.toString().equals("IntCell")) {
				objectType.set(obj, columnName, Integer.valueOf(value));
			} else if (type.toString().equals("LongCell")) {
				objectType.set(obj, columnName, Long.valueOf(value));
			} else if (type.toString().equals("DoubleCell")) {
				objectType.set(obj, columnName, Double.valueOf(value));
			} else if (type.toString().equals("TimestampCell")) {
				objectType.set(obj, columnName, Timestamp.valueOf(value));
			} else {
				logger.warn(String.format("Unknown type while casting column: %s of type %s with value %s",
						columnName, type,value));
				objectType.set(obj, columnName, value);
			}
		} catch (ClassCastException e) {
			logger.error(e.toString());
			logger.error(String.format("Class cast error while casting column: %s of type %s with value %s",
					columnName, type,value));
			logger.error(String.format("... check that the data column type is the same as the Class field type."));
		} catch (NullPointerException e) {
			logger.error(e.toString());
			logger.error("The column names and Class fields may have a mismatch, while setting column: "
					+ columnName);
			logger.error("...check that the Class has the field and its name is a match with the data column");
		}
    	return obj;
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

        return new DataTableSpec[]{null};
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) {       
        userFileSetting.saveSettingsTo(settings);
        incremental.saveSettingsTo(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadValidatedSettingsFrom(final NodeSettingsRO settings)
            throws InvalidSettingsException {
        userFileSetting.loadSettingsFrom(settings);
        incremental.loadSettingsFrom(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validateSettings(final NodeSettingsRO settings)
            throws InvalidSettingsException {
        // Do not actually set any values of any member variables.
        userFileSetting.validateSettings(settings);
        incremental.validateSettings(settings);

    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadInternals(final File internDir,
            final ExecutionMonitor exec) throws IOException,
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
    protected void saveInternals(final File internDir,
            final ExecutionMonitor exec) throws IOException,
            CanceledExecutionException {
       
        // TODO save internal models. 
        // Everything written to output ports is saved automatically (data
        // returned by the execute method, models saved in the saveModelContent,
        // and user settings saved through saveSettingsTo - is all taken care 
        // of). Save here only the other internals that need to be preserved
        // (e.g. data used by the views).

    }

}

