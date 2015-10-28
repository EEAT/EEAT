package org.eeat.knime.snlp.node;

import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponentNumber;
import org.knime.core.node.defaultnodesettings.SettingsModelInteger;

/**
 * <code>NodeDialog</code> for the "Drools" Node.
 *
 *
 * This node dialog derives from {@link DefaultNodeSettingsPane} which allows creation of a simple
 * dialog with standard components. If you need a more complex dialog please derive directly from
 * {@link org.knime.core.node.NodeDialogPane}.
 *
 * @author wrobinson.cis.gsu.edu
 */
public class SnlpNodeDialog extends DefaultNodeSettingsPane {

	/**
	 * New pane for configuring Drools node dialog. This is just a suggestion to demonstrate
	 * possible default dialog components.
	 */
	protected SnlpNodeDialog() {
		super();

		createNewGroup("Standford parser");
//		final DialogComponentStringSelection snlpConf = new DialogComponentStringSelection(
//				new SettingsModelString(SnlpNodeModel.CFG_PARSER_TYPE, null),
//				"Stanford parser",
//				SnlpNodeModel.parserTypes);
//		snlpConf.setToolTipText("Select the Stanford parser type");
//		addDialogComponent(snlpConf);
        addDialogComponent(new DialogComponentNumber(new SettingsModelInteger(
        		SnlpNodeModel.CFG_MAX_SENTENCE_LENGTH, 4096), "Max sentence length: ", 1));		
	}
}
