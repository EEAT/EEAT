package org.eeat.knime.git.prprinter;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

/**
 * <code>NodeFactory</code> for the "PRPrinter" Node.
 * 
 *
 * @author 
 */
public class PRPrinterNodeFactory 
        extends NodeFactory<PRPrinterNodeModel> {

    /**
     * {@inheritDoc}
     */
    @Override
    public PRPrinterNodeModel createNodeModel() {
        return new PRPrinterNodeModel();
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
    public NodeView<PRPrinterNodeModel> createNodeView(final int viewIndex,
            final PRPrinterNodeModel nodeModel) {
        return new PRPrinterNodeView(nodeModel);
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
        return new PRPrinterNodeDialog();
    }

}

