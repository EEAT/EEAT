package org.eeat.knime.mongo.node;

import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponentBoolean;
import org.knime.core.node.defaultnodesettings.DialogComponentMultiLineString;
import org.knime.core.node.defaultnodesettings.DialogComponentNumber;
import org.knime.core.node.defaultnodesettings.DialogComponentPasswordField;
import org.knime.core.node.defaultnodesettings.DialogComponentString;
import org.knime.core.node.defaultnodesettings.SettingsModelBoolean;
import org.knime.core.node.defaultnodesettings.SettingsModelInteger;
import org.knime.core.node.defaultnodesettings.SettingsModelString;

/**
 * <code>NodeDialog</code> for the "Mongo" Node. big query node
 * 
 * This node dialog derives from {@link DefaultNodeSettingsPane} which allows creation of a simple dialog with
 * standard components. If you need a more complex dialog please derive directly from
 * {@link org.knime.core.node.NodeDialogPane}.
 * 
 * @author
 */
public class MongoNodeDialog extends DefaultNodeSettingsPane {

	/**
	 * New pane for configuring Mongo node dialog. This is just a suggestion to demonstrate possible default
	 * dialog components.
	 */
	protected MongoNodeDialog() {
		super();
		addDialogComponent(new DialogComponentString(new SettingsModelString(MongoNodeModel.CFG_ID,
				"admin"), "ID", false, 10));
		addDialogComponent(new DialogComponentPasswordField(new SettingsModelString(MongoNodeModel.CFG_PASSWORD,
				"1234"), "Password", 10));
		addDialogComponent(new DialogComponentString(new SettingsModelString(MongoNodeModel.CFG_HOST,
				"localhost"), "Host", true, 10));
		addDialogComponent(new DialogComponentNumber(
				new SettingsModelInteger(MongoNodeModel.CFG_PORT, 27017), "Port", 1));
		addDialogComponent(new DialogComponentString(new SettingsModelString(MongoNodeModel.CFG_MONGO_DB,
				"test"), "Database", true, 10));
		addDialogComponent(new DialogComponentString(new SettingsModelString(MongoNodeModel.CFG_MONGO_COLL,
				"users"), "Collection", true, 10));		
		addDialogComponent(new DialogComponentMultiLineString(new SettingsModelString(
				MongoNodeModel.CFG_USER_QUERY, ""), "Query", true, 1, 5));
		addDialogComponent(new DialogComponentBoolean(new SettingsModelBoolean(
				MongoNodeModel.CFG_MONGO_QUERY_FORMAT, false), "Mongo query langauge"));
		addDialogComponent(new DialogComponentBoolean(new SettingsModelBoolean(
				MongoNodeModel.CFG_MONGO_QUERY_AND, true), "AND query"));
		addDialogComponent(new DialogComponentBoolean(new SettingsModelBoolean(
				MongoNodeModel.CFG_MONGO_SECONDARY, true), "Secondary preferred"));
		addDialogComponent(new DialogComponentNumber(new SettingsModelInteger(MongoNodeModel.CFG_MONGO_LIMIT,
				100), "Query limit", 10));
		addDialogComponent(new DialogComponentNumber(new SettingsModelInteger(MongoNodeModel.CFG_MONGO_BATCH,
				100), "Batch size", 10));
		addDialogComponent(new DialogComponentBoolean(new SettingsModelBoolean(
				MongoNodeModel.CFG_MONGO_INCREMENTAL, true), "Incremental"));
		addDialogComponent(new DialogComponentNumber(new SettingsModelInteger(
				MongoNodeModel.CFG_MONGO_INCREMENT_SIZE, 100), "Increment size", 10));
	}

}
