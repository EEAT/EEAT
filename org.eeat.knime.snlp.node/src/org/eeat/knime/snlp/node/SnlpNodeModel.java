package org.eeat.knime.snlp.node;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
import org.knime.core.node.defaultnodesettings.SettingsModelInteger;
import org.knime.core.node.defaultnodesettings.SettingsModelString;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.parser.nndep.DependencyParser;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.TypedDependency;

/**
 * This model provides an interface to the Stanford parser.
 *
 *
 * @author wrobinson.cis.gsu.edu
 */
public class SnlpNodeModel extends NodeModel {

	static final String CFG_PARSER_TYPE = "snlpParserType";
	static final String CFG_TAGGER_LANGUAGE_TYPE = "snlpTaggerLanguageType";
	private static final NodeLogger logger = NodeLogger.getLogger(SnlpNodeModel.class);
	// TODO Consider adding multiple parsers for user selection.
	static public final String[] parserTypes = { "Dependency", "ShiftReduce" };
	static public final String CFG_MAX_SENTENCE_LENGTH = "snlpMaxSentenceLength";
	static public final String[] taggerLanguageTypes = {"English", "Chinese", "GermanDeWac", "GermanFast", "GermanHgc", "Spanish", "Spanish-DistSim"};
	public final String LOGGER = "logger";
	// Stanford parser parameters.
	protected String taggerEnglishPath = "edu/stanford/nlp/models/pos-tagger/english-left3words/english-left3words-distsim.tagger";
	protected String taggerChinesePath = "edu/stanford/nlp/models/pos-tagger/chinese-distsim/chinese-distsim.tagger";
	protected String taggerGermanDeWacPath = "edu/stanford/nlp/models/pos-tagger/german/german-dewac.tagger";
	protected String taggerGermanFastPath = "edu/stanford/nlp/models/pos-tagger/german/german-fast.tagger";
	protected String taggerGermanHgcPath = "edu/stanford/nlp/models/pos-tagger/german/german-hgc.tagger";
	protected String taggerSpanishDistSimPath = "edu/stanford/nlp/models/pos-tagger/spanish/spanish-distsim/spanish-distsim.tagger";
	protected String taggerSpanishPath = "edu/stanford/nlp/models/pos-tagger/spanish/spanish-distsim/spanish.tagger";
	
	protected String nndepChineseModelPath = "edu/stanford/nlp/models/parser/nndep/CTB_CoNLL_params.txt.gz";
	protected String nndepDefaultModelPath = "edu/stanford/nlp/models/parser/nndep/english_UD.gz";
	protected String nndepStanfordModelPath = "edu/stanford/nlp/models/parser/nndep/PTB_Stanford_params.txt.gz";
	protected String nndepCoNLLModelPath = "edu/stanford/nlp/models/parser/nndep/PTB_CoNLL_params.txt.gz";
	static public final String[] modelTypes = {"English-default", "English-Stanford", "English-CoNLL", "Chinese-CoNLL"};
	static final String CFG_MODEL_TYPE = "snlpModelType";

	protected final String[] outputColumnNames = { "Rel", "Gov", "GovIdx", "GovTag", "Dep", "DepIdx", "DepTag",
			"SentNum", "RowId" };

	protected final SettingsModelString userParserType = new SettingsModelString(CFG_PARSER_TYPE, null);
	
	protected  final SettingsModelString languageType = new SettingsModelString(CFG_TAGGER_LANGUAGE_TYPE, null);
	
	protected  final SettingsModelString modelType = new SettingsModelString(CFG_MODEL_TYPE, null);
	
	protected final SettingsModelInteger  userMaxSentenceLength = new SettingsModelInteger(
			CFG_MAX_SENTENCE_LENGTH, 8192);

	/**
	 * Constructor for the node model.
	 */
	protected SnlpNodeModel() {

		// one incoming port and one outgoing port
		super(1, 1);
	}
	
