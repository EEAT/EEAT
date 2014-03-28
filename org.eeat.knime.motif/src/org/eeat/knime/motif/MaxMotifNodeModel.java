package org.eeat.knime.motif;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import motf.DocSet;
import motf.LetterDocSet;
import motf.Log;
import motf.MotFinder;
import motf.MotFinderCL;
import motf.PatSet;
import motf.Pattern;
import motf.PatternComparator;
import motf.PatternDocCountComparator;
import motf.PatternPosCountComparator;
import motf.PatternSizeComparator;

import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.RowKey;
import org.knime.core.data.def.DefaultRow;
import org.knime.core.data.def.DoubleCell;
import org.knime.core.data.def.IntCell;
import org.knime.core.data.def.StringCell;
import org.knime.core.node.BufferedDataContainer;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.defaultnodesettings.SettingsModelDouble;
import org.knime.core.node.defaultnodesettings.SettingsModelInteger;
import org.knime.core.node.defaultnodesettings.SettingsModelString;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;


/**
 * @author EEAT
 */
@SuppressWarnings("unused")
public class MaxMotifNodeModel extends NodeModel {
    private static final NodeLogger logger = NodeLogger.getLogger(MaxMotifNodeModel.class);
    private Map<String, String> codeMap = new HashMap<String, String>();
    private HashMap<String, String> revCodeMap = new HashMap<String, String>();
    private int currCode;
    
	static final String COLSEL = "selCol";
    private final SettingsModelString m_colSel = new SettingsModelString(COLSEL, "none");
    
	static final String MINSUP_COUNT = "minSupCount";
	static final int MINSUP_COUNT_DEFAULT = 30;
    private final SettingsModelInteger m_minSupCount = new SettingsModelInteger(MINSUP_COUNT, MINSUP_COUNT_DEFAULT);
    
	static final String MINSUP_RATIO = "minSupRatio";
	static final double MINSUP_RATIO_DEFAULT = 0.3;
    private final SettingsModelDouble m_minSupRatio = new SettingsModelDouble(MINSUP_RATIO, MINSUP_RATIO_DEFAULT);
    
    static final String MINSUP_SEL = "selMinSup";
    private final SettingsModelString m_minSupSel = new SettingsModelString(MINSUP_SEL, "Count");

	static final String GAP_LEN = "maxGapLen";
	static final int GAP_LEN_DEFAULT = 0;
    private final SettingsModelInteger m_gapLen = new SettingsModelInteger(GAP_LEN, GAP_LEN_DEFAULT);
    
	static final String MAX_PAT_SIZE = "maxPatSize";
	static final int MAX_PAT_SIZE_DEFAULT = 20;
    private final SettingsModelInteger m_maxSize = new SettingsModelInteger(MAX_PAT_SIZE, MAX_PAT_SIZE_DEFAULT);
    
    protected MaxMotifNodeModel() {
        super(1, 1); // one incoming port and one outgoing port
    }

    private void clearVars() {
        codeMap = new HashMap<String, String>();
        revCodeMap = new HashMap<String, String>();
        // TODO: is 255 wildcard in Motf?  skipping it for now (see encode/decode methods)
        // note: * is code 42 and comma is 44, so we avoid having those in our map (output by Motf)
        currCode = 65;
    }
        
    private String encodeSequence(String sequence) {
        logger.debug("encodeSequence begin");
    	// TODO: assumes comma delimited sequence of words (configure this in dialog)
    	StringTokenizer tokenizer = new StringTokenizer(sequence, ",");
    	StringBuilder encoded = new StringBuilder();
    	while (tokenizer.hasMoreTokens()) {
    		String token = tokenizer.nextToken();
    		if (!codeMap.containsKey(token)) {
    			String encodedToken = String.valueOf((char) currCode);
    			codeMap.put(token, encodedToken);
    			revCodeMap.put(encodedToken, token);

    			currCode++;
    			// 255 may be wildcard for Motf
    			if (currCode == 255) {
    				currCode++;
    			}
    		}
    		encoded.append(codeMap.get(token));
    	}
        logger.debug("encodeSequence end");
    	return encoded.toString();    	
    }
    
