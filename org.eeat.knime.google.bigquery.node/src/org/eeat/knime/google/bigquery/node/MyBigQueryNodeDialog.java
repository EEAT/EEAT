package org.eeat.knime.google.bigquery.node;

import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponentNumber;
import org.knime.core.node.defaultnodesettings.SettingsModelIntegerBounded;

/**
 * <code>NodeDialog</code> for the "MyBigQuery" Node.
 * big query node
 *
 * This node dialog derives from {@link DefaultNodeSettingsPane} which allows
 * creation of a simple dialog with standard components. If you need a more 
 * complex dialog please derive directly from 
 * {@link org.knime.core.node.NodeDialogPane}.
 * 
 * @author 
 */
public class MyBigQueryNodeDialog extends DefaultNodeSettingsPane {

    /**
     * New pane for configuring MyBigQuery node dialog.
     * This is just a suggestion to demonstrate possible default dialog
     * components.
     */
    protected MyBigQueryNodeDialog() {
        super();
        
        addDialogComponent(new DialogComponentNumber(
                new SettingsModelIntegerBounded(
                    MyBigQueryNodeModel.CFGKEY_COUNT,
                    MyBigQueryNodeModel.DEFAULT_COUNT,
                    Integer.MIN_VALUE, Integer.MAX_VALUE),
                    "Counter:", /*step*/ 1, /*componentwidth*/ 5));
                    
    }
}

