package org.simplity.eclipse.plugin.validator.marker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.PlatformUI;

public class SimplityMarkerFactory {

	public static final String MARKER = "org.simplity.eclipse.plugin.validator.marker";
	
	public static IMarker createSimplityMarker(IResource iResource) throws CoreException {
		IMarker iMarker = null;
		iMarker = iResource.createMarker("org.simplity.eclipse.plugin.validator.marker");
		iMarker.setAttribute("description", "Simplity markers");
		iMarker.setAttribute(IMarker.MESSAGE, "Simplity Marker");
		iMarker.setAttribute(IMarker.LOCATION, "Simplity Error");
		iMarker.setAttribute(IMarker.LINE_NUMBER, "5");
		return iMarker;
	}
	
	public static List<IMarker> findSimplityMarkers(IResource iResource) {
		try {
			return Arrays.asList(iResource.findMarkers(MARKER, true, IResource.DEPTH_ZERO));
		} catch (CoreException e) {
			List<IMarker> emptyIMarkerList = new ArrayList<IMarker>();
			return emptyIMarkerList;
		}
	}
	
	public static List<IMarker> findAllSimplityMarkers(IResource iResource) {
		try {
			return Arrays.asList(iResource.findMarkers(MARKER, true, IResource.DEPTH_INFINITE));
		} catch (CoreException e) {
			List<IMarker> emptyIMarkerList = new ArrayList<IMarker>();
			return emptyIMarkerList;
		}
	}
	
	public static TreeSelection getSimplityTreeSelection() {
		ISelection iSelection = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService()
				.getSelection();
		if (iSelection instanceof TreeSelection) {
			return (TreeSelection) iSelection;
		}
		return null;
	}

}
