/*
 * Copyright (c) 2017 simplity.org
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.simplity.eclipse.plugin.validator;

import java.util.Stack;

import org.simplity.kernel.ApplicationError;
import org.simplity.kernel.comp.ComponentType;
import org.simplity.kernel.comp.ValidationContext;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

/**
 * temporarily designed to extend ValidationContext to avoid any changes to that
 * class, but will be re-factored to stand on its own
 *
 * @author simplity.org
 *
 */
public class ValidationCtx extends ValidationContext {
	private static final Logger logger = LoggerFactory.getLogger(ValidationCtx.class);
	/**
	 * this context is re-used for validation at editing time by ide. this is
	 * the ide comp being validated
	 */
	private Comp compBeingValidated;

	private boolean needsValidation;

	private String currentTag;

	private Stack<String> pendingTags = new Stack<String>();
	/**
	 *
	 */
	public ValidationCtx() {
		// ensure that we do not call super()
	}

	/**
	 * begin processing a comp. Signals that the validation is at editing time,
	 * and no at run time.
	 *
	 * @param comp
	 * @param forValidation
	 *            true if this is for validation, false if it just being loaded.
	 */
	public void beginComp(Comp comp, boolean forValidation) {
		if (this.compBeingValidated != null) {
			logger.error("Error in validation of components. " + comp + " wants to start validating when "
					+ this.compBeingValidated + " is in the middle of validtion.");
		} else {
			this.compBeingValidated = comp;
			this.needsValidation = forValidation;
		}
	}

	/**
	 * ide validation is done for a comp
	 */
	public void endComp() {
		this.compBeingValidated = null;
	}

	/**
	 * add an error message
	 *
	 * @param error
	 */
	@Override
	public void addError(String error) {
		if (this.compBeingValidated == null) {
			logger.error("Error message being added without a call to startValidation(). " + error);
		} else if(this.needsValidation) {
			String row = pendingTags + "~" + error;
			System.out.println("Error message - " + row);
			this.compBeingValidated.addError(row);
		}
	}

	/**
	 * add an error message
	 *
	 * @param error
	 * @param tagName
	 * @param attributeName
	 */
	public void addError(String error, String tagName, String attributeName) {
		if (this.compBeingValidated == null) {
			throw new ApplicationError("Error message being added without a call to startValidation(). " + error);
		} else if(this.needsValidation) {
			//String[] row = { this.currentType, this.currentCompName, error };
			String row = tagName + "~" + attributeName+ "~" + error;
			this.compBeingValidated.addError(row);
		}
	}
	
	/**
	 * Report an unusual setting that is not an error, but perhaps requires some
	 * ones review to confirm that this is indeed by design and not by error or
	 * omission
	 *
	 * @param warning
	 */
	@Override
	public void reportUnusualSetting(String warning) {
		if (this.compBeingValidated == null) {
			logger.error("Warning message being added without a call to startValidation(). " + warning);
		} else if(this.needsValidation) {
			this.compBeingValidated.addWarning(warning);
		}
	}

	/**
	 * add a reference to the comp being validated
	 *
	 * @param refType
	 * @param refName
	 */
	@Override
	public void addReference(ComponentType refType, String refName) {
		if (this.compBeingValidated == null) {
			logger.error("referecne being added without a call to startValidation(). ");
		} else {
			this.compBeingValidated.addReferredComp(refType, refName, this.needsValidation, this.pendingTags);
		}
	}

	/**
	 * Many components refer to record. Here is a short-cut for them. Add this
	 * record as reference, and add error in case it is not defined
	 *
	 * @param recName
	 * @param fieldName
	 * @param isRequired
	 * @return 0 if all ok, 1 if an error got added
	 */
	@Override
	public int checkRecordExistence(String recName, String fieldName, boolean isRequired) {
		if (recName == null) {
			if (isRequired) {
				this.addError(fieldName + " requires a valid record name");
				return 1;
			}
			return 0;
		}
		this.addReference(ComponentType.REC, recName);
		return 0;
	}

	/**
	 * Many components refer to data type. Here is a short-cut for them. Add
	 * this data type as
	 * reference, and add error in case it i not defined
	 *
	 * @param dataTypeName
	 * @param fieldName
	 * @param isRequired
	 * @return 0 if all ok, 1 if an error got added
	 */
	@Override
	public int checkDtExistence(String dataTypeName, String fieldName, boolean isRequired) {
		if (dataTypeName == null) {
			if (isRequired) {
				this.addError(fieldName + " requires a valid data type");
				return 1;
			}
			return 0;
		}
		this.addReference(ComponentType.DT, dataTypeName);
		return 0;
	}

	/**
	 * check existence of a class and whether it implements the right interface
	 *
	 * @param className
	 * @param klass
	 * @return 0 if all OK, 1 if an error got added
	 */
	@Override
	public int checkClassName(String className, Class<?> klass) {
		if (className == null) {
			return 0;
		}
		try {
			Class<?> cls = Class.forName(className);
			if (klass.isAssignableFrom(cls) == false) {
				this.addError(className + " should implement " + klass.getName());
				return 1;
			}
		} catch (Exception e) {
			this.addError(className + " is not defined as a java class.");
			return 1;
		}

		return 0;
	}

	/**
	 * check existence of a class and whether it implements the right interface
	 *
	 * @param fieldName
	 * @param fieldValue
	 * @return 0 if all OK, 1 if an error got added
	 */
	@Override
	public int checkMandatoryField(String fieldName, Object fieldValue) {
		if (fieldValue == null) {
			this.addError(fieldName + " is a required field.");
			return 1;
		}
		return 0;
	}

	/**
	 * is this service in error? If so add a message and return true.
	 *
	 * @param serviceName
	 *            name of service
	 * @param attName
	 *            attribute that uses this service
	 * @return true if service is in error. false otherwise
	 */
	@Override
	public boolean checkServiceName(String serviceName, String attName) {
		if (serviceName != null) {
			this.addReference(ComponentType.SERVICE, serviceName);
		}
		return false;
	}

	//@Override
	public void beginTag(String tagName) {
		if(this.compBeingValidated == null){
			throw new ApplicationError("Tag " + tagName + " is added when there is no component being validated");
		}
		//if(this.currentTag != null){
		//	this.pendingTags.push(this.currentTag);
		//}
		this.pendingTags.push(tagName);
		this.currentTag = tagName;
	}

	//@Override
	public void endTag(String tagName) {
		if(this.currentTag == null) {
			throw new ApplicationError("Tag " + tagName + " is getting ended, but the tag was never started");
		}
		if(!this.currentTag.equalsIgnoreCase(tagName)) {
			throw new ApplicationError("Tag " + tagName + " is getting ended, but the current tag is " + this.currentTag + ". Note that child-tags can not over-lap, but have to be fully enclosed within teh parent tag.");
		}
		if(this.pendingTags.isEmpty()) {
			this.currentTag = null;
		}else {
			this.pendingTags.pop();
			if(!this.pendingTags.isEmpty())
				this.currentTag = this.pendingTags.lastElement();
			else
				this.currentTag = null;
		}
	}
}
