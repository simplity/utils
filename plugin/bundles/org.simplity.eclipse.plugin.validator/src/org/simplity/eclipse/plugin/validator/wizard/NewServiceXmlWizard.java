package org.simplity.eclipse.plugin.validator.wizard;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;
import org.simplity.eclipse.plugin.validator.Validator;
import org.simplity.kernel.db.DbAccessType;
import org.simplity.kernel.util.ReflectUtil;
import org.simplity.kernel.util.XmlUtil;
import org.simplity.tp.Service;

@SuppressWarnings("unused")
public class NewServiceXmlWizard extends Wizard implements INewWizard {

	private IStructuredSelection selection;
	private NewServiceXmlWizardPage newServiceXmlPage;
	private IWorkbench workbench;

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.workbench = workbench;
		this.selection = selection;
	}

	public NewServiceXmlWizard() {
		setWindowTitle("New Simplity service xml file");
	}

	public void addPages() {
		newServiceXmlPage = new NewServiceXmlWizardPage(selection);
		addPage(newServiceXmlPage);
	}

	@Override
	public boolean performFinish() {
		IFile file = null;
		if (validateFileName())
			file = newServiceXmlPage.createNewFile();

		if (file != null) {
			if (file.exists()) {
				Service service = new Service();
				Map<String, Field> serviceFields = ReflectUtil.getAllFields(service);
				try {
					String serviceName = newServiceXmlPage.getFileName();
					serviceName = serviceName.substring(0, serviceName.indexOf("."));
					Field name = serviceFields.get("name");
					name.setAccessible(true);
					name.set(service, serviceName);
					
					String moduleName = getModuleName(file.getFullPath().toString());
					Field module = serviceFields.get("moduleName");
					module.setAccessible(true);
					module.set(service, moduleName);
					
					Field dbAccessType = serviceFields.get("dbAccessType");
					dbAccessType.setAccessible(true);
					dbAccessType.set(service, DbAccessType.NONE);
					
					String xmlText = XmlUtil.objectToXmlString(service);	
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
		String fileName = newServiceXmlPage.getFileName();
		if(fileName.contains(".")) {
			String fileNameExtn = fileName.substring(fileName.lastIndexOf(".") + 1);
			if (fileNameExtn != "" && (fileNameExtn.equals("xml"))) {
				return true;
			}
		} else {
			String fileNameExtn = ".xml";
			newServiceXmlPage.setFileName(fileName + fileNameExtn);
			return true;
		}
		newServiceXmlPage.setErrorMessage("File name extension should be xml");
		newServiceXmlPage.setPageComplete(false);
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

	private String getModuleName(String filePath) {
		if(filePath.contains("/comp/service/tp/")) {
			filePath = filePath.split("/comp/service/tp/")[1];
			if(filePath.contains("/") && filePath.lastIndexOf("/") > 0) {
				filePath = filePath.substring(0, filePath.lastIndexOf("/"));
				filePath = filePath.replace("/", ".");
			} else {
				filePath = "";
			}
		} else {
			filePath = "";
		}
		return filePath;
	}
}
