package org.eeat.knime.hmm.node.so3_50;

import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponentNumber;
import org.knime.core.node.defaultnodesettings.SettingsModelIntegerBounded;

/**
 * <code>NodeDialog</code> for the "HMMNode_3_50" Node.
 * 
 *
 * This node dialog derives from {@link DefaultNodeSettingsPane} which allows
 * creation of a simple dialog with standard components. If you need a more 
 * complex dialog please derive directly from 
 * {@link org.knime.core.node.NodeDialogPane}.
 * 
 * @author Arash
 */
public class HMMNode_3_50NodeDialog extends DefaultNodeSettingsPane {

    /**
     * New pane for configuring HMMNode_3_50 node dialog.
     * This is just a suggestion to demonstrate possible default dialog
     * components.
     */
    protected HMMNode_3_50NodeDialog() {
        super();
        
        addDialogComponent(new DialogComponentNumber(
                new SettingsModelIntegerBounded(
                    HMMNode_3_50NodeModel.CFGKEY_COUNT,
                    HMMNode_3_50NodeModel.DEFAULT_COUNT,
                    Integer.MIN_VALUE, Integer.MAX_VALUE),
                    "Counter:", /*step*/ 1, /*componentwidth*/ 5));
                    
    }
}

