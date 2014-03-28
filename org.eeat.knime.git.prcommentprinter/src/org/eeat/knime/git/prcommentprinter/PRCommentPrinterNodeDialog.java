package org.eeat.knime.git.prcommentprinter;

import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponentNumber;
import org.knime.core.node.defaultnodesettings.SettingsModelIntegerBounded;

/**
 * <code>NodeDialog</code> for the "PRCommentPrinter" Node.
 * 
 *
 * This node dialog derives from {@link DefaultNodeSettingsPane} which allows
 * creation of a simple dialog with standard components. If you need a more 
 * complex dialog please derive directly from 
 * {@link org.knime.core.node.NodeDialogPane}.
 * 
 * @author 
 */
public class PRCommentPrinterNodeDialog extends DefaultNodeSettingsPane {

    /**
     * New pane for configuring PRCommentPrinter node dialog.
     * This is just a suggestion to demonstrate possible default dialog
     * components.
     */
    protected PRCommentPrinterNodeDialog() {
        super();
        
        addDialogComponent(new DialogComponentNumber(
                new SettingsModelIntegerBounded(
                    PRCommentPrinterNodeModel.CFGKEY_COUNT,
                    PRCommentPrinterNodeModel.DEFAULT_COUNT,
                    Integer.MIN_VALUE, Integer.MAX_VALUE),
                    "Counter:", /*step*/ 1, /*componentwidth*/ 5));
                    
    }
}

