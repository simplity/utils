package org.simplity.eclipse.plugin.validator.marker;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.validation.AbstractValidator;
import org.eclipse.wst.validation.ValidationResult;
import org.eclipse.wst.validation.ValidationState;
import org.eclipse.wst.validation.ValidatorMessage;

public class SimplityValidator extends AbstractValidator {

	public String getValidatorName() {
		return "Simplity Validator";
	}

	@Override
	public ValidationResult validate(IResource resource, int kind, ValidationState validationState, IProgressMonitor progressMonitor) {
		ValidationResult validationResult = new ValidationResult();
		ValidatorMessage validatorMessage = ValidatorMessage.create("Simplity Error - " + getValidatorName(), resource);
		validatorMessage.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
		validatorMessage.setAttribute(IMarker.LINE_NUMBER, 2);
		validationResult.add(validatorMessage);
		return validationResult;
	}

}