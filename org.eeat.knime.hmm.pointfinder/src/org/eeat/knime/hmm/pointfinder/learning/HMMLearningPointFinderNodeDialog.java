package org.eeat.knime.hmm.pointfinder.learning;

import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponentNumber;
import org.knime.core.node.defaultnodesettings.SettingsModelIntegerBounded;

/**
 * <code>NodeDialog</code> for the "HMMLearningPointFinder" Node.
 * 
 *
 * This node dialog derives from {@link DefaultNodeSettingsPane} which allows
 * creation of a simple dialog with standard components. If you need a more 
 * complex dialog please derive directly from 
 * {@link org.knime.core.node.NodeDialogPane}.
 * 
 * @author 
 */
public class HMMLearningPointFinderNodeDialog extends DefaultNodeSettingsPane {

    /**
     * New pane for configuring HMMLearningPointFinder node dialog.
     * This is just a suggestion to demonstrate possible default dialog
     * components.
     */
    protected HMMLearningPointFinderNodeDialog() {
        super();
        
        addDialogComponent(new DialogComponentNumber(
                new SettingsModelIntegerBounded(
                    HMMLearningPointFinderNodeModel.CFGKEY_COUNT,
                    HMMLearningPointFinderNodeModel.DEFAULT_COUNT,
                    Integer.MIN_VALUE, Integer.MAX_VALUE),
                    "Counter:", /*step*/ 1, /*componentwidth*/ 5));
                    
    }
}

