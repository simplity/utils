package org.simplity.eclipse.plugin.validator.hyperlink;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.AbstractHyperlinkDetector;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.jface.text.hyperlink.IHyperlinkDetector;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PlatformUI;

public class DetectHyperlinks extends AbstractHyperlinkDetector implements IHyperlinkDetector {

	public DetectHyperlinks() {
		System.out.println("inside DetectRecord");
	}

	@Override
	public IHyperlink[] detectHyperlinks(ITextViewer textViewer, IRegion region, boolean canShowMultipleHyperlinks) {
		IEditorPart iEditor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getWorkbench()
				.getActiveWorkbenchWindow().getActivePage().getActiveEditor();

		IDocument document = textViewer.getDocument();
		int offset = region.getOffset();

		IRegion lineRegion;
		String candidate;
		try {
			lineRegion = document.getLineInformationOfOffset(offset);
			candidate = document.get(lineRegion.getOffset(), lineRegion.getLength());
		} catch (BadLocationException ex) {
			return null;
		}

		IProject project = getSelectedProject(iEditor);
		// IResource resource = project.findMember(candidate + ".xml", true);
		IResource resource = project.findMember("src/main/resources/comp/application.xml");
		IFile iFile = (IFile) resource;
		if (iFile != null) {
			IRegion targetRegion = new Region(lineRegion.getOffset(), candidate.length());
			if ((targetRegion.getOffset() <= offset)
					&& ((targetRegion.getOffset() + targetRegion.getLength()) > offset)) {
				Hyperlink hyperlink = new Hyperlink(targetRegion, iFile);
				return new IHyperlink[] { hyperlink };
			}
		}
		return null;
	}

	public static IProject getSelectedProject(IEditorPart editor) {
		IEditorInput input = editor.getEditorInput();
		if (!(input instanceof IFileEditorInput))
			return null;
		return ((IFileEditorInput) input).getFile().getProject();
	}

}