    private String makeInputData(BufferedDataTable inTable) throws UnsupportedEncodingException, FileNotFoundException {
        logger.debug("makeInputData begin");
		StringBuilder buf = new StringBuilder();
		DataTableSpec spec = inTable.getDataTableSpec();
		// TODO: better validation needed here, for now, just select the first column if not specified
		int colIdx = m_colSel.getStringValue() != null ? 
				spec.findColumnIndex(m_colSel.getStringValue()) : 0;
		for (DataRow row : inTable) {
			// TODO: sequence column should be configured 
			DataCell cell = row.getCell(colIdx);
			String encodedSequence = encodeSequence(cell.toString());
			buf.append(encodedSequence);
			buf.append("\n");
        }
        logger.debug("makeInputData end");
		return buf.toString();
    }
    
    private void configureMotf() {
		boolean usePrintOcc	= false;
		boolean usePrintCount 	= true;

		MotFinder.usePrintOcc = usePrintOcc;
		MotFinder.usePrintCount = usePrintCount;
		// TODO: get this from dialog
		motf.Log.setFile("C:\\tmp\\motf.log");
    }

    private PatSet runMotf(String inputData) throws UnsupportedEncodingException, FileNotFoundException {
        logger.debug("runMotf begin");
    	configureMotf();
		//boolean useCL = true;
		//boolean useStatDocSet = true;
		boolean useDocCount = false;

		logger.debug("runMotf reading DocSet begin");
		DocSet docSet = new LetterDocSet();
		docSet.setUseDocCount(useDocCount);
		StringReader reader = new StringReader(inputData);
		docSet.readFile(reader);
        //OccPool occPool = new OccPool(docSet);
		logger.debug("runMotf reading DocSet end");

		MotFinder finder = new MotFinderCL(docSet); // -gclo
		finder.setUseDocCount(useDocCount);
		//if (useDocCount) { finder.setScorer(new DocScorer(docSet)); }
		finder.setMaxGapLen(m_gapLen.getIntValue() >= 0 ? m_gapLen.getIntValue() : 0);
		finder.setMaxPatSize(m_maxSize.getIntValue());

		logger.debug("find patterns start");
        PatSet patSet = null;
        if (m_minSupSel.getStringValue() == "Ratio" && m_minSupRatio.getDoubleValue() > 0) {
			patSet = finder.run(m_minSupRatio.getDoubleValue());
		} else if (m_minSupCount.getIntValue() > 0) {
			patSet = finder.run(m_minSupCount.getIntValue());
		} else {
			logger.debug("No valid minsup values!");
		}
		logger.debug("find patterns end");

		PatternComparator comp = null;
		boolean useSortByScore = false;
		if (useSortByScore) {
			//Sort by pattern score
			if (useDocCount) {
				comp = new PatternDocCountComparator();
			} else {
				comp = new PatternPosCountComparator();
			}
		} else {
			//Sort by pattern size
			comp = new PatternSizeComparator();
		}
		patSet.sort(comp);

        logger.debug("runMotf end");
		return patSet;
    }
    
