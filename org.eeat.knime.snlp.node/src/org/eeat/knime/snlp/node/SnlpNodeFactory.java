package org.eeat.knime.snlp.node;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;
import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;

/**
 * <code>NodeFactory</code> for the "Drools" Node.
 * 
 *
 * @author wrobinson.cis.gsu.edu
 */
public class SnlpNodeFactory 
        extends NodeFactory<SnlpNodeModel> {

    /**
     * {@inheritDoc}
     */
    @Override
    public SnlpNodeModel createNodeModel() {
        return new SnlpNodeModel();
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
    public NodeView<SnlpNodeModel> createNodeView(final int viewIndex,
            final SnlpNodeModel nodeModel) {
        return new SnlpNodeView(nodeModel);
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
        // TODO Add dialog for multiple parsers
    	// return new SnlpNodeDialog();
        return new DefaultNodeSettingsPane();
    }

}

