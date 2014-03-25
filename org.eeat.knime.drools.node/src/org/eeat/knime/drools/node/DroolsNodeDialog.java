package org.eeat.knime.drools.node;

import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponentBoolean;
import org.knime.core.node.defaultnodesettings.DialogComponentFileChooser;
import org.knime.core.node.defaultnodesettings.SettingsModelBoolean;
import org.knime.core.node.defaultnodesettings.SettingsModelString;

/**
 * <code>NodeDialog</code> for the "Drools" Node.
 * 
 *
 * This node dialog derives from {@link DefaultNodeSettingsPane} which allows
 * creation of a simple dialog with standard components. If you need a more 
 * complex dialog please derive directly from 
 * {@link org.knime.core.node.NodeDialogPane}.
 * 
 * @author wrobinson.cis.gsu.edu
 */
public class DroolsNodeDialog extends DefaultNodeSettingsPane {

    /**
     * New pane for configuring Drools node dialog.
     * This is just a suggestion to demonstrate possible default dialog
     * components.
     */
	protected DroolsNodeDialog() {
		super();

		final DialogComponentFileChooser droolsConf = new DialogComponentFileChooser(
				new SettingsModelString(DroolsNodeModel.CFG_USER_FILE_NAME, null),
				DroolsNodeModel.CFG_USER_FILE_NAME, ".drl", ".xml");
		droolsConf.setBorderTitle("Drools rules file:");
		droolsConf.setToolTipText("The Drools rule file.");
		addDialogComponent(droolsConf);
		addDialogComponent(new DialogComponentBoolean(new SettingsModelBoolean(DroolsNodeModel.CFG_INCREMENTAL,true),"Exectute Drools incrementally"));
    }
}

