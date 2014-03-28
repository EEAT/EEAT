package org.eeat;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

/**
 * <code>NodeFactory</code> for the "EEATTreeDiff" Node.
 * 
 *
 * @author EEAT
 */
public class EEATTreeDiffNodeFactory 
        extends NodeFactory<EEATTreeDiffNodeModel> {

    /**
     * {@inheritDoc}
     */
    @Override
    public EEATTreeDiffNodeModel createNodeModel() {
        return new EEATTreeDiffNodeModel();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getNrNodeViews() {
        return 1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NodeView<EEATTreeDiffNodeModel> createNodeView(final int viewIndex,
            final EEATTreeDiffNodeModel nodeModel) {
        return new EEATTreeDiffNodeView(nodeModel);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasDialog() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NodeDialogPane createNodeDialogPane() {
        return new EEATTreeDiffNodeDialog();
    }

}

