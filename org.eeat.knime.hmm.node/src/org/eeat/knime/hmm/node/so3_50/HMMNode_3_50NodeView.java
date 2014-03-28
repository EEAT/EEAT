package org.eeat.knime.hmm.node.so3_50;

import org.knime.core.node.NodeView;

/**
 * <code>NodeView</code> for the "HMMNode_3_50" Node.
 * 
 *
 * @author Arash
 */
public class HMMNode_3_50NodeView extends NodeView<HMMNode_3_50NodeModel> {

    /**
     * Creates a new view.
     * 
     * @param nodeModel The model (class: {@link HMMNode_3_50NodeModel})
     */
    protected HMMNode_3_50NodeView(final HMMNode_3_50NodeModel nodeModel) {
        super(nodeModel);

        // TODO instantiate the components of the view here.

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void modelChanged() {

        // TODO retrieve the new model from your nodemodel and 
        // update the view.
        HMMNode_3_50NodeModel nodeModel = 
            (HMMNode_3_50NodeModel)getNodeModel();
        assert nodeModel != null;
        
        // be aware of a possibly not executed nodeModel! The data you retrieve
        // from your nodemodel could be null, emtpy, or invalid in any kind.
        
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onClose() {
    
        // TODO things to do when closing the view
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onOpen() {

        // TODO things to do when opening the view
    }

}

