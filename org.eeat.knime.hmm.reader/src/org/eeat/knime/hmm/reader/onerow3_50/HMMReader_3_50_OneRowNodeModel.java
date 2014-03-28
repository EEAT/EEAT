package org.eeat.knime.hmm.reader.onerow3_50;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.eeat.knime.hmm.reader.observation.Observation50;
import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.RowKey;
import org.knime.core.data.container.CloseableRowIterator;
import org.knime.core.data.def.DefaultRow;
import org.knime.core.data.def.DoubleCell;
import org.knime.core.data.def.IntCell;
import org.knime.core.data.def.StringCell;
import org.knime.core.node.BufferedDataContainer;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.defaultnodesettings.SettingsModelIntegerBounded;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;

import be.ac.ulg.montefiore.run.jahmm.Hmm;
import be.ac.ulg.montefiore.run.jahmm.ObservationDiscrete;
import be.ac.ulg.montefiore.run.jahmm.OpdfDiscrete;
import be.ac.ulg.montefiore.run.jahmm.OpdfDiscreteFactory;
import be.ac.ulg.montefiore.run.jahmm.toolbox.KullbackLeiblerDistanceCalculator;


/**
 * This is the model implementation of HMMReader_3_50_OneRow.
 * 
 *
 * @author Arash
 */
public class HMMReader_3_50_OneRowNodeModel extends NodeModel {
    
    // the logger instance
    private static final NodeLogger logger = NodeLogger
            .getLogger(HMMReader_3_50_OneRowNodeModel.class);
        
    /** the settings key which is used to retrieve and 
        store the settings (from the dialog or from a settings file)    
       (package visibility to be usable from the dialog). */
	static final String CFGKEY_COUNT = "Count";

    /** initial default count value. */
    static final int DEFAULT_COUNT = 100;

    // example value: the models count variable filled from the dialog 
    // and used in the models execution method. The default components of the
    // dialog work with "SettingsModels".
    private final SettingsModelIntegerBounded m_count =
        new SettingsModelIntegerBounded(HMMReader_3_50_OneRowNodeModel.CFGKEY_COUNT,
                    HMMReader_3_50_OneRowNodeModel.DEFAULT_COUNT,
                    Integer.MIN_VALUE, Integer.MAX_VALUE);
    

