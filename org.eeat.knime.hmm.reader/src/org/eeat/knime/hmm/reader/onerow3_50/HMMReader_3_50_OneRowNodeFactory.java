package org.eeat.knime.hmm.reader.onerow3_50;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

/**
 * <code>NodeFactory</code> for the "HMMReader_3_50_OneRow" Node.
 * 
 *
 * @author Arash
 */
public class HMMReader_3_50_OneRowNodeFactory 
        extends NodeFactory<HMMReader_3_50_OneRowNodeModel> {

    /**
     * {@inheritDoc}
     */
    @Override
    public HMMReader_3_50_OneRowNodeModel createNodeModel() {
        return new HMMReader_3_50_OneRowNodeModel();
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
    public NodeView<HMMReader_3_50_OneRowNodeModel> createNodeView(final int viewIndex,
            final HMMReader_3_50_OneRowNodeModel nodeModel) {
        return new HMMReader_3_50_OneRowNodeView(nodeModel);
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
        return new HMMReader_3_50_OneRowNodeDialog();
    }

}

