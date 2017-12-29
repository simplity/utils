package org.simplity.eclipse.plugin.validator.hyperlink;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.ide.IDE;
import org.simplity.eclipse.plugin.validator.Validator;

public class Hyperlink implements IHyperlink {

	private IRegion region;
	private IFile file;
	private int lineNumber;

	public Hyperlink(IRegion urlRegion, IFile file) {
		this.region = urlRegion;
		this.file = file;
		this.lineNumber = 1;
	}

	@Override
	public IRegion getHyperlinkRegion() {
		return region;
	}

	@Override
	public String getTypeLabel() {
		return null;
	}

	@Override
	public String getHyperlinkText() {
		return file.getFullPath().toOSString();
	}

	@Override
	public void open() {
		
		try {
			IEditorPart editorPart = IDE.openEditor(
					Validator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage(), file, false);
			
/*
			Program program= Program.findProgram("xml");
			if (program != null)
				program.execute(file.getName());
				
			final ITextEditor editor = (ITextEditor) editorPart;

			final IDocumentProvider provider = editor.getDocumentProvider();
			final IDocument document = provider.getDocument(editor.getEditorInput());

			final int start = document.getLineOffset(lineNumber - 1);
			editor.selectAndReveal(start, 0);
*/
		} catch (Exception e) {
			e.printStackTrace();
		}

		/*
		 * if (region != null) { Program.launch(fUrlString + ".xml"); return; }
		 * //PreferencesUtil.createPreferenceDialogOn(Display.getDefault().
		 * getActiveShell(), null, null, null).open(); Shell shell =
		 * PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		 * System.out.println("text value : "+shell.getData().toString());
		 * MessageDialog.openInformation(shell, "Clicked", "Clicked on test");
		 */
	}
}