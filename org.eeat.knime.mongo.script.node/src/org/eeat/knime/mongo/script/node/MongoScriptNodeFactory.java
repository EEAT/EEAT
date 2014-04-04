package org.eeat.knime.mongo.script.node;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

/**
 * <code>NodeFactory</code> for the "Mongo" Node. big query node
 * 
 * @author
 */
public class MongoScriptNodeFactory extends NodeFactory<MongoScriptNodeModel> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public NodeDialogPane createNodeDialogPane() {
		return new MongoScriptNodeDialog();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MongoScriptNodeModel createNodeModel() {
		return new MongoScriptNodeModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public NodeView<MongoScriptNodeModel> createNodeView(final int viewIndex,
			final MongoScriptNodeModel nodeModel) {
		return new MongoScriptNodeView(nodeModel);
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
	public boolean hasDialog() {
		return true;
	}

}
