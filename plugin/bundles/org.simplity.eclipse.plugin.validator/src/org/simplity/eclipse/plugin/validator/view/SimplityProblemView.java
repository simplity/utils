package org.simplity.eclipse.plugin.validator.view;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.ui.part.ViewPart;

public class SimplityProblemView extends ViewPart {
	Label label;

	public SimplityProblemView() {
	}

	public void createPartControl(Composite parent) {
		label = new Label(parent, SWT.WRAP);
		label.setText("Simplity Problems");
	}

	public void setFocus() {
		// set focus to my widget. For a label, this doesn't
		// make much sense, but for more complex sets of widgets
		// you would decide which one gets the focus.
	}
}