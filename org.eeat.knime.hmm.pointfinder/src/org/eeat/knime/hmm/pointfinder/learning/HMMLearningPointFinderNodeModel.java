package org.eeat.knime.hmm.pointfinder.learning;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eeat.knime.hmm.pointfinder.builder.HMMCreator;
import org.eeat.knime.hmm.pointfinder.builder.Observation;
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

/**
 * This is the model implementation of HMMLearningPointFinder.
 * 
 * 
 * @author
 */
public class HMMLearningPointFinderNodeModel extends NodeModel {

	// the logger instance
	private static final NodeLogger logger = NodeLogger
			.getLogger(HMMLearningPointFinderNodeModel.class);

	/**
	 * the settings key which is used to retrieve and store the settings (from
	 * the dialog or from a settings file) (package visibility to be usable from
	 * the dialog).
	 */
	static final String CFGKEY_COUNT = "Count";

	/** initial default count value. */
	static final int DEFAULT_COUNT = 100;

	// example value: the models count variable filled from the dialog
	// and used in the models execution method. The default components of the
	// dialog work with "SettingsModels".
	private final SettingsModelIntegerBounded m_count = new SettingsModelIntegerBounded(
			HMMLearningPointFinderNodeModel.CFGKEY_COUNT,
			HMMLearningPointFinderNodeModel.DEFAULT_COUNT, Integer.MIN_VALUE,
			Integer.MAX_VALUE);

