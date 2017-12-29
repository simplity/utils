package org.simplity.eclipse.plugin.validator.marker;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Position;
import org.eclipse.ui.texteditor.IMarkerUpdater;

public class SimplityMarkerUpdater implements IMarkerUpdater {
	
	@Override
	public String[] getAttribute() {
		return null;
	}

	@Override
	public String getMarkerType() {
		return SimplityMarkerFactory.MARKER;
	}

	@Override
	public boolean updateMarker(IMarker iMarker, IDocument iDocument, Position characterPosition) {
		try {
			int characterStart = characterPosition.getOffset();
			int characterEnd = characterPosition.getOffset() + characterPosition.getLength();
			iMarker.setAttribute(IMarker.CHAR_START, characterStart);
			iMarker.setAttribute(IMarker.CHAR_END, characterEnd);
			return true;
		} catch (CoreException e) {
			return false;
		}
	}
}
