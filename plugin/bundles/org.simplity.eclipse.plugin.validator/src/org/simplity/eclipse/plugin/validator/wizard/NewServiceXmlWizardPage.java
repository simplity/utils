package org.simplity.eclipse.plugin.validator.wizard;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;

public class NewServiceXmlWizardPage extends WizardNewFileCreationPage {
	
	public NewServiceXmlWizardPage(IStructuredSelection selection) {
		super("Create Simplity Service", selection);
		setTitle("Simplity Service Xml");
		setDescription("Creates a new Simplity Service");
	}

	@Override
	protected void createAdvancedControls(Composite parent) {
		super.createAdvancedControls(parent);
	}

}
