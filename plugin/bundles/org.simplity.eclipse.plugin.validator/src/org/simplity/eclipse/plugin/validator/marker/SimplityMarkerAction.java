package org.simplity.eclipse.plugin.validator.marker;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;

public class SimplityMarkerAction implements IEditorActionDelegate {

	public SimplityMarkerAction() {
		super();
	}

	@Override
	public void setActiveEditor(IAction action, IEditorPart editor) {

	}

	/*
	 * create new marker for the given IFile
	 */
	@Override
	public void run(IAction action) {
		try {
			IFile iFile = (IFile) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor()
					.getEditorInput().getAdapter(IFile.class);
			SimplityMarkerFactory.createSimplityMarker(iFile);
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {

	}

}