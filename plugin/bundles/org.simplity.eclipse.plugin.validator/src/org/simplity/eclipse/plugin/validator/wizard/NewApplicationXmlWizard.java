package org.simplity.eclipse.plugin.validator.wizard;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.wizards.newresource.BasicNewFileResourceWizard;
import org.simplity.eclipse.plugin.validator.Validator;
import org.simplity.kernel.Application;
import org.simplity.kernel.util.ReflectUtil;
import org.simplity.kernel.util.XmlUtil;

@SuppressWarnings("unused")
public class NewApplicationXmlWizard extends Wizard implements INewWizard {

	private IStructuredSelection selection;
	private WizardNewFileCreationPage newApplicationXmlPage;
	private IWorkbench workbench;

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.workbench = workbench;
		this.selection = selection;
	}

	public NewApplicationXmlWizard() {
		setWindowTitle("Create Simplity application.xml");
	}

	public void addPages() {
		
		newApplicationXmlPage = new NewApplicationXmlWizardPage(selection);
		newApplicationXmlPage.setFileName("application.xml");
		addPage(newApplicationXmlPage);
	}

	
	@Override
	public boolean performFinish() {
		IFile file = null;
		if (validateFileName())
			file = newApplicationXmlPage.createNewFile();

		if (file != null) {
			if (file.exists()) {
				Application application = new Application();
				Map<String, Field> applicationFields = ReflectUtil.getAllFields(application);
				try {
					String projectName = file.getProject().getName();
					Field applicationId = applicationFields.get("applicationId");
					applicationId.setAccessible(true);
					applicationId.set(application, projectName);
					String xmlText = XmlUtil.objectToXmlString(application);	
					file.setContents(new ByteArrayInputStream(xmlText.getBytes(StandardCharsets.UTF_8)),0,null);
					file.refreshLocal(IResource.DEPTH_ZERO, null);
				} catch (CoreException | IllegalArgumentException | IllegalAccessException e) {
					e.printStackTrace();
				}
				openResource(file);
			}
			
			return true;
		} else
			return false;
	}

	public boolean validateFileName() {
		String fileName = newApplicationXmlPage.getFileName();
		if(fileName.contains(".")) {
			String fileNameExtn = fileName.substring(fileName.lastIndexOf(".") + 1);
			if (fileNameExtn != "" && (fileNameExtn.equals("xml"))) {
				return true;
			}
		} else {
			String fileNameExtn = ".xml";
			newApplicationXmlPage.setFileName(fileName + fileNameExtn);
			return true;
		}
		
		newApplicationXmlPage.setErrorMessage("File name extension should be xml");
		newApplicationXmlPage.setPageComplete(false);
		return false;
	}

	public final void openResource(final IFile resource) {
		final IWorkbenchPage activePage = Validator.getDefault().getWorkbench().getActiveWorkbenchWindow()
				.getActivePage();
		if (activePage != null) {
			final Display display = Validator.getDefault().getWorkbench().getDisplay();
			if (display != null) {
				display.asyncExec(new Runnable() {
					public void run() {
						try {
							IDE.openEditor(activePage, resource, true);
						} catch (PartInitException e) {
							e.printStackTrace();
						}
					}
				});
			}
		}
	}
/*
	public static String getSelectedProjectName() {
		ISelection iSelection = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService().getSelection();
		if (iSelection instanceof IStructuredSelection) {
			Object firstElement = ((IStructuredSelection) iSelection).getFirstElement();
			if (firstElement instanceof IResource) {
				return ((IResource) firstElement).getProject().getName();
			}
		}
		return null;
	}
*/
}
