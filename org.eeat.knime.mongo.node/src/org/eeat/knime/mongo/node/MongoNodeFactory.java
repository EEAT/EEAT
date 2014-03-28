package org.eeat.knime.mongo.node;

import org.eeat.knime.mongo.node.MongoNodeDialog;
import org.eeat.knime.mongo.node.MongoNodeModel;
import org.eeat.knime.mongo.node.MongoNodeView;
import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

/**
 * <code>NodeFactory</code> for the "Mongo" Node.
 * big query node
 *
 * @author 
 */
public class MongoNodeFactory 
        extends NodeFactory<MongoNodeModel> {

    /**
     * {@inheritDoc}
     */
    @Override
    public MongoNodeModel createNodeModel() {
        return new MongoNodeModel();
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
    public NodeView<MongoNodeModel> createNodeView(final int viewIndex,
            final MongoNodeModel nodeModel) {
        return new MongoNodeView(nodeModel);
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
        return new MongoNodeDialog();
    }

}

