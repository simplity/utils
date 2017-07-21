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
import org.simplity.kernel.dm.Record;
import org.simplity.kernel.dm.RecordUsageType;
import org.simplity.kernel.util.ReflectUtil;
import org.simplity.kernel.util.XmlUtil;

@SuppressWarnings("unused")
public class NewRecordXmlWizard extends Wizard implements INewWizard {

	private IStructuredSelection selection;
	private NewRecordXmlWizardPage newRecordXmlPage;
	private IWorkbench workbench;

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.workbench = workbench;
		this.selection = selection;
	}

	public NewRecordXmlWizard() {
		setWindowTitle("New Simplity record xml file");
	}

	public void addPages() {
		newRecordXmlPage = new NewRecordXmlWizardPage(selection);
		addPage(newRecordXmlPage);
	}

	@Override
	public boolean performFinish() {
		IFile file = null;
		if (validateFileName())
			file = newRecordXmlPage.createNewFile();

		if (file != null) {
			if (file.exists()) {
				Record record = new Record();
				Map<String, Field> recordFields = ReflectUtil.getAllFields(record);
				try {
					String recordName = newRecordXmlPage.getFileName();
					recordName = recordName.substring(0, recordName.indexOf("."));
					Field name = recordFields.get("name");
					name.setAccessible(true);
					name.set(record, recordName);
					
					String moduleName = getModuleName(file.getFullPath().toString());
					Field module = recordFields.get("moduleName");
					module.setAccessible(true);
					module.set(record, moduleName);
					
					Field recordType = recordFields.get("recordType");
					recordType.setAccessible(true);
					recordType.set(record, RecordUsageType.STRUCTURE);
					
					String xmlText = XmlUtil.objectToXmlString(record);	
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
		String fileName = newRecordXmlPage.getFileName();
		if(fileName.contains(".")) {
			String fileNameExtn = fileName.substring(fileName.lastIndexOf(".") + 1);
			if (fileNameExtn != "" && (fileNameExtn.equals("xml"))) {
				return true;
			}
		} else {
			String fileNameExtn = ".xml";
			newRecordXmlPage.setFileName(fileName + fileNameExtn);
			return true;
		}
		newRecordXmlPage.setErrorMessage("File name extension should be xml");
		newRecordXmlPage.setPageComplete(false);
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
		if(filePath.contains("/comp/rec/")) {
			filePath = filePath.split("/comp/rec/")[1];
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
