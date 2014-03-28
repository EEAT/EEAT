package org.eeat.knime.hmm.node.so3_50;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eeat.knime.hmm.node.builder.Constant;
import org.eeat.knime.hmm.node.builder.HMMCreator;
import org.eeat.knime.hmm.node.builder.observation.Observation50;
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


/**
 * This is the model implementation of HMMNode_3_50.
 * 
 *
 * @author Arash
 */
public class HMMNode_3_50NodeModel extends NodeModel {
    
    // the logger instance
    private static final NodeLogger logger = NodeLogger
            .getLogger(HMMNode_3_50NodeModel.class);
        
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
        new SettingsModelIntegerBounded(HMMNode_3_50NodeModel.CFGKEY_COUNT,
                    HMMNode_3_50NodeModel.DEFAULT_COUNT,
                    Integer.MIN_VALUE, Integer.MAX_VALUE);
    

    /**
     * Constructor for the node model.
     */
    protected HMMNode_3_50NodeModel() {
    
        // TODO one incoming port and one outgoing port is assumed
        super(6, 1);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected BufferedDataTable[] execute(final BufferedDataTable[] inData,
            final ExecutionContext exec) throws Exception {

    	Map<Integer, List<Double>> stateProbs = null; 
    	
    	Map<Integer, List<Double>> emissionProbs = null;
    	
    	List<Double> initialStateProbs; 
    	
		CloseableRowIterator itr;
		
		DataRow dataRow;
		
		itr = inData[0].iterator();
		
		dataRow = itr.next();
		
		int numberOfStates = Integer.parseInt(dataRow.getCell(0).toString());
		
		logger.info("NUMBER OF STATES:" + numberOfStates);
		
		itr = inData[1].iterator();
		
		dataRow = itr.next();
		
		int numberOfObservations = Integer.parseInt(dataRow.getCell(0).toString());
		
		logger.info("NUMBER OF OBSERVATIONS:" + numberOfObservations);
		
		// State Initial Probabilities calculation
		
		itr = inData[2].iterator();
		
		initialStateProbs = new ArrayList<Double>();
		
		while (itr.hasNext()) {
			
			dataRow = itr.next();
			
			if (dataRow.getCell(0).toString().equalsIgnoreCase("NA")) {
				
				logger.info("INITIAL STATE PROB MATRIX IS NOT POPULATED, USING EQUAL DISTRIBUTION OF:"  + (1.0/numberOfStates));

				for (int i = 0; i < numberOfStates; i++) {
					   
					initialStateProbs.add(1.0/numberOfStates);
				}
            
				break;
			}
			
			for (int i = 0; i < numberOfStates; i++) {
				
				DataCell dataCell = dataRow.getCell(i);
				
				initialStateProbs.add(Double.parseDouble(dataCell.toString()));
			}
		}
		
		logger.info("INITIAL STATE PROB MATRIX:" + initialStateProbs);		
    	
		
		// State probability matrix calculation
		
		itr = inData[3].iterator();
		
		int rowInMatrix = 0;
		
		stateProbs = new HashMap<Integer, List<Double>>();
		
		while (itr.hasNext()) {
			
			dataRow = itr.next();
			
			List<Double> rows = new ArrayList<Double>();
			
			if (dataRow.getCell(0).toString().equalsIgnoreCase("NA")) {
				
				
				logger.info("STATE PROB MATRIX IS NOT POPULATED, USING EQUAL DISTRIBUTION OF:"  + (1.0/numberOfStates));
				
				for (int i = 0; i < numberOfStates; i++) {
					
					rows = new ArrayList<Double>();
					
					for (int j = 0; j < numberOfStates; j++) { 
					   
						rows.add(1.0/numberOfStates);
					}
					
					stateProbs.put(i, rows);
				}
				
				break;
			}
			
			for (int i = 0; i < numberOfStates; i++) {
				
				DataCell dataCell = dataRow.getCell(i);
				
				rows.add(Double.parseDouble(dataCell.toString()));
			}
			
			stateProbs.put(rowInMatrix, rows);
			
			rowInMatrix++;
		}
		
		logger.info("STATE PROB MATRIX:" + stateProbs);
		
		
		
		
		
		
		
		// State probability matrix calculation
		
		itr = inData[4].iterator();
		
		rowInMatrix = 0;
		
		emissionProbs = new HashMap<Integer, List<Double>>();
		
		boolean useEmissionFromFile = false;
		
		while (itr.hasNext()) {
			
			dataRow = itr.next();
			
			List<Double> rows = new ArrayList<Double>();
			
			if (!dataRow.getCell(0).toString().equalsIgnoreCase("NA")) {
				
				useEmissionFromFile = true;
				
				for (int i = 0; i < numberOfObservations; i++) {
					
					DataCell dataCell = dataRow.getCell(i);
					
					rows.add(Double.parseDouble(dataCell.toString()));
				}
				
				emissionProbs.put(rowInMatrix, rows);
				
				rowInMatrix++;
			} else {
				
				break;
			}
		}
		
		
		
		
		logger.info("EMISSION PROB MATRIX:" + emissionProbs);		
		

//		int readCount = 0;
//		int composeCount = 0;
		
		int obs0 = 0;
		int obs1 = 0;
		int obs2 = 0;
		int obs3 = 0;
		int obs4 = 0;
		int obs5 = 0;
		int obs6 = 0;
		int obs7 = 0;
		int obs8 = 0;
		int obs9 = 0;
		int obs10 = 0;
		
		int obs11 = 0;
		int obs12 = 0;
		int obs13 = 0;
		int obs14 = 0;
		int obs15 = 0;
		int obs16 = 0;
		int obs17 = 0;
		int obs18 = 0;
		int obs19 = 0;
		int obs20 = 0;
		
		int obs21 = 0;
		int obs22 = 0;
		int obs23 = 0;
		int obs24 = 0;
		int obs25 = 0;
		int obs26 = 0;
		int obs27 = 0;
		int obs28 = 0;
		int obs29 = 0;
		
		int obs30 = 0;
		
		int obs31 = 0;
		int obs32 = 0;
		int obs33 = 0;
		int obs34 = 0;
		int obs35 = 0;
		int obs36 = 0;
		int obs37 = 0;
		int obs38 = 0;
		int obs39 = 0;
		int obs40 = 0;
		
		int obs41 = 0;
		int obs42 = 0;
		int obs43 = 0;
		int obs44 = 0;
		int obs45 = 0;
		int obs46 = 0;
		int obs47 = 0;
		int obs48 = 0;
		int obs49 = 0;		

		
//		List<Double[]> opdfList = null;
//		List<Double> opdfList = null;
		
		double[][] opdfList;

		Hmm<ObservationDiscrete<Observation50>> hmm = null;

		ArrayList<ArrayList<Object[]>> sequenceList = new ArrayList<ArrayList<Object[]>>();

		// while (itr.hasNext()) {

		// logger.info("HELLO:" + itr.next());

		// }
		
		 

		CloseableRowIterator itr1 = inData[5].iterator();
		
		int k = 0;

		ArrayList<Object[]> list = new ArrayList<Object[]>();

//		readCount = 0;
//		composeCount = 0;
		
//		opdfList = new ArrayList<Double[]>();
//		opdfList = new ArrayList<Double>();
		opdfList = new double[numberOfStates][numberOfObservations];

		while (itr1.hasNext()) {

			// logger.info("row:" + k++);

			dataRow = itr1.next();

			DataCell dataCell = dataRow.getCell(0);

			String ss = dataCell.toString();

//			opdfList = new ArrayList<Double[]>();
	//		opdfList = new ArrayList<Double>();
			
//			opdfList = new double[numberOfStates];

			// ArrayList<Object[]> list = null;

			// ArrayList<Object[]> list = new ArrayList<Object[]>();

			Object[] obj = new String[] { ss };

			logger.info("-------------->" + ss);

			list.add(obj);

			int observationIndex = 0;

			// readCount = 0;
			// composeCount = 0;

			// for (Object[] obj : list) {

//			if (ss.equalsIgnoreCase(Constant.EMAIL_READ)) {

//				readCount++;

//			} else if (ss.equalsIgnoreCase(Constant.EMAIL_COMPOSE)) {

//				composeCount++;
//			}
			
			
			if (ss.equalsIgnoreCase(Constant.OBS0)) {

				obs0++;

			} else if (ss.equalsIgnoreCase(Constant.OBS1)) {

				obs1++;
				
			} else if (ss.equalsIgnoreCase(Constant.OBS2)) {

				obs2++;
				
			} else if (ss.equalsIgnoreCase(Constant.OBS3)) {

				obs3++;
				
			} else if (ss.equalsIgnoreCase(Constant.OBS4)) {

				obs4++;
				
			} else if (ss.equalsIgnoreCase(Constant.OBS5)) {

				obs5++;
				
			} else if (ss.equalsIgnoreCase(Constant.OBS6)) {

				obs6++;
				
			} else if (ss.equalsIgnoreCase(Constant.OBS7)) {

				obs7++;
				
			} else if (ss.equalsIgnoreCase(Constant.OBS8)) {

				obs8++;
				
			} else if (ss.equalsIgnoreCase(Constant.OBS9)) {

				obs9++;
				
			} else if (ss.equalsIgnoreCase(Constant.OBS10)) {

				obs10++;
				
			}  else if (ss.equalsIgnoreCase(Constant.OBS11)) {

				obs11++;
				
			}  else if (ss.equalsIgnoreCase(Constant.OBS12)) {

				obs12++;
				
			}  else if (ss.equalsIgnoreCase(Constant.OBS13)) {

				obs13++;
				
			}  else if (ss.equalsIgnoreCase(Constant.OBS14)) {

				obs14++;
				
			}  else if (ss.equalsIgnoreCase(Constant.OBS15)) {

				obs15++;
				
			}  else if (ss.equalsIgnoreCase(Constant.OBS16)) {

				obs16++;
				
			}  else if (ss.equalsIgnoreCase(Constant.OBS17)) {

				obs17++;
				
			}  else if (ss.equalsIgnoreCase(Constant.OBS18)) {

				obs18++;
				
			}  else if (ss.equalsIgnoreCase(Constant.OBS19)) {

				obs19++;
				
			}  else if (ss.equalsIgnoreCase(Constant.OBS20)) {

				obs20++;
				
			}  else if (ss.equalsIgnoreCase(Constant.OBS21)) {

				obs21++;
				
			}  else if (ss.equalsIgnoreCase(Constant.OBS22)) {

				obs22++;
				
			}  else if (ss.equalsIgnoreCase(Constant.OBS23)) {

				obs23++;
				
			}  else if (ss.equalsIgnoreCase(Constant.OBS24)) {

				obs24++;
				
			}  else if (ss.equalsIgnoreCase(Constant.OBS25)) {

				obs25++;
				
			}  else if (ss.equalsIgnoreCase(Constant.OBS26)) {

				obs26++;
				
			}   else if (ss.equalsIgnoreCase(Constant.OBS27)) {

				obs27++;
				
			}   else if (ss.equalsIgnoreCase(Constant.OBS28)) {

				obs28++;
				
			}   else if (ss.equalsIgnoreCase(Constant.OBS29)) {

				obs29++;
				
			}  else if (ss.equalsIgnoreCase(Constant.OBS30)) {

				obs30++;
				
			}  else if (ss.equalsIgnoreCase(Constant.OBS31)) {

				obs31++;
				
			}  else if (ss.equalsIgnoreCase(Constant.OBS32)) {

				obs32++;
				
			}  else if (ss.equalsIgnoreCase(Constant.OBS33)) {

				obs33++;
				
			}  else if (ss.equalsIgnoreCase(Constant.OBS34)) {

				obs34++;
				
			}  else if (ss.equalsIgnoreCase(Constant.OBS35)) {

				obs35++;
				
			}  else if (ss.equalsIgnoreCase(Constant.OBS36)) {

				obs36++;
				
			}  else if (ss.equalsIgnoreCase(Constant.OBS37)) {

				obs37++;
				
			}  else if (ss.equalsIgnoreCase(Constant.OBS38)) {

				obs38++;
				
			}  else if (ss.equalsIgnoreCase(Constant.OBS39)) {

				obs39++;
				
			}  else if (ss.equalsIgnoreCase(Constant.OBS40)) {

				obs40++;
				
			}  else if (ss.equalsIgnoreCase(Constant.OBS41)) {

				obs41++;
				
			}  else if (ss.equalsIgnoreCase(Constant.OBS42)) {

				obs42++;
				
			}  else if (ss.equalsIgnoreCase(Constant.OBS43)) {

				obs43++;
				
			}  else if (ss.equalsIgnoreCase(Constant.OBS44)) {

				obs44++;
				
			}  else if (ss.equalsIgnoreCase(Constant.OBS45)) {

				obs45++;
				
			}  else if (ss.equalsIgnoreCase(Constant.OBS46)) {

				obs46++;
				
			}   else if (ss.equalsIgnoreCase(Constant.OBS47)) {

				obs47++;
				
			}   else if (ss.equalsIgnoreCase(Constant.OBS48)) {

				obs48++;
				
			}   else if (ss.equalsIgnoreCase(Constant.OBS49)) {

				obs49++;
				
			}
			
			
			
			// }

			// if (readCount + composeCount == 0) {

			// readCount = 1;
			// composeCount = 1;
			// }

			// opdfList.add(observationIndex++, new Double[] {
			// (double) readCount / (readCount + composeCount),
			// (double) composeCount / (readCount + composeCount) });

			// sequenceList.add(list);

		}

//		if (readCount + composeCount == 0) {

//			readCount = 1;
//			composeCount = 1;
//		}
		
		if (obs0 + obs1 + obs2 + obs3 + obs4 + obs5 + obs6 + obs7 + obs8 + obs9 + obs10 + obs11 + obs12 + obs13 + obs14 + obs15 + obs16 + obs17 + obs18 + obs19 + obs20 
			+ obs21 + obs22 + obs23 + obs24 + obs25 + obs26 + obs27 + obs28 + obs29	+ obs30 + obs31 + obs32 + obs33 + obs34 + obs35 + obs36 + obs37 + obs38 + obs39 + obs40 
			+ obs41 + obs42 + obs43 + obs44 + obs45 + obs46 + obs47 + obs48 + obs49 == 0) {
			
			obs0 = 1;
			obs1 = 1;
			obs2 = 1;
			obs3 = 1;
			obs4 = 1;
			obs5 = 1;
			obs6 = 1;
			obs7 = 1;
			obs8 = 1;
			obs9 = 1;
			obs10 = 1;
			
			obs11 = 1;
			obs12 = 1;
			obs13 = 1;
			obs14 = 1;
			obs15 = 1;
			obs16 = 1;
			obs17 = 1;
			obs18 = 1;
			obs19 = 1;
			obs20 = 1;
			
			obs21 = 1;
			obs22 = 1;
			obs23 = 1;
			obs24 = 1;
			obs25 = 1;
			obs26 = 1;
			obs27 = 1;
			obs28 = 1;
			obs29 = 1;
			
			obs30 = 1;
			
			obs31 = 1;
			obs32 = 1;
			obs33 = 1;
			obs34 = 1;
			obs35 = 1;
			obs36 = 1;
			obs37 = 1;
			obs38 = 1;
			obs39 = 1;
			obs40 = 1;
			
			obs41 = 1;
			obs42 = 1;
			obs43 = 1;
			obs44 = 1;
			obs45 = 1;
			obs46 = 1;
			obs47 = 1;
			obs48 = 1;
			obs49 = 1;
			
		
			
    	}

//		opdfList.add(new Double[] {
//				(double) readCount / (readCount + composeCount),
//				(double) composeCount / (readCount + composeCount) });

		if (useEmissionFromFile == false) {
			
			for (int i = 0; i < numberOfStates; i++) {
		
		opdfList[i][0] = (((double) obs0) / (obs0 + obs1 + obs2 + obs3 + obs4 + obs5 + obs6 + obs7 + obs8 + obs9 + obs10 + obs11 + obs12 + obs13 + obs14 + obs15 + obs16 + obs17 + obs18 + obs19 + obs20 + obs21 + obs22 + obs23 + obs24 + obs25 + obs26 + obs27 + obs28 + obs29 + + obs30 + obs31 + obs32 + obs33 + obs34 + obs35 + obs36 + obs37 + obs38 + obs39 + obs40 + obs41 + obs42 + obs43 + obs44 + obs45 + obs46 + obs47 + obs48 + obs49));
		
		opdfList[i][1] = (((double) obs1) / (obs0 + obs1 + obs2 + obs3 + obs4 + obs5 + obs6 + obs7 + obs8 + obs9 + obs10 + obs11 + obs12 + obs13 + obs14 + obs15 + obs16 + obs17 + obs18 + obs19 + obs20 + obs21 + obs22 + obs23 + obs24 + obs25 + obs26 + obs27 + obs28 + obs29 + + obs30 + obs31 + obs32 + obs33 + obs34 + obs35 + obs36 + obs37 + obs38 + obs39 + obs40 + obs41 + obs42 + obs43 + obs44 + obs45 + obs46 + obs47 + obs48 + obs49));
		
		opdfList[i][2] = (((double) obs2) / (obs0 + obs1 + obs2 + obs3 + obs4 + obs5 + obs6 + obs7 + obs8 + obs9 + obs10 + obs11 + obs12 + obs13 + obs14 + obs15 + obs16 + obs17 + obs18 + obs19 + obs20 + obs21 + obs22 + obs23 + obs24 + obs25 + obs26 + obs27 + obs28 + obs29 + + obs30 + obs31 + obs32 + obs33 + obs34 + obs35 + obs36 + obs37 + obs38 + obs39 + obs40 + obs41 + obs42 + obs43 + obs44 + obs45 + obs46 + obs47 + obs48 + obs49));
		
		opdfList[i][3] = (((double) obs3) / (obs0 + obs1 + obs2 + obs3 + obs4 + obs5 + obs6 + obs7 + obs8 + obs9 + obs10 + obs11 + obs12 + obs13 + obs14 + obs15 + obs16 + obs17 + obs18 + obs19 + obs20 + obs21 + obs22 + obs23 + obs24 + obs25 + obs26 + obs27 + obs28 + obs29 + + obs30 + obs31 + obs32 + obs33 + obs34 + obs35 + obs36 + obs37 + obs38 + obs39 + obs40 + obs41 + obs42 + obs43 + obs44 + obs45 + obs46 + obs47 + obs48 + obs49));

		opdfList[i][4] = (((double) obs4) / (obs0 + obs1 + obs2 + obs3 + obs4 + obs5 + obs6 + obs7 + obs8 + obs9 + obs10 + obs11 + obs12 + obs13 + obs14 + obs15 + obs16 + obs17 + obs18 + obs19 + obs20 + obs21 + obs22 + obs23 + obs24 + obs25 + obs26 + obs27 + obs28 + obs29 + + obs30 + obs31 + obs32 + obs33 + obs34 + obs35 + obs36 + obs37 + obs38 + obs39 + obs40 + obs41 + obs42 + obs43 + obs44 + obs45 + obs46 + obs47 + obs48 + obs49));
		
		opdfList[i][5] = (((double) obs5) / (obs0 + obs1 + obs2 + obs3 + obs4 + obs5 + obs6 + obs7 + obs8 + obs9 + obs10 + obs11 + obs12 + obs13 + obs14 + obs15 + obs16 + obs17 + obs18 + obs19 + obs20 + obs21 + obs22 + obs23 + obs24 + obs25 + obs26 + obs27 + obs28 + obs29 + + obs30 + obs31 + obs32 + obs33 + obs34 + obs35 + obs36 + obs37 + obs38 + obs39 + obs40 + obs41 + obs42 + obs43 + obs44 + obs45 + obs46 + obs47 + obs48 + obs49));
		
		opdfList[i][6] = (((double) obs6) / (obs0 + obs1 + obs2 + obs3 + obs4 + obs5 + obs6 + obs7 + obs8 + obs9 + obs10 + obs11 + obs12 + obs13 + obs14 + obs15 + obs16 + obs17 + obs18 + obs19 + obs20 + obs21 + obs22 + obs23 + obs24 + obs25 + obs26 + obs27 + obs28 + obs29 + + obs30 + obs31 + obs32 + obs33 + obs34 + obs35 + obs36 + obs37 + obs38 + obs39 + obs40 + obs41 + obs42 + obs43 + obs44 + obs45 + obs46 + obs47 + obs48 + obs49));
		
		opdfList[i][7] = (((double) obs7) / (obs0 + obs1 + obs2 + obs3 + obs4 + obs5 + obs6 + obs7 + obs8 + obs9 + obs10 + obs11 + obs12 + obs13 + obs14 + obs15 + obs16 + obs17 + obs18 + obs19 + obs20 + obs21 + obs22 + obs23 + obs24 + obs25 + obs26 + obs27 + obs28 + obs29 + + obs30 + obs31 + obs32 + obs33 + obs34 + obs35 + obs36 + obs37 + obs38 + obs39 + obs40 + obs41 + obs42 + obs43 + obs44 + obs45 + obs46 + obs47 + obs48 + obs49));
		
		opdfList[i][8] = (((double) obs8) / (obs0 + obs1 + obs2 + obs3 + obs4 + obs5 + obs6 + obs7 + obs8 + obs9 + obs10 + obs11 + obs12 + obs13 + obs14 + obs15 + obs16 + obs17 + obs18 + obs19 + obs20 + obs21 + obs22 + obs23 + obs24 + obs25 + obs26 + obs27 + obs28 + obs29 + + obs30 + obs31 + obs32 + obs33 + obs34 + obs35 + obs36 + obs37 + obs38 + obs39 + obs40 + obs41 + obs42 + obs43 + obs44 + obs45 + obs46 + obs47 + obs48 + obs49));
		
		opdfList[i][9] = (((double) obs9) / (obs0 + obs1 + obs2 + obs3 + obs4 + obs5 + obs6 + obs7 + obs8 + obs9 + obs10 + obs11 + obs12 + obs13 + obs14 + obs15 + obs16 + obs17 + obs18 + obs19 + obs20 + obs21 + obs22 + obs23 + obs24 + obs25 + obs26 + obs27 + obs28 + obs29 + + obs30 + obs31 + obs32 + obs33 + obs34 + obs35 + obs36 + obs37 + obs38 + obs39 + obs40 + obs41 + obs42 + obs43 + obs44 + obs45 + obs46 + obs47 + obs48 + obs49));
		
		opdfList[i][10] = (((double) obs10) / (obs0 + obs1 + obs2 + obs3 + obs4 + obs5 + obs6 + obs7 + obs8 + obs9 + obs10 + obs11 + obs12 + obs13 + obs14 + obs15 + obs16 + obs17 + obs18 + obs19 + obs20 + obs21 + obs22 + obs23 + obs24 + obs25 + obs26 + obs27 + obs28 + obs29 + + obs30 + obs31 + obs32 + obs33 + obs34 + obs35 + obs36 + obs37 + obs38 + obs39 + obs40 + obs41 + obs42 + obs43 + obs44 + obs45 + obs46 + obs47 + obs48 + obs49));
		
		opdfList[i][11] = (((double) obs11) / (obs0 + obs1 + obs2 + obs3 + obs4 + obs5 + obs6 + obs7 + obs8 + obs9 + obs10 + obs11 + obs12 + obs13 + obs14 + obs15 + obs16 + obs17 + obs18 + obs19 + obs20 + obs21 + obs22 + obs23 + obs24 + obs25 + obs26 + obs27 + obs28 + obs29 + + obs30 + obs31 + obs32 + obs33 + obs34 + obs35 + obs36 + obs37 + obs38 + obs39 + obs40 + obs41 + obs42 + obs43 + obs44 + obs45 + obs46 + obs47 + obs48 + obs49));
		
		opdfList[i][12] = (((double) obs12) / (obs0 + obs1 + obs2 + obs3 + obs4 + obs5 + obs6 + obs7 + obs8 + obs9 + obs10 + obs11 + obs12 + obs13 + obs14 + obs15 + obs16 + obs17 + obs18 + obs19 + obs20 + obs21 + obs22 + obs23 + obs24 + obs25 + obs26 + obs27 + obs28 + obs29 + + obs30 + obs31 + obs32 + obs33 + obs34 + obs35 + obs36 + obs37 + obs38 + obs39 + obs40 + obs41 + obs42 + obs43 + obs44 + obs45 + obs46 + obs47 + obs48 + obs49));
		
		opdfList[i][13] = (((double) obs13) / (obs0 + obs1 + obs2 + obs3 + obs4 + obs5 + obs6 + obs7 + obs8 + obs9 + obs10 + obs11 + obs12 + obs13 + obs14 + obs15 + obs16 + obs17 + obs18 + obs19 + obs20 + obs21 + obs22 + obs23 + obs24 + obs25 + obs26 + obs27 + obs28 + obs29 + + obs30 + obs31 + obs32 + obs33 + obs34 + obs35 + obs36 + obs37 + obs38 + obs39 + obs40 + obs41 + obs42 + obs43 + obs44 + obs45 + obs46 + obs47 + obs48 + obs49));
		
		opdfList[i][14] = (((double) obs14) / (obs0 + obs1 + obs2 + obs3 + obs4 + obs5 + obs6 + obs7 + obs8 + obs9 + obs10 + obs11 + obs12 + obs13 + obs14 + obs15 + obs16 + obs17 + obs18 + obs19 + obs20 + obs21 + obs22 + obs23 + obs24 + obs25 + obs26 + obs27 + obs28 + obs29 + + obs30 + obs31 + obs32 + obs33 + obs34 + obs35 + obs36 + obs37 + obs38 + obs39 + obs40 + obs41 + obs42 + obs43 + obs44 + obs45 + obs46 + obs47 + obs48 + obs49));
		
		opdfList[i][15] = (((double) obs15) / (obs0 + obs1 + obs2 + obs3 + obs4 + obs5 + obs6 + obs7 + obs8 + obs9 + obs10 + obs11 + obs12 + obs13 + obs14 + obs15 + obs16 + obs17 + obs18 + obs19 + obs20 + obs21 + obs22 + obs23 + obs24 + obs25 + obs26 + obs27 + obs28 + obs29 + + obs30 + obs31 + obs32 + obs33 + obs34 + obs35 + obs36 + obs37 + obs38 + obs39 + obs40 + obs41 + obs42 + obs43 + obs44 + obs45 + obs46 + obs47 + obs48 + obs49));
		
		opdfList[i][16] = (((double) obs16) / (obs0 + obs1 + obs2 + obs3 + obs4 + obs5 + obs6 + obs7 + obs8 + obs9 + obs10 + obs11 + obs12 + obs13 + obs14 + obs15 + obs16 + obs17 + obs18 + obs19 + obs20 + obs21 + obs22 + obs23 + obs24 + obs25 + obs26 + obs27 + obs28 + obs29 + + obs30 + obs31 + obs32 + obs33 + obs34 + obs35 + obs36 + obs37 + obs38 + obs39 + obs40 + obs41 + obs42 + obs43 + obs44 + obs45 + obs46 + obs47 + obs48 + obs49));
		
		opdfList[i][17] = (((double) obs17) / (obs0 + obs1 + obs2 + obs3 + obs4 + obs5 + obs6 + obs7 + obs8 + obs9 + obs10 + obs11 + obs12 + obs13 + obs14 + obs15 + obs16 + obs17 + obs18 + obs19 + obs20 + obs21 + obs22 + obs23 + obs24 + obs25 + obs26 + obs27 + obs28 + obs29 + + obs30 + obs31 + obs32 + obs33 + obs34 + obs35 + obs36 + obs37 + obs38 + obs39 + obs40 + obs41 + obs42 + obs43 + obs44 + obs45 + obs46 + obs47 + obs48 + obs49));
		
		opdfList[i][18] = (((double) obs18) / (obs0 + obs1 + obs2 + obs3 + obs4 + obs5 + obs6 + obs7 + obs8 + obs9 + obs10 + obs11 + obs12 + obs13 + obs14 + obs15 + obs16 + obs17 + obs18 + obs19 + obs20 + obs21 + obs22 + obs23 + obs24 + obs25 + obs26 + obs27 + obs28 + obs29 + + obs30 + obs31 + obs32 + obs33 + obs34 + obs35 + obs36 + obs37 + obs38 + obs39 + obs40 + obs41 + obs42 + obs43 + obs44 + obs45 + obs46 + obs47 + obs48 + obs49));
		
		opdfList[i][19] = (((double) obs19) / (obs0 + obs1 + obs2 + obs3 + obs4 + obs5 + obs6 + obs7 + obs8 + obs9 + obs10 + obs11 + obs12 + obs13 + obs14 + obs15 + obs16 + obs17 + obs18 + obs19 + obs20 + obs21 + obs22 + obs23 + obs24 + obs25 + obs26 + obs27 + obs28 + obs29 + + obs30 + obs31 + obs32 + obs33 + obs34 + obs35 + obs36 + obs37 + obs38 + obs39 + obs40 + obs41 + obs42 + obs43 + obs44 + obs45 + obs46 + obs47 + obs48 + obs49));
		
		opdfList[i][20] = (((double) obs20) / (obs0 + obs1 + obs2 + obs3 + obs4 + obs5 + obs6 + obs7 + obs8 + obs9 + obs10 + obs11 + obs12 + obs13 + obs14 + obs15 + obs16 + obs17 + obs18 + obs19 + obs20 + obs21 + obs22 + obs23 + obs24 + obs25 + obs26 + obs27 + obs28 + obs29 + + obs30 + obs31 + obs32 + obs33 + obs34 + obs35 + obs36 + obs37 + obs38 + obs39 + obs40 + obs41 + obs42 + obs43 + obs44 + obs45 + obs46 + obs47 + obs48 + obs49));
		
		opdfList[i][21] = (((double) obs21) / (obs0 + obs1 + obs2 + obs3 + obs4 + obs5 + obs6 + obs7 + obs8 + obs9 + obs10 + obs11 + obs12 + obs13 + obs14 + obs15 + obs16 + obs17 + obs18 + obs19 + obs20 + obs21 + obs22 + obs23 + obs24 + obs25 + obs26 + obs27 + obs28 + obs29 + + obs30 + obs31 + obs32 + obs33 + obs34 + obs35 + obs36 + obs37 + obs38 + obs39 + obs40 + obs41 + obs42 + obs43 + obs44 + obs45 + obs46 + obs47 + obs48 + obs49));
		
		opdfList[i][22] = (((double) obs22) / (obs0 + obs1 + obs2 + obs3 + obs4 + obs5 + obs6 + obs7 + obs8 + obs9 + obs10 + obs11 + obs12 + obs13 + obs14 + obs15 + obs16 + obs17 + obs18 + obs19 + obs20 + obs21 + obs22 + obs23 + obs24 + obs25 + obs26 + obs27 + obs28 + obs29 + + obs30 + obs31 + obs32 + obs33 + obs34 + obs35 + obs36 + obs37 + obs38 + obs39 + obs40 + obs41 + obs42 + obs43 + obs44 + obs45 + obs46 + obs47 + obs48 + obs49));
		
		opdfList[i][23] = (((double) obs23) / (obs0 + obs1 + obs2 + obs3 + obs4 + obs5 + obs6 + obs7 + obs8 + obs9 + obs10 + obs11 + obs12 + obs13 + obs14 + obs15 + obs16 + obs17 + obs18 + obs19 + obs20 + obs21 + obs22 + obs23 + obs24 + obs25 + obs26 + obs27 + obs28 + obs29 + + obs30 + obs31 + obs32 + obs33 + obs34 + obs35 + obs36 + obs37 + obs38 + obs39 + obs40 + obs41 + obs42 + obs43 + obs44 + obs45 + obs46 + obs47 + obs48 + obs49));
		
		opdfList[i][24] = (((double) obs24) / (obs0 + obs1 + obs2 + obs3 + obs4 + obs5 + obs6 + obs7 + obs8 + obs9 + obs10 + obs11 + obs12 + obs13 + obs14 + obs15 + obs16 + obs17 + obs18 + obs19 + obs20 + obs21 + obs22 + obs23 + obs24 + obs25 + obs26 + obs27 + obs28 + obs29 + + obs30 + obs31 + obs32 + obs33 + obs34 + obs35 + obs36 + obs37 + obs38 + obs39 + obs40 + obs41 + obs42 + obs43 + obs44 + obs45 + obs46 + obs47 + obs48 + obs49));
		
		opdfList[i][25] = (((double) obs25) / (obs0 + obs1 + obs2 + obs3 + obs4 + obs5 + obs6 + obs7 + obs8 + obs9 + obs10 + obs11 + obs12 + obs13 + obs14 + obs15 + obs16 + obs17 + obs18 + obs19 + obs20 + obs21 + obs22 + obs23 + obs24 + obs25 + obs26 + obs27 + obs28 + obs29 + + obs30 + obs31 + obs32 + obs33 + obs34 + obs35 + obs36 + obs37 + obs38 + obs39 + obs40 + obs41 + obs42 + obs43 + obs44 + obs45 + obs46 + obs47 + obs48 + obs49));
		
		opdfList[i][26] = (((double) obs26) / (obs0 + obs1 + obs2 + obs3 + obs4 + obs5 + obs6 + obs7 + obs8 + obs9 + obs10 + obs11 + obs12 + obs13 + obs14 + obs15 + obs16 + obs17 + obs18 + obs19 + obs20 + obs21 + obs22 + obs23 + obs24 + obs25 + obs26 + obs27 + obs28 + obs29 + + obs30 + obs31 + obs32 + obs33 + obs34 + obs35 + obs36 + obs37 + obs38 + obs39 + obs40 + obs41 + obs42 + obs43 + obs44 + obs45 + obs46 + obs47 + obs48 + obs49));
		
		opdfList[i][27] = (((double) obs27) / (obs0 + obs1 + obs2 + obs3 + obs4 + obs5 + obs6 + obs7 + obs8 + obs9 + obs10 + obs11 + obs12 + obs13 + obs14 + obs15 + obs16 + obs17 + obs18 + obs19 + obs20 + obs21 + obs22 + obs23 + obs24 + obs25 + obs26 + obs27 + obs28 + obs29 + + obs30 + obs31 + obs32 + obs33 + obs34 + obs35 + obs36 + obs37 + obs38 + obs39 + obs40 + obs41 + obs42 + obs43 + obs44 + obs45 + obs46 + obs47 + obs48 + obs49));
		
		opdfList[i][28] = (((double) obs28) / (obs0 + obs1 + obs2 + obs3 + obs4 + obs5 + obs6 + obs7 + obs8 + obs9 + obs10 + obs11 + obs12 + obs13 + obs14 + obs15 + obs16 + obs17 + obs18 + obs19 + obs20 + obs21 + obs22 + obs23 + obs24 + obs25 + obs26 + obs27 + obs28 + obs29 + + obs30 + obs31 + obs32 + obs33 + obs34 + obs35 + obs36 + obs37 + obs38 + obs39 + obs40 + obs41 + obs42 + obs43 + obs44 + obs45 + obs46 + obs47 + obs48 + obs49));
		
		opdfList[i][29] = (((double) obs29) / (obs0 + obs1 + obs2 + obs3 + obs4 + obs5 + obs6 + obs7 + obs8 + obs9 + obs10 + obs11 + obs12 + obs13 + obs14 + obs15 + obs16 + obs17 + obs18 + obs19 + obs20 + obs21 + obs22 + obs23 + obs24 + obs25 + obs26 + obs27 + obs28 + obs29 + + obs30 + obs31 + obs32 + obs33 + obs34 + obs35 + obs36 + obs37 + obs38 + obs39 + obs40 + obs41 + obs42 + obs43 + obs44 + obs45 + obs46 + obs47 + obs48 + obs49));
		
		opdfList[i][30] = (((double) obs30) / (obs0 + obs1 + obs2 + obs3 + obs4 + obs5 + obs6 + obs7 + obs8 + obs9 + obs10 + obs11 + obs12 + obs13 + obs14 + obs15 + obs16 + obs17 + obs18 + obs19 + obs20 + obs21 + obs22 + obs23 + obs24 + obs25 + obs26 + obs27 + obs28 + obs29 + + obs30 + obs31 + obs32 + obs33 + obs34 + obs35 + obs36 + obs37 + obs38 + obs39 + obs40 + obs41 + obs42 + obs43 + obs44 + obs45 + obs46 + obs47 + obs48 + obs49));
		
		opdfList[i][31] = (((double) obs31) / (obs0 + obs1 + obs2 + obs3 + obs4 + obs5 + obs6 + obs7 + obs8 + obs9 + obs10 + obs11 + obs12 + obs13 + obs14 + obs15 + obs16 + obs17 + obs18 + obs19 + obs20 + obs21 + obs22 + obs23 + obs24 + obs25 + obs26 + obs27 + obs28 + obs29 + + obs30 + obs31 + obs32 + obs33 + obs34 + obs35 + obs36 + obs37 + obs38 + obs39 + obs40 + obs41 + obs42 + obs43 + obs44 + obs45 + obs46 + obs47 + obs48 + obs49));
		
		opdfList[i][32] = (((double) obs32) / (obs0 + obs1 + obs2 + obs3 + obs4 + obs5 + obs6 + obs7 + obs8 + obs9 + obs10 + obs11 + obs12 + obs13 + obs14 + obs15 + obs16 + obs17 + obs18 + obs19 + obs20 + obs21 + obs22 + obs23 + obs24 + obs25 + obs26 + obs27 + obs28 + obs29 + + obs30 + obs31 + obs32 + obs33 + obs34 + obs35 + obs36 + obs37 + obs38 + obs39 + obs40 + obs41 + obs42 + obs43 + obs44 + obs45 + obs46 + obs47 + obs48 + obs49));
		
		opdfList[i][33] = (((double) obs33) / (obs0 + obs1 + obs2 + obs3 + obs4 + obs5 + obs6 + obs7 + obs8 + obs9 + obs10 + obs11 + obs12 + obs13 + obs14 + obs15 + obs16 + obs17 + obs18 + obs19 + obs20 + obs21 + obs22 + obs23 + obs24 + obs25 + obs26 + obs27 + obs28 + obs29 + + obs30 + obs31 + obs32 + obs33 + obs34 + obs35 + obs36 + obs37 + obs38 + obs39 + obs40 + obs41 + obs42 + obs43 + obs44 + obs45 + obs46 + obs47 + obs48 + obs49));
		
		opdfList[i][34] = (((double) obs34) / (obs0 + obs1 + obs2 + obs3 + obs4 + obs5 + obs6 + obs7 + obs8 + obs9 + obs10 + obs11 + obs12 + obs13 + obs14 + obs15 + obs16 + obs17 + obs18 + obs19 + obs20 + obs21 + obs22 + obs23 + obs24 + obs25 + obs26 + obs27 + obs28 + obs29 + + obs30 + obs31 + obs32 + obs33 + obs34 + obs35 + obs36 + obs37 + obs38 + obs39 + obs40 + obs41 + obs42 + obs43 + obs44 + obs45 + obs46 + obs47 + obs48 + obs49));
		
		opdfList[i][35] = (((double) obs35) / (obs0 + obs1 + obs2 + obs3 + obs4 + obs5 + obs6 + obs7 + obs8 + obs9 + obs10 + obs11 + obs12 + obs13 + obs14 + obs15 + obs16 + obs17 + obs18 + obs19 + obs20 + obs21 + obs22 + obs23 + obs24 + obs25 + obs26 + obs27 + obs28 + obs29 + + obs30 + obs31 + obs32 + obs33 + obs34 + obs35 + obs36 + obs37 + obs38 + obs39 + obs40 + obs41 + obs42 + obs43 + obs44 + obs45 + obs46 + obs47 + obs48 + obs49));
		
		opdfList[i][36] = (((double) obs36) / (obs0 + obs1 + obs2 + obs3 + obs4 + obs5 + obs6 + obs7 + obs8 + obs9 + obs10 + obs11 + obs12 + obs13 + obs14 + obs15 + obs16 + obs17 + obs18 + obs19 + obs20 + obs21 + obs22 + obs23 + obs24 + obs25 + obs26 + obs27 + obs28 + obs29 + + obs30 + obs31 + obs32 + obs33 + obs34 + obs35 + obs36 + obs37 + obs38 + obs39 + obs40 + obs41 + obs42 + obs43 + obs44 + obs45 + obs46 + obs47 + obs48 + obs49));
		
		opdfList[i][37] = (((double) obs37) / (obs0 + obs1 + obs2 + obs3 + obs4 + obs5 + obs6 + obs7 + obs8 + obs9 + obs10 + obs11 + obs12 + obs13 + obs14 + obs15 + obs16 + obs17 + obs18 + obs19 + obs20 + obs21 + obs22 + obs23 + obs24 + obs25 + obs26 + obs27 + obs28 + obs29 + + obs30 + obs31 + obs32 + obs33 + obs34 + obs35 + obs36 + obs37 + obs38 + obs39 + obs40 + obs41 + obs42 + obs43 + obs44 + obs45 + obs46 + obs47 + obs48 + obs49));
		
		opdfList[i][38] = (((double) obs38) / (obs0 + obs1 + obs2 + obs3 + obs4 + obs5 + obs6 + obs7 + obs8 + obs9 + obs10 + obs11 + obs12 + obs13 + obs14 + obs15 + obs16 + obs17 + obs18 + obs19 + obs20 + obs21 + obs22 + obs23 + obs24 + obs25 + obs26 + obs27 + obs28 + obs29 + + obs30 + obs31 + obs32 + obs33 + obs34 + obs35 + obs36 + obs37 + obs38 + obs39 + obs40 + obs41 + obs42 + obs43 + obs44 + obs45 + obs46 + obs47 + obs48 + obs49));
		
		opdfList[i][39] = (((double) obs39) / (obs0 + obs1 + obs2 + obs3 + obs4 + obs5 + obs6 + obs7 + obs8 + obs9 + obs10 + obs11 + obs12 + obs13 + obs14 + obs15 + obs16 + obs17 + obs18 + obs19 + obs20 + obs21 + obs22 + obs23 + obs24 + obs25 + obs26 + obs27 + obs28 + obs29 + + obs30 + obs31 + obs32 + obs33 + obs34 + obs35 + obs36 + obs37 + obs38 + obs39 + obs40 + obs41 + obs42 + obs43 + obs44 + obs45 + obs46 + obs47 + obs48 + obs49));
		
		opdfList[i][40] = (((double) obs40) / (obs0 + obs1 + obs2 + obs3 + obs4 + obs5 + obs6 + obs7 + obs8 + obs9 + obs10 + obs11 + obs12 + obs13 + obs14 + obs15 + obs16 + obs17 + obs18 + obs19 + obs20 + obs21 + obs22 + obs23 + obs24 + obs25 + obs26 + obs27 + obs28 + obs29 + + obs30 + obs31 + obs32 + obs33 + obs34 + obs35 + obs36 + obs37 + obs38 + obs39 + obs40 + obs41 + obs42 + obs43 + obs44 + obs45 + obs46 + obs47 + obs48 + obs49));
		
		opdfList[i][41] = (((double) obs41) / (obs0 + obs1 + obs2 + obs3 + obs4 + obs5 + obs6 + obs7 + obs8 + obs9 + obs10 + obs11 + obs12 + obs13 + obs14 + obs15 + obs16 + obs17 + obs18 + obs19 + obs20 + obs21 + obs22 + obs23 + obs24 + obs25 + obs26 + obs27 + obs28 + obs29 + + obs30 + obs31 + obs32 + obs33 + obs34 + obs35 + obs36 + obs37 + obs38 + obs39 + obs40 + obs41 + obs42 + obs43 + obs44 + obs45 + obs46 + obs47 + obs48 + obs49));
		
		opdfList[i][42] = (((double) obs42) / (obs0 + obs1 + obs2 + obs3 + obs4 + obs5 + obs6 + obs7 + obs8 + obs9 + obs10 + obs11 + obs12 + obs13 + obs14 + obs15 + obs16 + obs17 + obs18 + obs19 + obs20 + obs21 + obs22 + obs23 + obs24 + obs25 + obs26 + obs27 + obs28 + obs29 + + obs30 + obs31 + obs32 + obs33 + obs34 + obs35 + obs36 + obs37 + obs38 + obs39 + obs40 + obs41 + obs42 + obs43 + obs44 + obs45 + obs46 + obs47 + obs48 + obs49));
		
		opdfList[i][43] = (((double) obs43) / (obs0 + obs1 + obs2 + obs3 + obs4 + obs5 + obs6 + obs7 + obs8 + obs9 + obs10 + obs11 + obs12 + obs13 + obs14 + obs15 + obs16 + obs17 + obs18 + obs19 + obs20 + obs21 + obs22 + obs23 + obs24 + obs25 + obs26 + obs27 + obs28 + obs29 + + obs30 + obs31 + obs32 + obs33 + obs34 + obs35 + obs36 + obs37 + obs38 + obs39 + obs40 + obs41 + obs42 + obs43 + obs44 + obs45 + obs46 + obs47 + obs48 + obs49));
		
		opdfList[i][44] = (((double) obs44) / (obs0 + obs1 + obs2 + obs3 + obs4 + obs5 + obs6 + obs7 + obs8 + obs9 + obs10 + obs11 + obs12 + obs13 + obs14 + obs15 + obs16 + obs17 + obs18 + obs19 + obs20 + obs21 + obs22 + obs23 + obs24 + obs25 + obs26 + obs27 + obs28 + obs29 + + obs30 + obs31 + obs32 + obs33 + obs34 + obs35 + obs36 + obs37 + obs38 + obs39 + obs40 + obs41 + obs42 + obs43 + obs44 + obs45 + obs46 + obs47 + obs48 + obs49));
		
		opdfList[i][45] = (((double) obs45) / (obs0 + obs1 + obs2 + obs3 + obs4 + obs5 + obs6 + obs7 + obs8 + obs9 + obs10 + obs11 + obs12 + obs13 + obs14 + obs15 + obs16 + obs17 + obs18 + obs19 + obs20 + obs21 + obs22 + obs23 + obs24 + obs25 + obs26 + obs27 + obs28 + obs29 + + obs30 + obs31 + obs32 + obs33 + obs34 + obs35 + obs36 + obs37 + obs38 + obs39 + obs40 + obs41 + obs42 + obs43 + obs44 + obs45 + obs46 + obs47 + obs48 + obs49));
		
		opdfList[i][46] = (((double) obs46) / (obs0 + obs1 + obs2 + obs3 + obs4 + obs5 + obs6 + obs7 + obs8 + obs9 + obs10 + obs11 + obs12 + obs13 + obs14 + obs15 + obs16 + obs17 + obs18 + obs19 + obs20 + obs21 + obs22 + obs23 + obs24 + obs25 + obs26 + obs27 + obs28 + obs29 + + obs30 + obs31 + obs32 + obs33 + obs34 + obs35 + obs36 + obs37 + obs38 + obs39 + obs40 + obs41 + obs42 + obs43 + obs44 + obs45 + obs46 + obs47 + obs48 + obs49));
		
		opdfList[i][47] = (((double) obs47) / (obs0 + obs1 + obs2 + obs3 + obs4 + obs5 + obs6 + obs7 + obs8 + obs9 + obs10 + obs11 + obs12 + obs13 + obs14 + obs15 + obs16 + obs17 + obs18 + obs19 + obs20 + obs21 + obs22 + obs23 + obs24 + obs25 + obs26 + obs27 + obs28 + obs29 + + obs30 + obs31 + obs32 + obs33 + obs34 + obs35 + obs36 + obs37 + obs38 + obs39 + obs40 + obs41 + obs42 + obs43 + obs44 + obs45 + obs46 + obs47 + obs48 + obs49));
		
		opdfList[i][48] = (((double) obs48) / (obs0 + obs1 + obs2 + obs3 + obs4 + obs5 + obs6 + obs7 + obs8 + obs9 + obs10 + obs11 + obs12 + obs13 + obs14 + obs15 + obs16 + obs17 + obs18 + obs19 + obs20 + obs21 + obs22 + obs23 + obs24 + obs25 + obs26 + obs27 + obs28 + obs29 + + obs30 + obs31 + obs32 + obs33 + obs34 + obs35 + obs36 + obs37 + obs38 + obs39 + obs40 + obs41 + obs42 + obs43 + obs44 + obs45 + obs46 + obs47 + obs48 + obs49));
		
		opdfList[i][49] = (((double) obs49) / (obs0 + obs1 + obs2 + obs3 + obs4 + obs5 + obs6 + obs7 + obs8 + obs9 + obs10 + obs11 + obs12 + obs13 + obs14 + obs15 + obs16 + obs17 + obs18 + obs19 + obs20 + obs21 + obs22 + obs23 + obs24 + obs25 + obs26 + obs27 + obs28 + obs29 + + obs30 + obs31 + obs32 + obs33 + obs34 + obs35 + obs36 + obs37 + obs38 + obs39 + obs40 + obs41 + obs42 + obs43 + obs44 + obs45 + obs46 + obs47 + obs48 + obs49));

		
			}
		
		} else {
			
			for (int i = 0; i <emissionProbs.size(); i++) {
				
				List<Double> emissionList = emissionProbs.get(i);
				
				for (int j=0; j < emissionList.size(); j++) {
					
					opdfList[i][j] = emissionList.get(j);
				}
				
			}
		}
		
		
		
//		opdfList[1] = ((double) composeCount / (readCount + composeCount) );
		
		
		sequenceList.add(list);

		for (int i = 0; i < sequenceList.get(0).size(); i++) {

			logger.info("++++++++++++++:" + (sequenceList.get(0).get(i))[0]);
		}

		hmm = HMMCreator.buildHmm7(opdfList, sequenceList, initialStateProbs, stateProbs, numberOfStates, numberOfObservations);
		
//		buildHmm2(
//				double[] opdfList, ArrayList<ArrayList<Object[]>> sequenceList, List<Double> initialStateProbs, Map<Integer, List<Double>> stateProbs, int numberOfStates, int numberOfObservations) 

		logger.info("DONEEEEEEEEEEEEEEEEEEEEEE:" + hmm); 

		// the data table spec of the single output table,
		// the table will have three columns:
/*		DataColumnSpec[] allColSpecs = new DataColumnSpec[3];
		allColSpecs[0] = new DataColumnSpecCreator("Column 0", StringCell.TYPE)
				.createSpec();
		allColSpecs[1] = new DataColumnSpecCreator("Column 1", DoubleCell.TYPE)
				.createSpec();
		allColSpecs[2] = new DataColumnSpecCreator("Column 2", IntCell.TYPE)
				.createSpec();
		DataTableSpec outputSpec = new DataTableSpec(allColSpecs); */
		
		DataColumnSpec[] allColSpecs = new DataColumnSpec[162];
		
		
		allColSpecs[0] = new DataColumnSpecCreator("Pi_0", DoubleCell.TYPE)
			.createSpec();

		allColSpecs[1] = new DataColumnSpecCreator("Pi_1", DoubleCell.TYPE)
			.createSpec();
		
		allColSpecs[2] = new DataColumnSpecCreator("Pi_2", DoubleCell.TYPE)
		.createSpec();

		
		allColSpecs[3] = new DataColumnSpecCreator("s0->s0", DoubleCell.TYPE)
			.createSpec();

		allColSpecs[4] = new DataColumnSpecCreator("s0->s1", DoubleCell.TYPE)
			.createSpec();

		allColSpecs[5] = new DataColumnSpecCreator("s0->s2", DoubleCell.TYPE)
			.createSpec();
		
		allColSpecs[6] = new DataColumnSpecCreator("s1->s0", DoubleCell.TYPE)
			.createSpec();

		allColSpecs[7] = new DataColumnSpecCreator("s1->s1", DoubleCell.TYPE)
			.createSpec();

		allColSpecs[8] = new DataColumnSpecCreator("s1->s2", DoubleCell.TYPE)
			.createSpec();
		
		
		allColSpecs[9] = new DataColumnSpecCreator("s2->s0", DoubleCell.TYPE)
			.createSpec();

		allColSpecs[10] = new DataColumnSpecCreator("s2->s1", DoubleCell.TYPE)
			.createSpec();

		allColSpecs[11] = new DataColumnSpecCreator("s2->s2", DoubleCell.TYPE)
			.createSpec();
		
		
		allColSpecs[12] = new DataColumnSpecCreator("s0->o0", DoubleCell.TYPE)
			.createSpec();

		allColSpecs[13] = new DataColumnSpecCreator("s0->o1", DoubleCell.TYPE)
			.createSpec();

		allColSpecs[14] = new DataColumnSpecCreator("s0->o2", DoubleCell.TYPE)
			.createSpec();
		
		allColSpecs[15] = new DataColumnSpecCreator("s0->o3", DoubleCell.TYPE)
		.createSpec();

	allColSpecs[16] = new DataColumnSpecCreator("s0->o4", DoubleCell.TYPE)
		.createSpec();

	allColSpecs[17] = new DataColumnSpecCreator("s0->o5", DoubleCell.TYPE)
		.createSpec();		
		
	allColSpecs[18] = new DataColumnSpecCreator("s0->o6", DoubleCell.TYPE)
	.createSpec();

allColSpecs[19] = new DataColumnSpecCreator("s0->o7", DoubleCell.TYPE)
	.createSpec();

allColSpecs[20] = new DataColumnSpecCreator("s0->o8", DoubleCell.TYPE)
	.createSpec();		
		
allColSpecs[21] = new DataColumnSpecCreator("s0->o9", DoubleCell.TYPE)
.createSpec();

allColSpecs[22] = new DataColumnSpecCreator("s0->o10", DoubleCell.TYPE)
.createSpec();	

allColSpecs[23] = new DataColumnSpecCreator("s0->o11", DoubleCell.TYPE)
.createSpec();

allColSpecs[24] = new DataColumnSpecCreator("s0->o12", DoubleCell.TYPE)
.createSpec();

allColSpecs[25] = new DataColumnSpecCreator("s0->o13", DoubleCell.TYPE)
.createSpec();

allColSpecs[26] = new DataColumnSpecCreator("s0->o14", DoubleCell.TYPE)
.createSpec();

allColSpecs[27] = new DataColumnSpecCreator("s0->o15", DoubleCell.TYPE)
.createSpec();

allColSpecs[28] = new DataColumnSpecCreator("s0->o16", DoubleCell.TYPE)
.createSpec();

allColSpecs[29] = new DataColumnSpecCreator("s0->o17", DoubleCell.TYPE)
.createSpec();

allColSpecs[30] = new DataColumnSpecCreator("s0->o18", DoubleCell.TYPE)
.createSpec();

allColSpecs[31] = new DataColumnSpecCreator("s0->o19", DoubleCell.TYPE)
.createSpec();

allColSpecs[32] = new DataColumnSpecCreator("s0->o20", DoubleCell.TYPE)
.createSpec();

allColSpecs[33] = new DataColumnSpecCreator("s0->o21", DoubleCell.TYPE)
.createSpec();

allColSpecs[34] = new DataColumnSpecCreator("s0->o22", DoubleCell.TYPE)
.createSpec();

allColSpecs[35] = new DataColumnSpecCreator("s0->o23", DoubleCell.TYPE)
.createSpec();

allColSpecs[36] = new DataColumnSpecCreator("s0->o24", DoubleCell.TYPE)
.createSpec();

allColSpecs[37] = new DataColumnSpecCreator("s0->o25", DoubleCell.TYPE)
.createSpec();

allColSpecs[38] = new DataColumnSpecCreator("s0->o26", DoubleCell.TYPE)
.createSpec();

allColSpecs[39] = new DataColumnSpecCreator("s0->o27", DoubleCell.TYPE)
.createSpec();

allColSpecs[40] = new DataColumnSpecCreator("s0->o28", DoubleCell.TYPE)
.createSpec();

allColSpecs[41] = new DataColumnSpecCreator("s0->o29", DoubleCell.TYPE)
.createSpec();

allColSpecs[42] = new DataColumnSpecCreator("s0->o30", DoubleCell.TYPE)
.createSpec();		
	
allColSpecs[43] = new DataColumnSpecCreator("s0->o31", DoubleCell.TYPE)
.createSpec();

allColSpecs[44] = new DataColumnSpecCreator("s0->o32", DoubleCell.TYPE)
.createSpec();	

allColSpecs[45] = new DataColumnSpecCreator("s0->o33", DoubleCell.TYPE)
.createSpec();

allColSpecs[46] = new DataColumnSpecCreator("s0->o34", DoubleCell.TYPE)
.createSpec();

allColSpecs[47] = new DataColumnSpecCreator("s0->o35", DoubleCell.TYPE)
.createSpec();

allColSpecs[48] = new DataColumnSpecCreator("s0->o36", DoubleCell.TYPE)
.createSpec();

allColSpecs[49] = new DataColumnSpecCreator("s0->o37", DoubleCell.TYPE)
.createSpec();

allColSpecs[50] = new DataColumnSpecCreator("s0->o38", DoubleCell.TYPE)
.createSpec();

allColSpecs[51] = new DataColumnSpecCreator("s0->o39", DoubleCell.TYPE)
.createSpec();

allColSpecs[52] = new DataColumnSpecCreator("s0->o40", DoubleCell.TYPE)
.createSpec();

allColSpecs[53] = new DataColumnSpecCreator("s0->o41", DoubleCell.TYPE)
.createSpec();

allColSpecs[54] = new DataColumnSpecCreator("s0->o42", DoubleCell.TYPE)
.createSpec();

allColSpecs[55] = new DataColumnSpecCreator("s0->o43", DoubleCell.TYPE)
.createSpec();

allColSpecs[56] = new DataColumnSpecCreator("s0->o44", DoubleCell.TYPE)
.createSpec();

allColSpecs[57] = new DataColumnSpecCreator("s0->o45", DoubleCell.TYPE)
.createSpec();

allColSpecs[58] = new DataColumnSpecCreator("s0->o46", DoubleCell.TYPE)
.createSpec();

allColSpecs[59] = new DataColumnSpecCreator("s0->o47", DoubleCell.TYPE)
.createSpec();

allColSpecs[60] = new DataColumnSpecCreator("s0->o48", DoubleCell.TYPE)
.createSpec();

allColSpecs[61] = new DataColumnSpecCreator("s0->o49", DoubleCell.TYPE)
.createSpec();



allColSpecs[62] = new DataColumnSpecCreator("s1->o0", DoubleCell.TYPE)
.createSpec();

allColSpecs[63] = new DataColumnSpecCreator("s1->o1", DoubleCell.TYPE)
.createSpec();

allColSpecs[64] = new DataColumnSpecCreator("s1->o2", DoubleCell.TYPE)
.createSpec();

allColSpecs[65] = new DataColumnSpecCreator("s1->o3", DoubleCell.TYPE)
.createSpec();

allColSpecs[66] = new DataColumnSpecCreator("s1->o4", DoubleCell.TYPE)
.createSpec();

allColSpecs[67] = new DataColumnSpecCreator("s1->o5", DoubleCell.TYPE)
.createSpec();		

allColSpecs[68] = new DataColumnSpecCreator("s1->o6", DoubleCell.TYPE)
.createSpec();

allColSpecs[69] = new DataColumnSpecCreator("s1->o7", DoubleCell.TYPE)
.createSpec();

allColSpecs[70] = new DataColumnSpecCreator("s1->o8", DoubleCell.TYPE)
.createSpec();		

allColSpecs[71] = new DataColumnSpecCreator("s1->o9", DoubleCell.TYPE)
.createSpec();

allColSpecs[72] = new DataColumnSpecCreator("s1->o10", DoubleCell.TYPE)
.createSpec();		

allColSpecs[73] = new DataColumnSpecCreator("s1->o11", DoubleCell.TYPE)
.createSpec();

allColSpecs[74] = new DataColumnSpecCreator("s1->o12", DoubleCell.TYPE)
.createSpec();

allColSpecs[75] = new DataColumnSpecCreator("s1->o13", DoubleCell.TYPE)
.createSpec();

allColSpecs[76] = new DataColumnSpecCreator("s1->o14", DoubleCell.TYPE)
.createSpec();

allColSpecs[77] = new DataColumnSpecCreator("s1->o15", DoubleCell.TYPE)
.createSpec();

allColSpecs[78] = new DataColumnSpecCreator("s1->o16", DoubleCell.TYPE)
.createSpec();

allColSpecs[79] = new DataColumnSpecCreator("s1->o17", DoubleCell.TYPE)
.createSpec();

allColSpecs[80] = new DataColumnSpecCreator("s1->o18", DoubleCell.TYPE)
.createSpec();

allColSpecs[81] = new DataColumnSpecCreator("s1->o19", DoubleCell.TYPE)
.createSpec();

allColSpecs[82] = new DataColumnSpecCreator("s1->o20", DoubleCell.TYPE)
.createSpec();


allColSpecs[83] = new DataColumnSpecCreator("s1->o21", DoubleCell.TYPE)
.createSpec();


allColSpecs[84] = new DataColumnSpecCreator("s1->o22", DoubleCell.TYPE)
.createSpec();


allColSpecs[85] = new DataColumnSpecCreator("s1->o23", DoubleCell.TYPE)
.createSpec();


allColSpecs[86] = new DataColumnSpecCreator("s1->o24", DoubleCell.TYPE)
.createSpec();

allColSpecs[87] = new DataColumnSpecCreator("s1->o25", DoubleCell.TYPE)
.createSpec();

allColSpecs[88] = new DataColumnSpecCreator("s1->o26", DoubleCell.TYPE)
.createSpec();

allColSpecs[89] = new DataColumnSpecCreator("s1->o27", DoubleCell.TYPE)
.createSpec();

allColSpecs[90] = new DataColumnSpecCreator("s1->o28", DoubleCell.TYPE)
.createSpec();

allColSpecs[91] = new DataColumnSpecCreator("s1->o29", DoubleCell.TYPE)
.createSpec();

allColSpecs[92] = new DataColumnSpecCreator("s1->o30", DoubleCell.TYPE)
.createSpec();		

allColSpecs[93] = new DataColumnSpecCreator("s1->o31", DoubleCell.TYPE)
.createSpec();

allColSpecs[94] = new DataColumnSpecCreator("s1->o32", DoubleCell.TYPE)
.createSpec();

allColSpecs[95] = new DataColumnSpecCreator("s1->o33", DoubleCell.TYPE)
.createSpec();

allColSpecs[96] = new DataColumnSpecCreator("s1->o34", DoubleCell.TYPE)
.createSpec();

allColSpecs[97] = new DataColumnSpecCreator("s1->o35", DoubleCell.TYPE)
.createSpec();

allColSpecs[98] = new DataColumnSpecCreator("s1->o36", DoubleCell.TYPE)
.createSpec();

allColSpecs[99] = new DataColumnSpecCreator("s1->o37", DoubleCell.TYPE)
.createSpec();

allColSpecs[100] = new DataColumnSpecCreator("s1->o38", DoubleCell.TYPE)
.createSpec();

allColSpecs[101] = new DataColumnSpecCreator("s1->o39", DoubleCell.TYPE)
.createSpec();

allColSpecs[102] = new DataColumnSpecCreator("s1->o40", DoubleCell.TYPE)
.createSpec();


allColSpecs[103] = new DataColumnSpecCreator("s1->o41", DoubleCell.TYPE)
.createSpec();


allColSpecs[104] = new DataColumnSpecCreator("s1->o42", DoubleCell.TYPE)
.createSpec();


allColSpecs[105] = new DataColumnSpecCreator("s1->o43", DoubleCell.TYPE)
.createSpec();


allColSpecs[106] = new DataColumnSpecCreator("s1->o44", DoubleCell.TYPE)
.createSpec();

allColSpecs[107] = new DataColumnSpecCreator("s1->o45", DoubleCell.TYPE)
.createSpec();

allColSpecs[108] = new DataColumnSpecCreator("s1->o46", DoubleCell.TYPE)
.createSpec();

allColSpecs[109] = new DataColumnSpecCreator("s1->o47", DoubleCell.TYPE)
.createSpec();

allColSpecs[110] = new DataColumnSpecCreator("s1->o48", DoubleCell.TYPE)
.createSpec();

allColSpecs[111] = new DataColumnSpecCreator("s1->o49", DoubleCell.TYPE)
.createSpec();





allColSpecs[112] = new DataColumnSpecCreator("s2->o0", DoubleCell.TYPE)
.createSpec();

allColSpecs[113] = new DataColumnSpecCreator("s2->o1", DoubleCell.TYPE)
.createSpec();

allColSpecs[114] = new DataColumnSpecCreator("s2->o2", DoubleCell.TYPE)
.createSpec();

allColSpecs[115] = new DataColumnSpecCreator("s2->o3", DoubleCell.TYPE)
.createSpec();

allColSpecs[116] = new DataColumnSpecCreator("s2->o4", DoubleCell.TYPE)
.createSpec();

allColSpecs[117] = new DataColumnSpecCreator("s2->o5", DoubleCell.TYPE)
.createSpec();		

allColSpecs[118] = new DataColumnSpecCreator("s2->o6", DoubleCell.TYPE)
.createSpec();

allColSpecs[119] = new DataColumnSpecCreator("s2->o7", DoubleCell.TYPE)
.createSpec();

allColSpecs[120] = new DataColumnSpecCreator("s2->o8", DoubleCell.TYPE)
.createSpec();		

allColSpecs[121] = new DataColumnSpecCreator("s2->o9", DoubleCell.TYPE)
.createSpec();

allColSpecs[122] = new DataColumnSpecCreator("s2->o10", DoubleCell.TYPE)
.createSpec();		

allColSpecs[123] = new DataColumnSpecCreator("s2->o11", DoubleCell.TYPE)
.createSpec();	

allColSpecs[124] = new DataColumnSpecCreator("s2->o12", DoubleCell.TYPE)
.createSpec();	

allColSpecs[125] = new DataColumnSpecCreator("s2->o13", DoubleCell.TYPE)
.createSpec();	

allColSpecs[126] = new DataColumnSpecCreator("s2->o14", DoubleCell.TYPE)
.createSpec();	

allColSpecs[127] = new DataColumnSpecCreator("s2->o15", DoubleCell.TYPE)
.createSpec();	

allColSpecs[128] = new DataColumnSpecCreator("s2->o16", DoubleCell.TYPE)
.createSpec();	

allColSpecs[129] = new DataColumnSpecCreator("s2->o17", DoubleCell.TYPE)
.createSpec();	

allColSpecs[130] = new DataColumnSpecCreator("s2->o18", DoubleCell.TYPE)
.createSpec();	

allColSpecs[131] = new DataColumnSpecCreator("s2->o19", DoubleCell.TYPE)
.createSpec();	

allColSpecs[132] = new DataColumnSpecCreator("s2->o20", DoubleCell.TYPE)
.createSpec();	

allColSpecs[133] = new DataColumnSpecCreator("s2->o21", DoubleCell.TYPE)
.createSpec();	

allColSpecs[134] = new DataColumnSpecCreator("s2->o22", DoubleCell.TYPE)
.createSpec();	

allColSpecs[135] = new DataColumnSpecCreator("s2->o23", DoubleCell.TYPE)
.createSpec();	

allColSpecs[136] = new DataColumnSpecCreator("s2->o24", DoubleCell.TYPE)
.createSpec();	

allColSpecs[137] = new DataColumnSpecCreator("s2->o25", DoubleCell.TYPE)
.createSpec();	

allColSpecs[138] = new DataColumnSpecCreator("s2->o26", DoubleCell.TYPE)
.createSpec();	
		
allColSpecs[139] = new DataColumnSpecCreator("s2->o27", DoubleCell.TYPE)
.createSpec();	

allColSpecs[140] = new DataColumnSpecCreator("s2->o28", DoubleCell.TYPE)
.createSpec();	

allColSpecs[141] = new DataColumnSpecCreator("s2->o29", DoubleCell.TYPE)
.createSpec();	

allColSpecs[142] = new DataColumnSpecCreator("s2->o30", DoubleCell.TYPE)
.createSpec();		

allColSpecs[143] = new DataColumnSpecCreator("s2->o31", DoubleCell.TYPE)
.createSpec();	

allColSpecs[144] = new DataColumnSpecCreator("s2->o32", DoubleCell.TYPE)
.createSpec();	

allColSpecs[145] = new DataColumnSpecCreator("s2->o33", DoubleCell.TYPE)
.createSpec();	

allColSpecs[146] = new DataColumnSpecCreator("s2->o34", DoubleCell.TYPE)
.createSpec();	

allColSpecs[147] = new DataColumnSpecCreator("s2->o35", DoubleCell.TYPE)
.createSpec();	

allColSpecs[148] = new DataColumnSpecCreator("s2->o36", DoubleCell.TYPE)
.createSpec();	

allColSpecs[149] = new DataColumnSpecCreator("s2->o37", DoubleCell.TYPE)
.createSpec();	

allColSpecs[150] = new DataColumnSpecCreator("s2->o38", DoubleCell.TYPE)
.createSpec();	

allColSpecs[151] = new DataColumnSpecCreator("s2->o39", DoubleCell.TYPE)
.createSpec();	

allColSpecs[152] = new DataColumnSpecCreator("s2->o40", DoubleCell.TYPE)
.createSpec();	

allColSpecs[153] = new DataColumnSpecCreator("s2->o41", DoubleCell.TYPE)
.createSpec();	

allColSpecs[154] = new DataColumnSpecCreator("s2->o42", DoubleCell.TYPE)
.createSpec();	

allColSpecs[155] = new DataColumnSpecCreator("s2->o43", DoubleCell.TYPE)
.createSpec();	

allColSpecs[156] = new DataColumnSpecCreator("s2->o44", DoubleCell.TYPE)
.createSpec();	

allColSpecs[157] = new DataColumnSpecCreator("s2->o45", DoubleCell.TYPE)
.createSpec();	

allColSpecs[158] = new DataColumnSpecCreator("s2->o46", DoubleCell.TYPE)
.createSpec();	
		
allColSpecs[159] = new DataColumnSpecCreator("s2->o47", DoubleCell.TYPE)
.createSpec();	

allColSpecs[160] = new DataColumnSpecCreator("s2->o48", DoubleCell.TYPE)
.createSpec();	

allColSpecs[161] = new DataColumnSpecCreator("s2->o49", DoubleCell.TYPE)
.createSpec();	
		



		DataTableSpec outputSpec = new DataTableSpec(allColSpecs); 
		
		
		
		// the execution context will provide us with storage capacity, in this
		// case a data container to which we will add rows sequentially
		// Note, this container can also handle arbitrary big data tables, it
		// will buffer to disc if necessary.
		BufferedDataContainer container = exec.createDataContainer(outputSpec);
		
		ObservationDiscrete<Observation50> obd_0 = new ObservationDiscrete<Observation50>(Observation50.OBS_0);
		ObservationDiscrete<Observation50> obd_1 = new ObservationDiscrete<Observation50>(Observation50.OBS_1);
		ObservationDiscrete<Observation50> obd_2 = new ObservationDiscrete<Observation50>(Observation50.OBS_2);
		ObservationDiscrete<Observation50> obd_3 = new ObservationDiscrete<Observation50>(Observation50.OBS_3);
		ObservationDiscrete<Observation50> obd_4 = new ObservationDiscrete<Observation50>(Observation50.OBS_4);
		ObservationDiscrete<Observation50> obd_5 = new ObservationDiscrete<Observation50>(Observation50.OBS_5);
		ObservationDiscrete<Observation50> obd_6 = new ObservationDiscrete<Observation50>(Observation50.OBS_6);
		ObservationDiscrete<Observation50> obd_7 = new ObservationDiscrete<Observation50>(Observation50.OBS_7);
		ObservationDiscrete<Observation50> obd_8 = new ObservationDiscrete<Observation50>(Observation50.OBS_8);
		ObservationDiscrete<Observation50> obd_9 = new ObservationDiscrete<Observation50>(Observation50.OBS_9);
		ObservationDiscrete<Observation50> obd_10 = new ObservationDiscrete<Observation50>(Observation50.OBS_10);
		ObservationDiscrete<Observation50> obd_11 = new ObservationDiscrete<Observation50>(Observation50.OBS_11);
		ObservationDiscrete<Observation50> obd_12 = new ObservationDiscrete<Observation50>(Observation50.OBS_12);
		ObservationDiscrete<Observation50> obd_13 = new ObservationDiscrete<Observation50>(Observation50.OBS_13);
		ObservationDiscrete<Observation50> obd_14 = new ObservationDiscrete<Observation50>(Observation50.OBS_14);
		ObservationDiscrete<Observation50> obd_15 = new ObservationDiscrete<Observation50>(Observation50.OBS_15);
		ObservationDiscrete<Observation50> obd_16 = new ObservationDiscrete<Observation50>(Observation50.OBS_16);
		ObservationDiscrete<Observation50> obd_17 = new ObservationDiscrete<Observation50>(Observation50.OBS_17);
		ObservationDiscrete<Observation50> obd_18 = new ObservationDiscrete<Observation50>(Observation50.OBS_18);
		ObservationDiscrete<Observation50> obd_19 = new ObservationDiscrete<Observation50>(Observation50.OBS_19);
		ObservationDiscrete<Observation50> obd_20 = new ObservationDiscrete<Observation50>(Observation50.OBS_20);
		ObservationDiscrete<Observation50> obd_21 = new ObservationDiscrete<Observation50>(Observation50.OBS_21);
		ObservationDiscrete<Observation50> obd_22 = new ObservationDiscrete<Observation50>(Observation50.OBS_22);
		ObservationDiscrete<Observation50> obd_23 = new ObservationDiscrete<Observation50>(Observation50.OBS_23);
		ObservationDiscrete<Observation50> obd_24 = new ObservationDiscrete<Observation50>(Observation50.OBS_24);
		ObservationDiscrete<Observation50> obd_25 = new ObservationDiscrete<Observation50>(Observation50.OBS_25);
		ObservationDiscrete<Observation50> obd_26 = new ObservationDiscrete<Observation50>(Observation50.OBS_26);
		ObservationDiscrete<Observation50> obd_27 = new ObservationDiscrete<Observation50>(Observation50.OBS_27);
		ObservationDiscrete<Observation50> obd_28 = new ObservationDiscrete<Observation50>(Observation50.OBS_28);
		ObservationDiscrete<Observation50> obd_29 = new ObservationDiscrete<Observation50>(Observation50.OBS_29);
		ObservationDiscrete<Observation50> obd_30 = new ObservationDiscrete<Observation50>(Observation50.OBS_30);
		ObservationDiscrete<Observation50> obd_31 = new ObservationDiscrete<Observation50>(Observation50.OBS_31);
		ObservationDiscrete<Observation50> obd_32 = new ObservationDiscrete<Observation50>(Observation50.OBS_32);
		ObservationDiscrete<Observation50> obd_33 = new ObservationDiscrete<Observation50>(Observation50.OBS_33);
		ObservationDiscrete<Observation50> obd_34 = new ObservationDiscrete<Observation50>(Observation50.OBS_34);
		ObservationDiscrete<Observation50> obd_35 = new ObservationDiscrete<Observation50>(Observation50.OBS_35);
		ObservationDiscrete<Observation50> obd_36 = new ObservationDiscrete<Observation50>(Observation50.OBS_36);
		ObservationDiscrete<Observation50> obd_37 = new ObservationDiscrete<Observation50>(Observation50.OBS_37);
		ObservationDiscrete<Observation50> obd_38 = new ObservationDiscrete<Observation50>(Observation50.OBS_38);
		ObservationDiscrete<Observation50> obd_39 = new ObservationDiscrete<Observation50>(Observation50.OBS_39);
		ObservationDiscrete<Observation50> obd_40 = new ObservationDiscrete<Observation50>(Observation50.OBS_40);
		ObservationDiscrete<Observation50> obd_41 = new ObservationDiscrete<Observation50>(Observation50.OBS_41);
		ObservationDiscrete<Observation50> obd_42 = new ObservationDiscrete<Observation50>(Observation50.OBS_42);
		ObservationDiscrete<Observation50> obd_43 = new ObservationDiscrete<Observation50>(Observation50.OBS_43);
		ObservationDiscrete<Observation50> obd_44 = new ObservationDiscrete<Observation50>(Observation50.OBS_44);
		ObservationDiscrete<Observation50> obd_45 = new ObservationDiscrete<Observation50>(Observation50.OBS_45);
		ObservationDiscrete<Observation50> obd_46 = new ObservationDiscrete<Observation50>(Observation50.OBS_46);
		ObservationDiscrete<Observation50> obd_47 = new ObservationDiscrete<Observation50>(Observation50.OBS_47);
		ObservationDiscrete<Observation50> obd_48 = new ObservationDiscrete<Observation50>(Observation50.OBS_48);
		ObservationDiscrete<Observation50> obd_49 = new ObservationDiscrete<Observation50>(Observation50.OBS_49);

		
		
		RowKey key = new RowKey("HMM");
		// the cells of the current row, the types of the cells must match
		// the column spec (see above)
		DataCell[] cells = new DataCell[162];
	
		cells[0] = new DoubleCell(hmm.getPi(0));
		cells[1] = new DoubleCell(hmm.getPi(1));
		cells[2] = new DoubleCell(hmm.getPi(2));
		cells[3] = new DoubleCell(hmm.getAij(0, 0));
		cells[4] = new DoubleCell(hmm.getAij(0, 1));
		cells[5] = new DoubleCell(hmm.getAij(0, 2));
		
		cells[6] = new DoubleCell(hmm.getAij(1, 0));
		cells[7] = new DoubleCell(hmm.getAij(1, 1));
		cells[8] = new DoubleCell(hmm.getAij(1, 2));

		cells[9] = new DoubleCell(hmm.getAij(2, 0));
		cells[10] = new DoubleCell(hmm.getAij(2, 1));
		cells[11] = new DoubleCell(hmm.getAij(2, 2));

		
		
		cells[12] = new DoubleCell(hmm.getOpdf(0).probability(obd_0));
		cells[13] = new DoubleCell(hmm.getOpdf(0).probability(obd_1));
		cells[14] = new DoubleCell(hmm.getOpdf(0).probability(obd_2));
		cells[15] = new DoubleCell(hmm.getOpdf(0).probability(obd_3));
		cells[16] = new DoubleCell(hmm.getOpdf(0).probability(obd_4));
		cells[17] = new DoubleCell(hmm.getOpdf(0).probability(obd_5));
		cells[18] = new DoubleCell(hmm.getOpdf(0).probability(obd_6));
		cells[19] = new DoubleCell(hmm.getOpdf(0).probability(obd_7));
		cells[20] = new DoubleCell(hmm.getOpdf(0).probability(obd_8));
		cells[21] = new DoubleCell(hmm.getOpdf(0).probability(obd_9));
		cells[22] = new DoubleCell(hmm.getOpdf(0).probability(obd_10));
		

		cells[23] = new DoubleCell(hmm.getOpdf(0).probability(obd_11));
		cells[24] = new DoubleCell(hmm.getOpdf(0).probability(obd_12));
		cells[25] = new DoubleCell(hmm.getOpdf(0).probability(obd_13));
		cells[26] = new DoubleCell(hmm.getOpdf(0).probability(obd_14));
		cells[27] = new DoubleCell(hmm.getOpdf(0).probability(obd_15));
		cells[28] = new DoubleCell(hmm.getOpdf(0).probability(obd_16));
		cells[29] = new DoubleCell(hmm.getOpdf(0).probability(obd_17));
		cells[30] = new DoubleCell(hmm.getOpdf(0).probability(obd_18));
		cells[31] = new DoubleCell(hmm.getOpdf(0).probability(obd_19));
		cells[32] = new DoubleCell(hmm.getOpdf(0).probability(obd_20));		
		

		cells[33] = new DoubleCell(hmm.getOpdf(0).probability(obd_21));
		cells[34] = new DoubleCell(hmm.getOpdf(0).probability(obd_22));
		cells[35] = new DoubleCell(hmm.getOpdf(0).probability(obd_23));
		cells[36] = new DoubleCell(hmm.getOpdf(0).probability(obd_24));
		cells[37] = new DoubleCell(hmm.getOpdf(0).probability(obd_25));
		cells[38] = new DoubleCell(hmm.getOpdf(0).probability(obd_26));
		cells[39] = new DoubleCell(hmm.getOpdf(0).probability(obd_27));
		cells[40] = new DoubleCell(hmm.getOpdf(0).probability(obd_28));
		cells[41] = new DoubleCell(hmm.getOpdf(0).probability(obd_29));
		
		cells[42] = new DoubleCell(hmm.getOpdf(0).probability(obd_30));
		

		cells[43] = new DoubleCell(hmm.getOpdf(0).probability(obd_31));
		cells[44] = new DoubleCell(hmm.getOpdf(0).probability(obd_32));
		cells[45] = new DoubleCell(hmm.getOpdf(0).probability(obd_33));
		cells[46] = new DoubleCell(hmm.getOpdf(0).probability(obd_34));
		cells[47] = new DoubleCell(hmm.getOpdf(0).probability(obd_35));
		cells[48] = new DoubleCell(hmm.getOpdf(0).probability(obd_36));
		cells[49] = new DoubleCell(hmm.getOpdf(0).probability(obd_37));
		cells[50] = new DoubleCell(hmm.getOpdf(0).probability(obd_38));
		cells[51] = new DoubleCell(hmm.getOpdf(0).probability(obd_39));
		cells[52] = new DoubleCell(hmm.getOpdf(0).probability(obd_40));		
		

		cells[53] = new DoubleCell(hmm.getOpdf(0).probability(obd_41));
		cells[54] = new DoubleCell(hmm.getOpdf(0).probability(obd_42));
		cells[55] = new DoubleCell(hmm.getOpdf(0).probability(obd_43));
		cells[56] = new DoubleCell(hmm.getOpdf(0).probability(obd_44));
		cells[57] = new DoubleCell(hmm.getOpdf(0).probability(obd_45));
		cells[58] = new DoubleCell(hmm.getOpdf(0).probability(obd_46));
		cells[59] = new DoubleCell(hmm.getOpdf(0).probability(obd_47));
		cells[60] = new DoubleCell(hmm.getOpdf(0).probability(obd_48));
		cells[61] = new DoubleCell(hmm.getOpdf(0).probability(obd_49));
				
		
		
		
		cells[62] = new DoubleCell(hmm.getOpdf(1).probability(obd_0));
		cells[63] = new DoubleCell(hmm.getOpdf(1).probability(obd_1));
		cells[64] = new DoubleCell(hmm.getOpdf(1).probability(obd_2));
		cells[65] = new DoubleCell(hmm.getOpdf(1).probability(obd_3));
		cells[66] = new DoubleCell(hmm.getOpdf(1).probability(obd_4));
		cells[67] = new DoubleCell(hmm.getOpdf(1).probability(obd_5));
		cells[68] = new DoubleCell(hmm.getOpdf(1).probability(obd_6));
		cells[69] = new DoubleCell(hmm.getOpdf(1).probability(obd_7));
		cells[70] = new DoubleCell(hmm.getOpdf(1).probability(obd_8));
		cells[71] = new DoubleCell(hmm.getOpdf(1).probability(obd_9));
		cells[72] = new DoubleCell(hmm.getOpdf(1).probability(obd_10));
		
		cells[73] = new DoubleCell(hmm.getOpdf(1).probability(obd_11));
		cells[74] = new DoubleCell(hmm.getOpdf(1).probability(obd_12));
		cells[75] = new DoubleCell(hmm.getOpdf(1).probability(obd_13));
		cells[76] = new DoubleCell(hmm.getOpdf(1).probability(obd_14));
		cells[77] = new DoubleCell(hmm.getOpdf(1).probability(obd_15));
		cells[78] = new DoubleCell(hmm.getOpdf(1).probability(obd_16));
		cells[79] = new DoubleCell(hmm.getOpdf(1).probability(obd_17));
		cells[80] = new DoubleCell(hmm.getOpdf(1).probability(obd_18));
		cells[81] = new DoubleCell(hmm.getOpdf(1).probability(obd_19));
		cells[82] = new DoubleCell(hmm.getOpdf(1).probability(obd_20));
		
		cells[83] = new DoubleCell(hmm.getOpdf(1).probability(obd_21));
		cells[84] = new DoubleCell(hmm.getOpdf(1).probability(obd_22));
		cells[85] = new DoubleCell(hmm.getOpdf(1).probability(obd_23));
		cells[86] = new DoubleCell(hmm.getOpdf(1).probability(obd_24));
		cells[87] = new DoubleCell(hmm.getOpdf(1).probability(obd_25));
		cells[88] = new DoubleCell(hmm.getOpdf(1).probability(obd_26));
		cells[89] = new DoubleCell(hmm.getOpdf(1).probability(obd_27));
		cells[90] = new DoubleCell(hmm.getOpdf(1).probability(obd_28));
		cells[91] = new DoubleCell(hmm.getOpdf(1).probability(obd_29));
		
		cells[92] = new DoubleCell(hmm.getOpdf(1).probability(obd_30));
		
		cells[93] = new DoubleCell(hmm.getOpdf(1).probability(obd_31));
		cells[94] = new DoubleCell(hmm.getOpdf(1).probability(obd_32));
		cells[95] = new DoubleCell(hmm.getOpdf(1).probability(obd_33));
		cells[96] = new DoubleCell(hmm.getOpdf(1).probability(obd_34));
		cells[97] = new DoubleCell(hmm.getOpdf(1).probability(obd_35));
		cells[98] = new DoubleCell(hmm.getOpdf(1).probability(obd_36));
		cells[99] = new DoubleCell(hmm.getOpdf(1).probability(obd_37));
		cells[100] = new DoubleCell(hmm.getOpdf(1).probability(obd_38));
		cells[101] = new DoubleCell(hmm.getOpdf(1).probability(obd_39));
		cells[102] = new DoubleCell(hmm.getOpdf(1).probability(obd_40));
		
		cells[103] = new DoubleCell(hmm.getOpdf(1).probability(obd_41));
		cells[104] = new DoubleCell(hmm.getOpdf(1).probability(obd_42));
		cells[105] = new DoubleCell(hmm.getOpdf(1).probability(obd_43));
		cells[106] = new DoubleCell(hmm.getOpdf(1).probability(obd_44));
		cells[107] = new DoubleCell(hmm.getOpdf(1).probability(obd_45));
		cells[108] = new DoubleCell(hmm.getOpdf(1).probability(obd_46));
		cells[109] = new DoubleCell(hmm.getOpdf(1).probability(obd_47));
		cells[110] = new DoubleCell(hmm.getOpdf(1).probability(obd_48));
		cells[111] = new DoubleCell(hmm.getOpdf(1).probability(obd_49));
		
		
		
		
		
		
		cells[112] = new DoubleCell(hmm.getOpdf(2).probability(obd_0));
		cells[113] = new DoubleCell(hmm.getOpdf(2).probability(obd_1));
		cells[114] = new DoubleCell(hmm.getOpdf(2).probability(obd_2));
		cells[115] = new DoubleCell(hmm.getOpdf(2).probability(obd_3));
		cells[116] = new DoubleCell(hmm.getOpdf(2).probability(obd_4));
		cells[117] = new DoubleCell(hmm.getOpdf(2).probability(obd_5));
		cells[118] = new DoubleCell(hmm.getOpdf(2).probability(obd_6));
		cells[119] = new DoubleCell(hmm.getOpdf(2).probability(obd_7));
		cells[120] = new DoubleCell(hmm.getOpdf(2).probability(obd_8));
		cells[121] = new DoubleCell(hmm.getOpdf(2).probability(obd_9));
		cells[122] = new DoubleCell(hmm.getOpdf(2).probability(obd_10));	
		
		cells[123] = new DoubleCell(hmm.getOpdf(2).probability(obd_11));
		cells[124] = new DoubleCell(hmm.getOpdf(2).probability(obd_12));
		cells[125] = new DoubleCell(hmm.getOpdf(2).probability(obd_13));
		cells[126] = new DoubleCell(hmm.getOpdf(2).probability(obd_14));
		cells[127] = new DoubleCell(hmm.getOpdf(2).probability(obd_15));
		cells[128] = new DoubleCell(hmm.getOpdf(2).probability(obd_16));
		cells[129] = new DoubleCell(hmm.getOpdf(2).probability(obd_17));
		cells[130] = new DoubleCell(hmm.getOpdf(2).probability(obd_18));
		
		cells[131] = new DoubleCell(hmm.getOpdf(2).probability(obd_19));
		cells[132] = new DoubleCell(hmm.getOpdf(2).probability(obd_20));
		cells[133] = new DoubleCell(hmm.getOpdf(2).probability(obd_21));
		cells[134] = new DoubleCell(hmm.getOpdf(2).probability(obd_22));
		cells[135] = new DoubleCell(hmm.getOpdf(2).probability(obd_23));
		cells[136] = new DoubleCell(hmm.getOpdf(2).probability(obd_24));
		cells[137] = new DoubleCell(hmm.getOpdf(2).probability(obd_25));
		cells[138] = new DoubleCell(hmm.getOpdf(2).probability(obd_26));
		cells[139] = new DoubleCell(hmm.getOpdf(2).probability(obd_27));		
		cells[140] = new DoubleCell(hmm.getOpdf(2).probability(obd_28));		
		cells[141] = new DoubleCell(hmm.getOpdf(2).probability(obd_29));
		
		cells[142] = new DoubleCell(hmm.getOpdf(2).probability(obd_30));	
		
		cells[143] = new DoubleCell(hmm.getOpdf(2).probability(obd_31));
		cells[144] = new DoubleCell(hmm.getOpdf(2).probability(obd_32));
		cells[145] = new DoubleCell(hmm.getOpdf(2).probability(obd_33));
		cells[146] = new DoubleCell(hmm.getOpdf(2).probability(obd_34));
		cells[147] = new DoubleCell(hmm.getOpdf(2).probability(obd_35));
		cells[148] = new DoubleCell(hmm.getOpdf(2).probability(obd_36));
		cells[149] = new DoubleCell(hmm.getOpdf(2).probability(obd_37));
		cells[150] = new DoubleCell(hmm.getOpdf(2).probability(obd_38));
		
		cells[151] = new DoubleCell(hmm.getOpdf(2).probability(obd_39));
		cells[152] = new DoubleCell(hmm.getOpdf(2).probability(obd_40));
		cells[153] = new DoubleCell(hmm.getOpdf(2).probability(obd_41));
		cells[154] = new DoubleCell(hmm.getOpdf(2).probability(obd_42));
		cells[155] = new DoubleCell(hmm.getOpdf(2).probability(obd_43));
		cells[156] = new DoubleCell(hmm.getOpdf(2).probability(obd_44));
		cells[157] = new DoubleCell(hmm.getOpdf(2).probability(obd_45));
		cells[158] = new DoubleCell(hmm.getOpdf(2).probability(obd_46));
		cells[159] = new DoubleCell(hmm.getOpdf(2).probability(obd_47));		
		cells[160] = new DoubleCell(hmm.getOpdf(2).probability(obd_48));		
		cells[161] = new DoubleCell(hmm.getOpdf(2).probability(obd_49));
		
		
 /*       cells[12] = new DoubleCell(hmm.getOpdf(0).probability(obd_0));
		cells[13] = new DoubleCell(hmm.getOpdf(0).probability(obd_1));
		cells[14] = new DoubleCell(hmm.getOpdf(0).probability(obd_2));
		cells[15] = new DoubleCell(hmm.getOpdf(0).probability(obd_3));
	
		
		cells[16] = new DoubleCell(hmm.getOpdf(1).probability(obd_0));
		cells[17] = new DoubleCell(hmm.getOpdf(1).probability(obd_1));
		cells[18] = new DoubleCell(hmm.getOpdf(1).probability(obd_2));
		cells[19] = new DoubleCell(hmm.getOpdf(1).probability(obd_3));

		
		cells[20] = new DoubleCell(hmm.getOpdf(2).probability(obd_0));
		cells[21] = new DoubleCell(hmm.getOpdf(2).probability(obd_1));
		cells[22] = new DoubleCell(hmm.getOpdf(2).probability(obd_2));
		cells[23] = new DoubleCell(hmm.getOpdf(2).probability(obd_3)); */
		
		DataRow row = new DefaultRow(key, cells);
		container.addRowToTable(row);
		
		
		// let's add m_count rows to it
//		for (int i = 0; i < m_count.getIntValue(); i++) {
//			RowKey key = new RowKey("Row " + i);
			// the cells of the current row, the types of the cells must match
			// the column spec (see above)
//			DataCell[] cells = new DataCell[3];
//			cells[0] = new StringCell("String_" + i);
//			cells[1] = new DoubleCell(0.5 * i);
//			cells[2] = new IntCell(i);
//			DataRow row = new DefaultRow(key, cells);
//			container.addRowToTable(row);

			// check if the execution monitor was canceled
//			exec.checkCanceled();
//			exec.setProgress(i / (double) m_count.getIntValue(), "Adding row "
//					+ i);
//		}
		// once we are done, we close the container and return its table
		container.close();
		BufferedDataTable out = container.getTable();
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

