package org.eeat.knime.snlp.node;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
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
	private static final NodeLogger logger = NodeLogger.getLogger(SnlpNodeModel.class);
	public final String LOGGER = "logger";
	// Stanford parser parameters. 
	protected String taggerPath = "edu/stanford/nlp/models/pos-tagger/english-left3words/english-left3words-distsim.tagger";
	protected final String[] outputColumnNames = { "Rel", "Gov", "GovIdx", "GovTag", "Dep", "DepIdx", "DepTag", "SentNum", "RowNum" };
	// TODO Consider adding multiple parsers for user selection.
	static public final String[] parserTypes = {"Dependency", "ShiftReduce"};

	

	protected final SettingsModelString userParserType = new SettingsModelString(
			CFG_PARSER_TYPE, null);

	/**
	 * Constructor for the node model.
	 */
	protected SnlpNodeModel() {

		// one incoming port and one outgoing port
		super(1, 1);
	}
	

	protected BufferedDataTable collectSnlpResults(final List<List<String>> relations,
			final ExecutionContext exec) throws CanceledExecutionException {
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
	protected DataTableSpec[] configure(final DataTableSpec[] inSpecs)
			throws InvalidSettingsException {

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

	protected List<String> dataTableToDataList(final BufferedDataTable inData) {
		logger.debug("Converting data rows: " + inData.getRowCount());
		if (inData.getDataTableSpec().getNumColumns() > 1) {
			logger.warn("Only the first column will be processed.");
		}
		List<String> list = new ArrayList<String>();
		for (DataRow row : inData) {
			list.add(row.getCell(0).toString());
		}
		return list;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected BufferedDataTable[] execute(final BufferedDataTable[] inData,
			final ExecutionContext exec) throws Exception {

		BufferedDataTable out = processWithSnlp(inData[0], exec);
		return new BufferedDataTable[] { out };
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
	protected void loadValidatedSettingsFrom(final NodeSettingsRO settings)
			throws InvalidSettingsException {
		userParserType.loadSettingsFrom(settings);
	}

	protected BufferedDataTable processWithSnlp(final BufferedDataTable inData,
			final ExecutionContext exec) throws CanceledExecutionException {
		logger.debug("Begin processing Snlp node.");
		List<List<String>> resultRelations = new ArrayList<List<String>>();
		MaxentTagger tagger = new MaxentTagger(taggerPath);
		DependencyParser parser = DependencyParser.loadFromModelFile(DependencyParser.DEFAULT_MODEL);
		List<String> dataList = dataTableToDataList(inData);
		double listSize = dataList.size();
		int rowNum = 0;
		for (String rowItem : dataList) {
			DocumentPreprocessor tokenizer = new DocumentPreprocessor(new StringReader(rowItem));
			int sentNum = 0;
			for (List<HasWord> sentence : tokenizer) {
				List<TaggedWord> tagged = tagger.tagSentence(sentence);
				GrammaticalStructure gs = parser.predict(tagged);
				for (TypedDependency dep : gs.allTypedDependencies()) {
					ArrayList<String> relation = (ArrayList<String>) formatDepenency(dep);
					relation.add(String.format("%d", sentNum));
					relation.add(String.format("%d", rowNum));
					resultRelations.add(relation);
				}				
				// Print typed dependencies
				logger.debug(gs);
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
		userParserType.saveSettingsTo(settings);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void validateSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
		// Do not actually set any values of any member variables.
		userParserType.validateSettings(settings);

	}

}