	protected String taggerPath() {
		String path;
		switch (languageType.getStringValue()) {
		case "English":
			path = taggerEnglishPath;
			break;
		case "Chinese":
			path = taggerChinesePath;
			break;
		case "GermanDeWac":
			path = taggerGermanDeWacPath;
			break;
		case "GermanFast":
			path = taggerGermanFastPath;
			break;
		case "GermanHgc":
			path = taggerGermanHgcPath;
			break;
		case "Spanish":
			path = taggerSpanishPath;
			break;
		case "Spanish-DistSim":
			path = taggerSpanishDistSimPath;
			break;
		default:
			path = taggerEnglishPath;
		}
		return path;
	}
	
	protected String modelPath() {
		String path;
		switch (modelType.getStringValue()) {
		case "English-default":
			path = nndepDefaultModelPath;
			break;
		case "English-Stanford":
			path = nndepStanfordModelPath;
			break;
		case "English-CoNLL":
			path = nndepCoNLLModelPath;
			break;
		case "Chinese-CoNLL":
			path = nndepChineseModelPath;
			break;
		default:
			path = nndepDefaultModelPath;
		}
		return path;
	}

	protected BufferedDataTable collectSnlpResults(final List<List<String>> relations, final ExecutionContext exec)
			throws CanceledExecutionException {
		BufferedDataTable out = null;
		DataTableSpec outputSpec = createTableSpec();
		BufferedDataContainer container = exec.createDataContainer(outputSpec);
		if (relations.size() <= 0)
			logger.warn("Snlp results in empty set.");
		int rowNum = 0;
		for (List<String> relation : relations) {
			container.addRowToTable(createDataRow(relation, rowNum));
			// check if the execution monitor was canceled
			exec.checkCanceled();
			exec.setProgress(rowNum / (double) relations.size(), "Adding row " + rowNum++);
		}
		container.close();
		out = container.getTable();
		return out;
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

	protected DataRow createDataRow(List<String> relation, int rowNum) {
		RowKey key = new RowKey(String.valueOf(rowNum));
		DataCell[] cells = new DataCell[relation.size()];
		for (int i = 0; i < relation.size(); i++) {
			cells[i] = new StringCell((relation.get(i) != null) ? (String) relation.get(i) : "");
		}
		DataRow row = new DefaultRow(key, cells);
		return row;
	}

	protected DataTableSpec createTableSpec() {
		DataColumnSpec[] allColSpecs = new DataColumnSpec[outputColumnNames.length];
		int i = 0;
		for (String name : outputColumnNames) {
			allColSpecs[i++] = new DataColumnSpecCreator(name, StringCell.TYPE).createSpec();
		}
		DataTableSpec outputSpec = new DataTableSpec(allColSpecs);
		return outputSpec;
	}

//	protected List<String> dataTableToDataList(final BufferedDataTable inData) {
//		logger.debug("Converting data rows: " + inData.getRowCount());
//		if (inData.getDataTableSpec().getNumColumns() > 1) {
//			logger.warn("Only the first column will be processed.");
//		}
//		List<String> list = new ArrayList<String>();
//		for (DataRow row : inData) {
//			list.add(row.getCell(0).toString());
//		}
//		return list;
//	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected BufferedDataTable[] execute(final BufferedDataTable[] inData, final ExecutionContext exec)
			throws Exception {

		BufferedDataTable out = processWithSnlp(inData[0], exec);
		return new BufferedDataTable[] { out };
	}

	protected List<String> formatDepenency() {
		List<String> list;
		list = Arrays.asList("", "", "0", "", "", "0", "");
		return new ArrayList<String>(list);
	}

	protected List<String> formatDepenency(TypedDependency dependency) {
		List<String> list;
		list = Arrays.asList(dependency.reln().toString(), dependency.gov().value(),
				String.format("%d", dependency.gov().index()), dependency.gov().tag(), dependency.dep().value(),
				String.format("%d", dependency.dep().index()), dependency.dep().tag());
		return new ArrayList<String>(list);
	}

	public SettingsModelString getUserFileSetting() {
		return userParserType;
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
	protected void loadValidatedSettingsFrom(final NodeSettingsRO settings) throws InvalidSettingsException {
//		userParserType.loadSettingsFrom(settings);
		userMaxSentenceLength.loadSettingsFrom(settings);
		languageType.loadSettingsFrom(settings);
		modelType.loadSettingsFrom(settings);
	}

	protected BufferedDataTable processWithSnlp(final BufferedDataTable inData, final ExecutionContext exec)
			throws CanceledExecutionException {
		logger.debug("Begin processing Snlp node.");
		List<List<String>> resultRelations = new ArrayList<List<String>>();
		MaxentTagger tagger = new MaxentTagger(taggerPath());
//		DependencyParser parser = DependencyParser.loadFromModelFile(DependencyParser.DEFAULT_MODEL);
		DependencyParser parser = DependencyParser.loadFromModelFile(modelPath());
		double listSize = inData.getRowCount();
		int rowNum = 0;
		String rowItem;
		String rowId;
		for (DataRow row : inData) {
		//for (String rowItem : dataList) {
			rowItem = row.getCell(0).toString();
			rowId = row.getKey().toString();
			// Don't process empty rows
			if (rowItem.length()<=0) continue;
			
			// Ensure reasonable character set.			
			String inputString;
//			inputString = new String(rowItem.getBytes(StandardCharsets.US_ASCII));
			inputString = new String(rowItem.getBytes());
			DocumentPreprocessor tokenizer = new DocumentPreprocessor(new StringReader(inputString));
			int sentNum = 0;
			for (List<HasWord> sentence : tokenizer) {
				try {
					logger.debug(String.format("IN: row #%d rowID %s sent#%d, length:%d: %s", rowNum, rowId, sentNum,
							sentence.toString().length(), sentence));
					if (sentence.toString().length() < userMaxSentenceLength.getIntValue()) {
						List<TaggedWord> tagged = tagger.tagSentence(sentence);
						GrammaticalStructure gs = parser.predict(tagged);
						for (TypedDependency dep : gs.allTypedDependencies()) {
							ArrayList<String> relation = (ArrayList<String>) formatDepenency(dep);
							relation.add(String.format("%d", sentNum));
							relation.add(String.format("%s", rowId));
							resultRelations.add(relation);
						}
						// Print typed dependencies
						logger.debug(String.format("OUT: rowId %s sent#%d: %s", rowId, sentNum, gs));
					} else {
						logger.error(String.format("Sentence length execeeds maximum: %d > %d. ",
								sentence.toString().length(), userMaxSentenceLength.getIntValue()));
						// Add empty sentence
						ArrayList<String> relation = (ArrayList<String>) formatDepenency();
						relation.add(String.format("%d", sentNum));
						relation.add(String.format("%s", rowId));
						resultRelations.add(relation);
					}

				} catch (Exception | StackOverflowError e) {
					// In case the above check for bad input doesn't work.
					logger.error(e.toString());
					logger.error("Input: " + inputString);
					// FIX: Nothing hereafter executes after StackOverflowError
					System.gc();
				}
				sentNum++;
			}
			// check if the execution monitor was canceled
			exec.checkCanceled();
			exec.setProgress(rowNum / listSize, "Adding row " + rowNum++);
		}
		BufferedDataTable outTable = collectSnlpResults(resultRelations, exec);
		logger.debug("End processing Snlp node (" + rowNum + " rows).");
		return outTable;
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
	protected void saveInternals(final File internDir, final ExecutionMonitor exec)
			throws IOException, CanceledExecutionException {

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
//		userParserType.saveSettingsTo(settings);
		userMaxSentenceLength.saveSettingsTo(settings);
		languageType.saveSettingsTo(settings);
		modelType.saveSettingsTo(settings);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void validateSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
		// Do not actually set any values of any member variables.
//		userParserType.validateSettings(settings);
		userMaxSentenceLength.validateSettings(settings);
		languageType.validateSettings(settings);
		modelType.validateSettings(settings);

	}

}
