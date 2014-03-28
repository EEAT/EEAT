package org.eeat.knime.git.commentprinter;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

/**
 * <code>NodeFactory</code> for the "CommentPrinter" Node.
 * 
 *
 * @author 
 */
public class CommentPrinterNodeFactory 
        extends NodeFactory<CommentPrinterNodeModel> {

    /**
     * {@inheritDoc}
     */
    @Override
    public CommentPrinterNodeModel createNodeModel() {
        return new CommentPrinterNodeModel();
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
    public NodeView<CommentPrinterNodeModel> createNodeView(final int viewIndex,
            final CommentPrinterNodeModel nodeModel) {
        return new CommentPrinterNodeView(nodeModel);
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
        return new CommentPrinterNodeDialog();
    }

}

