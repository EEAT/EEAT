package org.eeat.knime.motif;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

/**
 * <code>NodeFactory</code> for the "MaxMotif" Node.
 * 
 *
 * @author EEAT
 */
public class MaxMotifNodeFactory 
        extends NodeFactory<MaxMotifNodeModel> {

    /**
     * {@inheritDoc}
     */
    @Override
    public MaxMotifNodeModel createNodeModel() {
        return new MaxMotifNodeModel();
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
    public NodeView<MaxMotifNodeModel> createNodeView(final int viewIndex,
            final MaxMotifNodeModel nodeModel) {
        return new MaxMotifNodeView(nodeModel);
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
        return new MaxMotifNodeDialog();
    }

}

