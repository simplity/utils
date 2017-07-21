package org.simplity.eclipse.plugin.validator.wizard;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;

public class NewRecordXmlWizardPage extends WizardNewFileCreationPage {

	public NewRecordXmlWizardPage(IStructuredSelection selection) {
		super("Create Simplity Record", selection);
		setTitle("Simplity Record Xml");
		setDescription("Creates a new Simplity Record");
	}

	@Override
	protected void createAdvancedControls(Composite parent) {
		super.createAdvancedControls(parent);
	}

}