	/**
	 * Constructor for the node model.
	 */
	protected HMMLearningPointFinderNodeModel() {

		// TODO one incoming port and one outgoing port is assumed
		super(2, 1);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected BufferedDataTable[] execute(final BufferedDataTable[] inData,
			final ExecutionContext exec) throws Exception {

		// TODO do something here
		/*
		 * logger.info("Node Model Stub... this is not yet implemented !");
		 * 
		 * 
		 * 
		 * // the data table spec of the single output table, // the table will
		 * have three columns: DataColumnSpec[] allColSpecs = new
		 * DataColumnSpec[3]; allColSpecs[0] = new
		 * DataColumnSpecCreator("Column 0", StringCell.TYPE).createSpec();
		 * allColSpecs[1] = new DataColumnSpecCreator("Column 1",
		 * DoubleCell.TYPE).createSpec(); allColSpecs[2] = new
		 * DataColumnSpecCreator("Column 2", IntCell.TYPE).createSpec();
		 * DataTableSpec outputSpec = new DataTableSpec(allColSpecs); // the
		 * execution context will provide us with storage capacity, in this //
		 * case a data container to which we will add rows sequentially // Note,
		 * this container can also handle arbitrary big data tables, it // will
		 * buffer to disc if necessary. BufferedDataContainer container =
		 * exec.createDataContainer(outputSpec); // let's add m_count rows to it
		 * for (int i = 0; i < m_count.getIntValue(); i++) { RowKey key = new
		 * RowKey("Row " + i); // the cells of the current row, the types of the
		 * cells must match // the column spec (see above) DataCell[] cells =
		 * new DataCell[3]; cells[0] = new StringCell("String_" + i); cells[1] =
		 * new DoubleCell(0.5 * i); cells[2] = new IntCell(i); DataRow row = new
		 * DefaultRow(key, cells); container.addRowToTable(row);
		 * 
		 * // check if the execution monitor was canceled exec.checkCanceled();
		 * exec.setProgress(i / (double)m_count.getIntValue(), "Adding row " +
		 * i); } // once we are done, we close the container and return its
		 * table container.close(); BufferedDataTable out =
		 * container.getTable(); return new BufferedDataTable[]{out};
		 */
		
		Hmm<ObservationDiscrete<Observation>> hmm;
		
		ArrayList<Hmm<ObservationDiscrete<Observation>>> hmmList = new ArrayList<Hmm<ObservationDiscrete<Observation>>>();
		List<List<Hmm<ObservationDiscrete<Observation>>>> hmmListList = new ArrayList<List<Hmm<ObservationDiscrete<Observation>>>>();

		CloseableRowIterator itr = null;

		DataRow dataRow = null;

		itr = inData[0].iterator();

		dataRow = itr.next();

		int numberOfInflectionPoints = Integer.parseInt(dataRow.getCell(0)
				.toString());

		dataRow = null;

		itr = null;

		itr = inData[1].iterator();
		
		int index = 0; // for each two week 

		while (itr.hasNext()) {

			dataRow = itr.next();

			logger.debug("THE row for creating HMM:" + dataRow);

			DataCell dataCell = dataRow.getCell(0);
			DataCell dataCel2 = dataRow.getCell(1);
			DataCell dataCel3 = dataRow.getCell(2);
			DataCell dataCel4 = dataRow.getCell(3);
			DataCell dataCel5 = dataRow.getCell(4);
			DataCell dataCel6 = dataRow.getCell(5);
			DataCell dataCel7 = dataRow.getCell(6);
			DataCell dataCel8 = dataRow.getCell(7);
			DataCell dataCel9 = dataRow.getCell(8);
			DataCell dataCel10 = dataRow.getCell(9);

			logger.debug("Pi_A:" + dataCell.toString());
			logger.debug("Pi_B:" + dataCel2.toString());
			logger.debug("A->A:" + dataCel3.toString());
			logger.debug("A->B:" + dataCel4.toString());
			logger.debug("B->A:" + dataCel5.toString());
			logger.debug("B->B:" + dataCel6.toString());
			logger.debug("A->Read:" + dataCel7.toString());
			logger.debug("A->Compose:" + dataCel8.toString());
			logger.debug("B->Read:" + dataCel9.toString());
			logger.debug("B->Compose:" + dataCel10.toString());

			hmm = new Hmm<ObservationDiscrete<Observation>>(2,
					new OpdfDiscreteFactory<Observation>(Observation.class));

			hmm.setPi(0, Double.parseDouble(dataCell.toString()));
			hmm.setPi(1, Double.parseDouble(dataCel2.toString()));

			hmm.setOpdf(0, new OpdfDiscrete<Observation>(Observation.class,
					new double[] { Double.parseDouble(dataCel7.toString()),
							Double.parseDouble(dataCel8.toString()) }));
			hmm.setOpdf(1, new OpdfDiscrete<Observation>(Observation.class,
					new double[] { Double.parseDouble(dataCel9.toString()),
							Double.parseDouble(dataCel10.toString()) }));

			hmm.setAij(0, 0, Double.parseDouble(dataCel3.toString()));
			hmm.setAij(0, 1, Double.parseDouble(dataCel4.toString()));
			hmm.setAij(1, 0, Double.parseDouble(dataCel5.toString()));
			hmm.setAij(1, 1, Double.parseDouble(dataCel6.toString()));

			hmmList.add(hmm);
			
			// every two week (42 hour segment in a 2 week, 1-4 sat ...)
			if ((index + 1) % 42 == 0) {
			
				hmmListList.add(hmmList);
				hmmList = new ArrayList<Hmm<ObservationDiscrete<Observation>>>();
			}
			
			index++;

		}
		
		List<Double> errorList = HMMCreator.diffHMMAverage(hmmListList);
		
		//finds the peaks by finding the max errors -- index of weeks that peak happened
		int[] indexForInflecionWeeks = findInflationWeekIndex (errorList, numberOfInflectionPoints);

		// you have inflection points
		
		//make number of inflection points groups
		//put all those hmms with minimum of the distance between weeks in each group
		//we hard code now just 4, 2 weeks - each list has 42 HMM
		//then we start comparing for each group the distance between HMM and see which scores
		//more if score more the same group thats learning
		
		// another explanation, there is word document for this too
		// at each inflection point, take 4, 2week after and before
		// make group A and B
		// for each HMM in after inflection point compare the distance with all after inflection
		// and before is closet one was before then A++, if after then B++
		// if B scores more eventually then that inflection point was a learning otherwise it was not
		// why? because it means more sequnece of activities are not similar to previous ones and so it is a new phenomena
		// it is very possible is a learning thing
		
//		List<Hmm<ObservationDiscrete<Observation>>> group = new ArrayList<Hmm<ObservationDiscrete<Observation>>>();
		
		List<List<Hmm<ObservationDiscrete<Observation>>>> groups = new ArrayList<List<Hmm<ObservationDiscrete<Observation>>>>();
		
		for (int i = 0; i < 2 * indexForInflecionWeeks.length; i++) {
			
			List<Hmm<ObservationDiscrete<Observation>>> group = new ArrayList<Hmm<ObservationDiscrete<Observation>>>();
			
			groups.add(group);
			
			addToGroup(indexForInflecionWeeks[i/2], hmmListList, group, i);
			
		}	
		
		System.out.println("===========================");
		for (int i = 0; i < groups.size(); i++)
		System.out.println(groups.get(i).size());
		
		//int[] scores = new int[2 * indexForInflationWeeks.length];
		double[] scores = new double[2 * indexForInflecionWeeks.length];
		
		// calculate scores for A and B at each point
		calculateScores(groups, scores);
		
		System.out.println("##############");
		
		// so total 4 points and each point A and B so totally 8 A and B
		// this is what we really looking for
		for (int i = 0; i < scores.length; i++) {
			
			System.out.println(i + ":" + scores[i]);
			
		}
		
		
		// just for printing
	    System.out.println("##############");
	    
	    for (int g : indexForInflecionWeeks) {
		    
	    	//	    	System.out.println(g);
	    	
	    	logger.debug("the index of weeks");
	    	logger.debug(g);
	    	
	    }	  
	    
	    System.out.println("*****************************************************");
	    
	    // just for printing
		for (double error: errorList) {
		
			System.out.println(error);
		}
		
		
		
		
        DataColumnSpec[] allColSpecs = new DataColumnSpec[3];
        
        allColSpecs[0] = 
                new DataColumnSpecCreator("Learning Scores", DoubleCell.TYPE).createSpec();
        
        allColSpecs[1] = 
                new DataColumnSpecCreator("Learning/Not Learning [0.5 (min) to 1.0 (max)]", StringCell.TYPE).createSpec();
        
        allColSpecs[2] = 
                new DataColumnSpecCreator("Happening Week (Two week Unit)", StringCell.TYPE).createSpec();        
		
		
        DataTableSpec outputSpec = new DataTableSpec(allColSpecs);
        
        BufferedDataContainer container = exec.createDataContainer(outputSpec);
        
        DataCell[] cells = new DataCell[3];
        
        RowKey key;
        	
        	
        	for (int i = 0; i < scores.length; i++) {
        		
        		key = new RowKey("Row" + i); 
        		
        		cells[0] = new DoubleCell(scores[i]); 
        		
        		cells[1] = new StringCell("");
        		cells[2] = new StringCell("");
        		
        		if (i % 2 == 0) {
        			
                   if (scores[i] < scores[i+1])  {		
        			
        			cells[1] = new StringCell("Learning with ratio " + (scores[i+1]/(scores[i+1] + scores[i])));
        			cells[2] = new StringCell (indexForInflecionWeeks[i / 2] + "");
        			
                   }
                   
                   else { 
                	   
                	   cells[1] = new StringCell("No Learning with ratio " + (scores[i]/(scores[i+1] + scores[i])));
                	   cells[2] = new StringCell (indexForInflecionWeeks[i / 2] + "");
                	   
                   }
        		} 
        		
                DataRow row = new DefaultRow(key, cells);
                container.addRowToTable(row);
        		
        	}
        	
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

		return new DataTableSpec[] { null };
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
	
	public static int[] findInflationWeekIndex (List<Double> errorList, int numberOfInflation) {
		
		int[] indexForInflationWeeks = new int[numberOfInflation];
		
	    double maxError = 0;
	    int maxIndex;
	    
    
	    List<Double> errorListTemp = new ArrayList<Double>();
	    
	    for (int k = 0; k < errorList.size(); k++) {
	    	
	    	errorListTemp.add(errorList.get(k));
	    }
	    
	    int k = 0;
	    
	    for (int j = 0; j < numberOfInflation ; j++) {
	    	
	    	maxError = 0;
	    	maxIndex = 0;
	    
	    	for (int i = 0; i < errorListTemp.size(); i++) {
	    	
	    		if (errorListTemp.get(i) > maxError) {
	    		
	    			maxError = errorListTemp.get(i);
	    		    maxIndex = i;
	    		}
	    	
	    	}
			indexForInflationWeeks[k++] = maxIndex;
			errorListTemp.set(maxIndex, 0d);
	    }

	    return indexForInflationWeeks;
	
	}
	
	public static void addToGroup(int indexForInflationWeeks, List<List<Hmm<ObservationDiscrete<Observation>>>> hmmListList, List<Hmm<ObservationDiscrete<Observation>>> group, int h) {
		
		
		List<Hmm<ObservationDiscrete<Observation>>> hmmList;
		
		// for only 4  weeks
		
		for (int k = 1; k < 5; k++) {
		
		   if (h % 2 == 0) 	{
			   
			  if (indexForInflationWeeks - k <= 0) break;
			
		   hmmList = hmmListList.get(indexForInflationWeeks - k);
		   
		   } else {
			   
			   if (indexForInflationWeeks + k -1 >= 52) break;
			   
			   hmmList = hmmListList.get(indexForInflationWeeks + k -1);
			   
		   }
		   
		   // 42
		
		   for (int i = 0; i <  hmmList.size(); i++) {
			   
			   group.add(hmmList.get(i));
		         	   
		   }
		
		}
	}	
	
	public static void calculateScores(List<List<Hmm<ObservationDiscrete<Observation>>>> groups, double[] scores) {
		
		for (int i = 0; i < groups.size() - 1; i++) {
			
			scores[i] = 0;
			scores[i+1] = 0;
			
			double minInOtherGroup = 0.0;
			double minInOwnGroup = 0.0;
			
			for (int j = 0; j < groups.get(i + 1).size(); j++) {
				
				 minInOtherGroup = compareAndGiveMinInAnotherGroup(groups.get(i), groups.get(i+1).get(j));
				 
				 minInOwnGroup = compareAndGiveMinInOwnGroup(groups.get(i+1), groups.get(i+1).get(j), j);
			
		         if (minInOtherGroup > minInOwnGroup) {
		        	 
		        	// it was giving one score if min in own group was less than other group
		        	// changed to give weight score
		        	// scores[i]++;
		        	 scores[i] += minInOtherGroup/(minInOtherGroup + minInOwnGroup);
		        	 
		        	 
		         } else {
		        	 
		        	 //scores[i+1]++;
		        	 scores[i+1] += minInOwnGroup/(minInOtherGroup + minInOwnGroup);
		         }
				
			}
			
		}
	}
	
	public static double compareAndGiveMinInAnotherGroup(List<Hmm<ObservationDiscrete<Observation>>> hmmList, Hmm<ObservationDiscrete<Observation>> hmm) {
		
		double min = 10000000;
		
		double minTemp = 0;
		
		for (int i = 0; i < hmmList.size(); i++) {
			

			
			minTemp = Math.abs(HMMCreator.diffHMMTwo(hmmList.get(i), hmm));
			
			if (minTemp < min && minTemp != 0 && !Double.isNaN(minTemp)) {
				
				min = minTemp;
			}
		}
		
	logger.info("COMPARING DONE");
		
	//	System.out.println("COMPARING DONE");
		
		return min;
		
	}
	
	// the last int is for not comparing with itself in its own group
	public static double  compareAndGiveMinInOwnGroup(List<Hmm<ObservationDiscrete<Observation>>> hmmList, Hmm<ObservationDiscrete<Observation>> hmm, int index) {
		
		double min = 10000000;
		
		double minTemp = 0;
		
		for (int i = 0; i < hmmList.size(); i++) {
			

			
			minTemp = Math.abs(HMMCreator.diffHMMTwo(hmmList.get(i), hmm));
			
			if (minTemp < min && minTemp != 0 && !Double.isNaN(minTemp) && index != i) {
				
				min = minTemp;
			}
		}
	//	System.out.println("COMPARING OWN DONE");
		
		logger.info("COMPARING OWN DONE");
		
		return min;
		
	}	

}
