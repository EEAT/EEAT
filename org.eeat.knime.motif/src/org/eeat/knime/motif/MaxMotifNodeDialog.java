package org.eeat.knime.motif;

import org.knime.core.data.StringValue;
import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponentColumnNameSelection;
import org.knime.core.node.defaultnodesettings.DialogComponentNumber;
import org.knime.core.node.defaultnodesettings.DialogComponentStringSelection;
import org.knime.core.node.defaultnodesettings.SettingsModelDouble;
import org.knime.core.node.defaultnodesettings.SettingsModelInteger;
import org.knime.core.node.defaultnodesettings.SettingsModelString;

/**
 * <code>NodeDialog</code> for the "MaxMotif" Node.
 * 
 *
 * This node dialog derives from {@link DefaultNodeSettingsPane} which allows
 * creation of a simple dialog with standard components. If you need a more 
 * complex dialog please derive directly from 
 * {@link org.knime.core.node.NodeDialogPane}.
 * 
 * @author EEAT
 */
public class MaxMotifNodeDialog extends DefaultNodeSettingsPane {

    /**
     * New pane for configuring MaxMotif node dialog.
     * This is just a suggestion to demonstrate possible default dialog
     * components.
     */
    @SuppressWarnings("unchecked")
	protected MaxMotifNodeDialog() {
        super();
        
//        addDialogComponent(new DialogComponentNumber(
//                new SettingsModelIntegerBounded(
//                    MaxMotifNodeModel.CFGKEY_COUNT,
//                    MaxMotifNodeModel.DEFAULT_COUNT,
//                    Integer.MIN_VALUE, Integer.MAX_VALUE),
//                    "Counter:", /*step*/ 1, /*componentwidth*/ 5));
        addDialogComponent(new DialogComponentColumnNameSelection(
                new SettingsModelString(MaxMotifNodeModel.COLSEL, ""),
                "Select a column", 0, true, false, StringValue.class));
        addDialogComponent(new DialogComponentNumber(
        		new SettingsModelInteger(
        				MaxMotifNodeModel.MINSUP_COUNT,
        				MaxMotifNodeModel.MINSUP_COUNT_DEFAULT),
        				"Min Frequency Threshold Count:", /*step*/ 1, /*componentwidth*/ 4));
        addDialogComponent(new DialogComponentNumber(
        		new SettingsModelDouble(
        				MaxMotifNodeModel.MINSUP_RATIO,
        				MaxMotifNodeModel.MINSUP_RATIO_DEFAULT),
        				"Min Frequency Threshold Ratio:", /*step*/ 0.1, /*componentwidth*/ 4));
        addDialogComponent(new DialogComponentStringSelection(
        		new SettingsModelString(MaxMotifNodeModel.MINSUP_SEL, ""),
        		"Use Minimum Support", "Count", "Ratio"));
        addDialogComponent(new DialogComponentNumber(
        		new SettingsModelInteger(
        				MaxMotifNodeModel.GAP_LEN,
        				MaxMotifNodeModel.GAP_LEN_DEFAULT),
        				"Max gap length (consecutive wildcards):", /*step*/ 1, /*componentwidth*/ 4));
        addDialogComponent(new DialogComponentNumber(
        		new SettingsModelInteger(
        				MaxMotifNodeModel.MAX_PAT_SIZE,
        				MaxMotifNodeModel.MAX_PAT_SIZE_DEFAULT),
        				"Max pattern size:", /*step*/ 1, /*componentwidth*/ 4));
    }
}

