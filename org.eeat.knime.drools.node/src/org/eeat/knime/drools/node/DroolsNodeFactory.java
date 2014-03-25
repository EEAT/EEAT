package org.eeat.knime.drools.node;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

/**
 * <code>NodeFactory</code> for the "Drools" Node.
 * 
 *
 * @author wrobinson.cis.gsu.edu
 */
public class DroolsNodeFactory 
        extends NodeFactory<DroolsNodeModel> {

    /**
     * {@inheritDoc}
     */
    @Override
    public DroolsNodeModel createNodeModel() {
        return new DroolsNodeModel();
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
    public NodeView<DroolsNodeModel> createNodeView(final int viewIndex,
            final DroolsNodeModel nodeModel) {
        return new DroolsNodeView(nodeModel);
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
        return new DroolsNodeDialog();
    }

}

