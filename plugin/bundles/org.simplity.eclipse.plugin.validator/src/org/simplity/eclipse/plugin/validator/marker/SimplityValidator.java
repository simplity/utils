package org.simplity.eclipse.plugin.validator.marker;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.validation.AbstractValidator;
import org.eclipse.wst.validation.ValidationResult;
import org.eclipse.wst.validation.ValidationState;
import org.eclipse.wst.validation.ValidatorMessage;
import org.simplity.eclipse.plugin.validator.CompsManager;

public class SimplityValidator extends AbstractValidator {

	public String getValidatorName() {
		return "Simplity Validator";
	}

	@Override
	public ValidationResult validate(IResource resource, int kind, ValidationState validationState, IProgressMonitor progressMonitor) {
		String compRoot = "D:/WorkSpace/SimplityPluginRuntime/SimplityTest/src/main/resources/comp";
		CompsManager.loadResources(compRoot);
		
		ValidationResult validationResult = new ValidationResult();
		
		String fileName = resource.getLocation().toString();
		String[] errors = CompsManager.validate(fileName);
		for(String error : errors) {
			ValidatorMessage validatorMessage = ValidatorMessage.create(error, resource);
			validatorMessage.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
			validatorMessage.setAttribute(IMarker.LINE_NUMBER, 2);
			validationResult.add(validatorMessage);
		}
		
		return validationResult;
	}

}