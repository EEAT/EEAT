package org.eeat.knime.mongo.script.node;

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
public class MongoScriptNodeDialog extends DefaultNodeSettingsPane {

	/**
	 * New pane for configuring Mongo node dialog. This is just a suggestion to demonstrate possible default
	 * dialog components.
	 */
	protected MongoScriptNodeDialog() {
		super();
		addDialogComponent(new DialogComponentString(new SettingsModelString(MongoScriptNodeModel.CFG_ID,
				"admin"), "ID", false, 10));
		addDialogComponent(new DialogComponentPasswordField(new SettingsModelString(MongoScriptNodeModel.CFG_PASSWORD,
				"1234"), "Password", 10));		
		addDialogComponent(new DialogComponentBoolean(new SettingsModelBoolean(
				MongoScriptNodeModel.CFG_MONGO_NOOP, false), "No operation"));
		addDialogComponent(new DialogComponentBoolean(new SettingsModelBoolean(
				MongoScriptNodeModel.CFG_MONGO_COPY, false), "Copy operation"));

		addDialogComponent(new DialogComponentString(new SettingsModelString(MongoScriptNodeModel.CFG_HOST1,
				"localhost"), "Host1", true, 10));
		addDialogComponent(new DialogComponentNumber(new SettingsModelInteger(MongoScriptNodeModel.CFG_PORT1,
				27017), "Port1", 1));
		addDialogComponent(new DialogComponentString(new SettingsModelString(MongoScriptNodeModel.CFG_DB1,
				"test"), "Database1", true, 10));
		addDialogComponent(new DialogComponentString(new SettingsModelString(
				MongoScriptNodeModel.CFG_MONGO_COLL, "users"), "Collection", true, 10));
		addDialogComponent(new DialogComponentMultiLineString(new SettingsModelString(
				MongoScriptNodeModel.CFG_SCRIPT, "db.serverStatus();"), "Script", true, 60, 5));

		addDialogComponent(new DialogComponentString(new SettingsModelString(MongoScriptNodeModel.CFG_HOST2,
				"localhost"), "Host2", true, 10));
		addDialogComponent(new DialogComponentNumber(new SettingsModelInteger(MongoScriptNodeModel.CFG_PORT2,
				27018), "Port2", 1));
		addDialogComponent(new DialogComponentString(new SettingsModelString(MongoScriptNodeModel.CFG_DB2,
				"test"), "Database2", true, 10));
	}

}
