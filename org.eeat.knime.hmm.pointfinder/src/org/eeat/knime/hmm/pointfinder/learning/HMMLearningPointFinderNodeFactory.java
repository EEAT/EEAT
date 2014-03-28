package org.eeat.knime.hmm.pointfinder.learning;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

/**
 * <code>NodeFactory</code> for the "HMMLearningPointFinder" Node.
 * 
 *
 * @author 
 */
public class HMMLearningPointFinderNodeFactory 
        extends NodeFactory<HMMLearningPointFinderNodeModel> {

    /**
     * {@inheritDoc}
     */
    @Override
    public HMMLearningPointFinderNodeModel createNodeModel() {
        return new HMMLearningPointFinderNodeModel();
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
    public NodeView<HMMLearningPointFinderNodeModel> createNodeView(final int viewIndex,
            final HMMLearningPointFinderNodeModel nodeModel) {
        return new HMMLearningPointFinderNodeView(nodeModel);
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
        return new HMMLearningPointFinderNodeDialog();
    }

}

