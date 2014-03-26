package org.eeat.core.plugin;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

public class Util {
	static public void showError(String heading, String message, Exception e) {
		String error = "";
		if (e != null) {
			e.printStackTrace();
			error = e.toString();
		}
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (window != null) {
			Shell shell = window.getShell();
			MessageDialog.openInformation(shell, heading, message + error);
		}
	}
}