    /**
     * Constructor for the node model.
     */
    protected HMMReader_3_50_OneRowNodeModel() {
    
        // TODO one incoming port and one outgoing port is assumed
        super(1, 1);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected BufferedDataTable[] execute(final BufferedDataTable[] inData,
            final ExecutionContext exec) throws Exception {

    	ArrayList<Double> hmmDistances = new ArrayList<Double>();
    	
    	Hmm<ObservationDiscrete<Observation50>> hmm;
    	ArrayList<Hmm<ObservationDiscrete<Observation50>>> hmmList = new ArrayList<Hmm<ObservationDiscrete<Observation50>>>();

        // TODO do something here
        logger.info("Node Model Stub... this is not yet implemented ?");
        
		CloseableRowIterator itr = null;

		//while (itr.hasNext()) {
		{
			
			DataRow dataRow = null;
			
			
				itr = inData[0].iterator();
				
		 while (itr.hasNext()) {
			 
	 
		
			dataRow = itr.next();

			logger.info("THE row for creating HMM:" + dataRow);
			
			DataCell[] dataCellArray = new DataCell[162];
			
			for (int i = 0; i < dataCellArray.length; i++) {
				
				dataCellArray[i] = dataRow.getCell(i);
			}

//			DataCell dataCell = dataRow.getCell(0);
//			DataCell dataCel2 = dataRow.getCell(1);
//			DataCell dataCel3 = dataRow.getCell(2);
//			DataCell dataCel4 = dataRow.getCell(3);
//			DataCell dataCel5 = dataRow.getCell(4);
//			DataCell dataCel6 = dataRow.getCell(5);
///			DataCell dataCel7 = dataRow.getCell(6);
//			DataCell dataCel8 = dataRow.getCell(7);
//			DataCell dataCel9 = dataRow.getCell(8);
//			DataCell dataCel10 = dataRow.getCell(9);
			
//			logger.info("Pi_A:" + dataCell.toString());
//			logger.info("Pi_B:" + dataCel2.toString());
//			logger.info("A->A:" + dataCel3.toString());
//			logger.info("A->B:" + dataCel4.toString());
//			logger.info("B->A:" + dataCel5.toString());
//			logger.info("B->B:" + dataCel6.toString());
//			logger.info("A->Read:" + dataCel7.toString());
//			logger.info("A->Compose:" + dataCel8.toString());
//			logger.info("B->Read:" + dataCel9.toString());
//			logger.info("B->Compose:" + dataCel10.toString());
			
			for (int i = 0; i < dataCellArray.length; i++) {
				
				logger.info(i + "=" + dataCellArray[i]);
			}
			
			hmm = new Hmm<ObservationDiscrete<Observation50>>(50,
					new OpdfDiscreteFactory<Observation50>(Observation50.class));
			
			
/*
			hmm.setPi(0, Double.parseDouble(dataCell.toString()));
			hmm.setPi(1, Double.parseDouble(dataCel2.toString()));

			hmm.setOpdf(
					0,
					new OpdfDiscrete<Observation>(Observation.class,
							new double[] { Double.parseDouble(dataCel7.toString()),
						Double.parseDouble(dataCel8.toString()) }));
			hmm.setOpdf(
					1,
					new OpdfDiscrete<Observation>(Observation.class,
							new double[] { Double.parseDouble(dataCel9.toString()),
						Double.parseDouble(dataCel10.toString()) }));

			hmm.setAij(0, 0, Double.parseDouble(dataCel3.toString()));
			hmm.setAij(0, 1, Double.parseDouble(dataCel4.toString()));
			hmm.setAij(1, 0, Double.parseDouble(dataCel5.toString()));
			hmm.setAij(1, 1, Double.parseDouble(dataCel6.toString())); */
			
			hmm.setPi(0, Double.parseDouble(dataCellArray[0].toString()));
			hmm.setPi(1, Double.parseDouble(dataCellArray[1].toString()));
			hmm.setPi(2, Double.parseDouble(dataCellArray[2].toString()));
			
			hmm.setAij(0, 0, Double.parseDouble(dataCellArray[3].toString()));
			hmm.setAij(0, 1, Double.parseDouble(dataCellArray[4].toString()));
			hmm.setAij(0, 2, Double.parseDouble(dataCellArray[5].toString()));
			hmm.setAij(1, 0, Double.parseDouble(dataCellArray[6].toString()));
			hmm.setAij(1, 1, Double.parseDouble(dataCellArray[7].toString()));
			hmm.setAij(1, 2, Double.parseDouble(dataCellArray[8].toString()));
			hmm.setAij(2, 0, Double.parseDouble(dataCellArray[9].toString()));
			hmm.setAij(2, 1, Double.parseDouble(dataCellArray[10].toString()));
			hmm.setAij(2, 2, Double.parseDouble(dataCellArray[11].toString()));		
			
			
			double[] observPropForOneState = new double[50];
			
			for (int i = 12; i < 62; i++) {
				
				observPropForOneState[i-12] = Double.parseDouble(dataCellArray[i].toString());
			}
			
			hmm.setOpdf(
					0,
					new OpdfDiscrete<Observation50>(Observation50.class, observPropForOneState));
			

			for (int i = 62; i < 112; i++) {
				
				observPropForOneState[i-62] = Double.parseDouble(dataCellArray[i].toString());
			}
			
			hmm.setOpdf(
					1,
					new OpdfDiscrete<Observation50>(Observation50.class, observPropForOneState));			
			
			for (int i = 112; i < 162; i++) {
				
				observPropForOneState[i-112] = Double.parseDouble(dataCellArray[i].toString());
			}
			
			hmm.setOpdf(
					2,
					new OpdfDiscrete<Observation50>(Observation50.class, observPropForOneState));			
			
			hmmList.add(hmm);
			
		  }
		 }
		
		KullbackLeiblerDistanceCalculator klc = 
				new KullbackLeiblerDistanceCalculator();
		
		
		double difference = 0;
		
		for (int i = 0; i < hmmList.size() - 1; i++) {
			
			difference = klc.distance(hmmList.get(i), hmmList.get(i + 1));
			
			hmmDistances.add(difference);
		}
		
        DataColumnSpec[] allColSpecs = new DataColumnSpec[1];
        
        allColSpecs[0] = 
                new DataColumnSpecCreator("HMM Difference", DoubleCell.TYPE).createSpec();
		
		
        DataTableSpec outputSpec = new DataTableSpec(allColSpecs);
        
        BufferedDataContainer container = exec.createDataContainer(outputSpec);
        
        DataCell[] cells = new DataCell[1];
        
        RowKey key;
        
        if (hmmDistances.size() == 0) {
        	
        	key = new RowKey("Row0");
        	cells[0] = new DoubleCell(0); 
        	
            DataRow row = new DefaultRow(key, cells);
            container.addRowToTable(row);
        	
        } else {
        	
        	for (int i = 0; i < hmmDistances.size(); i++) {
        		
        		key = new RowKey("Row" + i); 
        		
        		cells[0] = new DoubleCell(hmmDistances.get(i)); 
        		
                DataRow row = new DefaultRow(key, cells);
                container.addRowToTable(row);
        		
        	}
        }
        
        

        
        // the data table spec of the single output table, 
        // the table will have three columns:
//        DataColumnSpec[] allColSpecs = new DataColumnSpec[3];
//        allColSpecs[0] = 
//            new DataColumnSpecCreator("Column 0", StringCell.TYPE).createSpec();
//        allColSpecs[1] = 
//            new DataColumnSpecCreator("Column 1", DoubleCell.TYPE).createSpec();
//        allColSpecs[2] = 
//            new DataColumnSpecCreator("Column 2", IntCell.TYPE).createSpec();
//        DataTableSpec outputSpec = new DataTableSpec(allColSpecs);
        // the execution context will provide us with storage capacity, in this
        // case a data container to which we will add rows sequentially
        // Note, this container can also handle arbitrary big data tables, it
        // will buffer to disc if necessary.
//        BufferedDataContainer container = exec.createDataContainer(outputSpec);
        // let's add m_count rows to it
//        for (int i = 0; i < m_count.getIntValue(); i++) {
//            RowKey key = new RowKey("Row " + i);
            // the cells of the current row, the types of the cells must match
            // the column spec (see above)
//            DataCell[] cells = new DataCell[3];
//            cells[0] = new StringCell("String_" + i); 
//            cells[1] = new DoubleCell(0.5 * i); 
//            cells[2] = new IntCell(i);
//            DataRow row = new DefaultRow(key, cells);
//            container.addRowToTable(row);
            
            // check if the execution monitor was canceled
//            exec.checkCanceled();
//            exec.setProgress(i / (double)m_count.getIntValue(), 
 //               "Adding row " + i);
//        }
        
        
        // once we are done, we close the container and return its table
        container.close();
        BufferedDataTable out = container.getTable();
        return new BufferedDataTable[]{out};    	
    	
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

        // TODO save user settings to the config object.
        
        m_count.saveSettingsTo(settings);

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
        
        m_count.loadSettingsFrom(settings);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validateSettings(final NodeSettingsRO settings)
            throws InvalidSettingsException {
            
        // TODO check if the settings could be applied to our model
        // e.g. if the count is in a certain range (which is ensured by the
        // SettingsModel).
        // Do not actually set any values of any member variables.

        m_count.validateSettings(settings);

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

