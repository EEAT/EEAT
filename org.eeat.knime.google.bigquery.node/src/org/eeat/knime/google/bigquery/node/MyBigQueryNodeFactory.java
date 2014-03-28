package org.eeat.knime.google.bigquery.node;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

/**
 * <code>NodeFactory</code> for the "MyBigQuery" Node.
 * big query node
 *
 * @author 
 */
public class MyBigQueryNodeFactory 
        extends NodeFactory<MyBigQueryNodeModel> {

    /**
     * {@inheritDoc}
     */
    @Override
    public MyBigQueryNodeModel createNodeModel() {
        return new MyBigQueryNodeModel();
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
    public NodeView<MyBigQueryNodeModel> createNodeView(final int viewIndex,
            final MyBigQueryNodeModel nodeModel) {
        return new MyBigQueryNodeView(nodeModel);
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
        return new MyBigQueryNodeDialog();
    }

}

