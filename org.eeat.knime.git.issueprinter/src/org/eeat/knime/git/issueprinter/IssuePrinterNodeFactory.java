package org.eeat.knime.git.issueprinter;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

/**
 * <code>NodeFactory</code> for the "IssuePrinter" Node.
 * 
 *
 * @author 
 */
public class IssuePrinterNodeFactory 
        extends NodeFactory<IssuePrinterNodeModel> {

    /**
     * {@inheritDoc}
     */
    @Override
    public IssuePrinterNodeModel createNodeModel() {
        return new IssuePrinterNodeModel();
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
    public NodeView<IssuePrinterNodeModel> createNodeView(final int viewIndex,
            final IssuePrinterNodeModel nodeModel) {
        return new IssuePrinterNodeView(nodeModel);
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
        return new IssuePrinterNodeDialog();
    }

}

