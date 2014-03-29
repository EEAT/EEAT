package org.eeat.knime.mongo.node;

import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponentBoolean;
import org.knime.core.node.defaultnodesettings.DialogComponentNumber;
import org.knime.core.node.defaultnodesettings.DialogComponentString;
import org.knime.core.node.defaultnodesettings.SettingsModelBoolean;
import org.knime.core.node.defaultnodesettings.SettingsModelInteger;
import org.knime.core.node.defaultnodesettings.SettingsModelString;

/**
 * <code>NodeDialog</code> for the "Mongo" Node.
 * big query node
 *
 * This node dialog derives from {@link DefaultNodeSettingsPane} which allows
 * creation of a simple dialog with standard components. If you need a more 
 * complex dialog please derive directly from 
 * {@link org.knime.core.node.NodeDialogPane}.
 * 
 * @author 
 */
public class MongoNodeDialog extends DefaultNodeSettingsPane {

    /**
     * New pane for configuring Mongo node dialog.
     * This is just a suggestion to demonstrate possible default dialog
     * components.
     */
    protected MongoNodeDialog() {
        super();
                
        addDialogComponent(new DialogComponentString(new SettingsModelString(MongoNodeModel.CFG_MONGO_DB, "github"), "Mongo DB name"));
        addDialogComponent(new DialogComponentString(new SettingsModelString(MongoNodeModel.CFG_MONGO_COLL, "users"), "Collection"));
        addDialogComponent(new DialogComponentString(new SettingsModelString(MongoNodeModel.CFG_USER_QUERY, ""), "Mongo collection conditions"));
        addDialogComponent(new DialogComponentBoolean(new SettingsModelBoolean(MongoNodeModel.CFG_MONGO_QUERY_AND,true),"Mongo AND query"));
        addDialogComponent(new DialogComponentBoolean(new SettingsModelBoolean(MongoNodeModel.CFG_MONGO_READONLY,true),"Mongo read only"));
        addDialogComponent(new DialogComponentNumber(new SettingsModelInteger(MongoNodeModel.CFG_MONGO_LIMIT, 100), "Mongo query limit",10));
    }    
    
}

