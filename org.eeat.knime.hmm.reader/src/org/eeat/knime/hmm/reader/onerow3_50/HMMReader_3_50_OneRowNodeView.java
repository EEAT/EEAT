package org.eeat.knime.hmm.reader.onerow3_50;

import org.knime.core.node.NodeView;

/**
 * <code>NodeView</code> for the "HMMReader_3_50_OneRow" Node.
 * 
 *
 * @author Arash
 */
public class HMMReader_3_50_OneRowNodeView extends NodeView<HMMReader_3_50_OneRowNodeModel> {

    /**
     * Creates a new view.
     * 
     * @param nodeModel The model (class: {@link HMMReader_3_50_OneRowNodeModel})
     */
    protected HMMReader_3_50_OneRowNodeView(final HMMReader_3_50_OneRowNodeModel nodeModel) {
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
        HMMReader_3_50_OneRowNodeModel nodeModel = 
            (HMMReader_3_50_OneRowNodeModel)getNodeModel();
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

