package org.eeat.knime.hmm.node.so3_50;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

/**
 * <code>NodeFactory</code> for the "HMMNode_3_50" Node.
 * 
 *
 * @author Arash
 */
public class HMMNode_3_50NodeFactory 
        extends NodeFactory<HMMNode_3_50NodeModel> {

    /**
     * {@inheritDoc}
     */
    @Override
    public HMMNode_3_50NodeModel createNodeModel() {
        return new HMMNode_3_50NodeModel();
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
    public NodeView<HMMNode_3_50NodeModel> createNodeView(final int viewIndex,
            final HMMNode_3_50NodeModel nodeModel) {
        return new HMMNode_3_50NodeView(nodeModel);
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
        return new HMMNode_3_50NodeDialog();
    }

}