    private String decodeString(String encodedString) {
    	// TODO: returns comma delimited sequence of words (should be configured in dialog)
        int len = encodedString.length();
        StringBuilder decodedBuf = new StringBuilder();
        for (int idx = 0; idx < len; idx++) {
        	char ch = encodedString.charAt(idx);
        	if (ch != '*') {
        		decodedBuf.append(revCodeMap.get(String.valueOf(ch)));
        	} else {
        		decodedBuf.append("*");
        	}
        	if (idx != len - 1) {
        		decodedBuf.append(",");
        	}
        }    	
        return decodedBuf.toString();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected BufferedDataTable[] execute(final BufferedDataTable[] inData,
            final ExecutionContext exec) throws Exception {
        logger.debug("execute begin");

        clearVars();
        BufferedDataTable inTable = inData[0];
        String inputData = makeInputData(inTable);

		PatSet patSet = runMotf(inputData);

        DataTableSpec outputSpec = makeTableSpec();
        BufferedDataContainer container = exec.createDataContainer(outputSpec);
        for (int i = 0; i < patSet.size(); i++) {
        	Pattern pattern = patSet.get(i);
            String decodedPattern = decodeString(pattern.getSeqString());
            DataRow row = makeRow(i, pattern, decodedPattern);
            container.addRowToTable(row);
            // check if the execution monitor was canceled
            exec.checkCanceled();
            exec.setProgress(i / (double) patSet.size(), "Adding row " + i);
        }

        container.close();
        BufferedDataTable out = container.getTable();
        logger.debug("execute end");
        return new BufferedDataTable[]{out};
    }

	private DataRow makeRow(int rowNum, Pattern pattern, String decodedPattern) {
		RowKey key = new RowKey(String.valueOf(rowNum));
		DataCell[] cells = new DataCell[4];
		cells[0] = new StringCell(decodedPattern);
		cells[1] = new IntCell(pattern.size());
		cells[2] = new IntCell(pattern.getPosCount());
		cells[3] = new IntCell(pattern.getDocCount());
		DataRow row = new DefaultRow(key, cells);
		return row;
	}

	private DataTableSpec makeTableSpec() {
		DataColumnSpec[] allColSpecs = new DataColumnSpec[4];
        allColSpecs[0] = 
            new DataColumnSpecCreator("Pattern", StringCell.TYPE).createSpec();
        allColSpecs[1] = 
            new DataColumnSpecCreator("Size", IntCell.TYPE).createSpec();
        allColSpecs[2] = 
            new DataColumnSpecCreator("PosCount", IntCell.TYPE).createSpec();
        allColSpecs[3] = 
            new DataColumnSpecCreator("DocCount", IntCell.TYPE).createSpec();
        DataTableSpec outputSpec = new DataTableSpec(allColSpecs);
		return outputSpec;
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
    	m_colSel.saveSettingsTo(settings);
    	m_minSupCount.saveSettingsTo(settings);
    	m_minSupRatio.saveSettingsTo(settings);
    	m_minSupSel.saveSettingsTo(settings);
    	m_gapLen.saveSettingsTo(settings);
    	m_maxSize.saveSettingsTo(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadValidatedSettingsFrom(final NodeSettingsRO settings)
            throws InvalidSettingsException {
    	m_colSel.loadSettingsFrom(settings);
    	m_minSupCount.loadSettingsFrom(settings);
    	m_minSupRatio.loadSettingsFrom(settings);
    	m_minSupSel.loadSettingsFrom(settings);
    	m_gapLen.loadSettingsFrom(settings);
    	m_maxSize.loadSettingsFrom(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validateSettings(final NodeSettingsRO settings)
            throws InvalidSettingsException {
        m_colSel.validateSettings(settings);
        m_minSupCount.validateSettings(settings);
        m_minSupRatio.validateSettings(settings);
        m_minSupSel.validateSettings(settings);
        m_gapLen.validateSettings(settings);
    	m_maxSize.validateSettings(settings);

        SettingsModelString supSel = m_minSupSel.createCloneWithValidatedValue(settings);
        SettingsModelDouble supRatio = m_minSupRatio.createCloneWithValidatedValue(settings);
        SettingsModelInteger supCount = m_minSupCount.createCloneWithValidatedValue(settings);
    	if (supSel.getStringValue() == "Ratio") {
    		if (supRatio.getDoubleValue() <= 0 || supRatio.getDoubleValue() >= 1) {
    			throw new InvalidSettingsException("Minimum support ratio must be between 0 and 1.");
    		}
    	} else if (supSel.getStringValue() == "Count") {
    		if (supCount.getIntValue() <= 0) {
    			throw new InvalidSettingsException("Minimum support count must be greater than 0.");
    		}
    	}
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

