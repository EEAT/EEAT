package org.eeat.knime.git.prcommentprinter;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

/**
 * <code>NodeFactory</code> for the "PRCommentPrinter" Node.
 * 
 *
 * @author 
 */
public class PRCommentPrinterNodeFactory 
        extends NodeFactory<PRCommentPrinterNodeModel> {

    /**
     * {@inheritDoc}
     */
    @Override
    public PRCommentPrinterNodeModel createNodeModel() {
        return new PRCommentPrinterNodeModel();
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
    public NodeView<PRCommentPrinterNodeModel> createNodeView(final int viewIndex,
            final PRCommentPrinterNodeModel nodeModel) {
        return new PRCommentPrinterNodeView(nodeModel);
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
        return new PRCommentPrinterNodeDialog();
    }

}

