package org.eeat.knime.git.issuecommentprinter;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

/**
 * <code>NodeFactory</code> for the "IssueCommentPrinter" Node.
 * 
 *
 * @author 
 */
public class IssueCommentPrinterNodeFactory 
        extends NodeFactory<IssueCommentPrinterNodeModel> {

    /**
     * {@inheritDoc}
     */
    @Override
    public IssueCommentPrinterNodeModel createNodeModel() {
        return new IssueCommentPrinterNodeModel();
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
    public NodeView<IssueCommentPrinterNodeModel> createNodeView(final int viewIndex,
            final IssueCommentPrinterNodeModel nodeModel) {
        return new IssueCommentPrinterNodeView(nodeModel);
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
        return new IssueCommentPrinterNodeDialog();
    }

}

