package org.simplity.eclipse.plugin.validator.marker;

import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jdt.core.IOpenable;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;

public class SimplityFilesMarkerCount implements IEditorActionDelegate {

	public SimplityFilesMarkerCount() {
		super();
	}

	@Override
	public void setActiveEditor(IAction action, IEditorPart editor) {
	}

	/*
	 * Identify all markers for an IResource
	 */
	@Override
	public void run(IAction action) {
		TreeSelection selection = SimplityMarkerFactory.getSimplityTreeSelection();
		if (selection.getFirstElement() instanceof IOpenable) {
			IResource resource = (IResource) ((IAdaptable) selection.getFirstElement()).getAdapter(IResource.class);
			List<IMarker> markers = SimplityMarkerFactory.findAllSimplityMarkers(resource);

			MessageDialog dialog = new MessageDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
					"Marker Count", null, markers.size() + " marker(s)", MessageDialog.INFORMATION,
					new String[] { "OK" }, 0);
			dialog.open();
		}

	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
	}

}
