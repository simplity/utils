package org.simplity.eclipse.plugin.validator.marker;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;

public class SimplityFileMarkerCount implements IEditorActionDelegate {

	public SimplityFileMarkerCount() {
		super();
	}

	@Override
	public void setActiveEditor(IAction action, IEditorPart editor) {
	}

	/*
	 * count number of markers in the file
	 */
	@Override
	public void run(IAction action) {
		IFile file = (IFile) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor()
				.getEditorInput().getAdapter(IFile.class);
		List<IMarker> markers = SimplityMarkerFactory.findSimplityMarkers(file);
		MessageDialog dialog = new MessageDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
				"Marker Count", null, markers.size() + " marker(s)", MessageDialog.INFORMATION, new String[] { "OK" },
				0);
		dialog.open();

	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {

	}

}
