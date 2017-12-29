package org.simplity.eclipse.plugin.validator.wizard;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;

public class NewApplicationXmlWizardPage extends WizardNewFileCreationPage {
	
	public NewApplicationXmlWizardPage(IStructuredSelection selection) {
		super("Create Simplity application.xml", selection);
		setTitle("Simplity Application Xml");
		setDescription("Creates a new Simplity Application Xml");
		setFileName("application.xml");
	}
	
	@Override
	protected void createAdvancedControls(Composite parent) {
		((Text)((Composite)((Composite)parent.getChildren()[0]).getChildren()[1]).getChildren()[1]).setEnabled(false);
		super.createAdvancedControls(parent);
	}
	
}
