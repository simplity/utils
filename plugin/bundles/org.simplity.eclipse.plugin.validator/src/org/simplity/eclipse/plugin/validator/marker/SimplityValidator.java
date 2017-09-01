package org.simplity.eclipse.plugin.validator.marker;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.validation.AbstractValidator;
import org.eclipse.wst.validation.ValidationResult;
import org.eclipse.wst.validation.ValidationState;
import org.eclipse.wst.validation.ValidatorMessage;
import org.simplity.eclipse.plugin.validator.CompsManager;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

public class SimplityValidator extends AbstractValidator {

	public String getValidatorName() {
		return "Simplity Validator";
	}

	@Override
	public ValidationResult validate(IResource resource, int kind, ValidationState validationState,
			IProgressMonitor progressMonitor) {
		String compRoot = resource.getProject().findMember("src/main/resources/comp").getLocation().toString();
		CompsManager.loadResources(compRoot);

		Document document = null;
		try {
			document = readXML(((IFile) resource).getContents(), "lineNum");
		} catch (Exception e) {
			e.printStackTrace();
		}

		ValidationResult validationResult = new ValidationResult();

		String fileName = resource.getLocation().toString();
		String[] errors = CompsManager.validate(fileName);

		if(errors != null & errors.length > 0) {
			for (String error : errors) {
				// Extract the element from the doc where the error is found, and
				// use the lineNum attribute to extract the line number
				// ((Element)(doc.getElementsByTagName("setValue").item(0))).getAttribute("lineNum");
				
				Map<String,String> errorMap = parseErrorData(document, error);
				
				ValidatorMessage validatorMessage = ValidatorMessage.create(errorMap.get("errorMessage"), resource);
				validatorMessage.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
				validatorMessage.setAttribute(IMarker.LINE_NUMBER, Integer.valueOf(errorMap.get("errorLineNumber")));
				validationResult.add(validatorMessage);
			}
		}
		
		return validationResult;
	}

	private Map<String,String> parseErrorData(Document document, String errorData) {
		Map<String, String> errorMap = new HashMap<String, String>();
		
		String xpathExpression = null;
		String[] errorArray = errorData.split("~");
		if (errorArray != null && errorArray.length > 0) {
			errorMap.put("errorMessage", errorArray[2]);
			errorMap.put("attribute", errorArray[1]);
			String regex = "\\[|\\]|\\s+";
			xpathExpression = errorArray[0].replaceAll(regex, "").replaceAll(",","/");
		}

		if(xpathExpression != null) {
			try {
				XPathFactory xPathFactory = XPathFactory.newInstance();
				XPath xPath = xPathFactory.newXPath();
				NodeList nodeList = (NodeList) xPath.evaluate("/" + xpathExpression, document, XPathConstants.NODESET);
				if(nodeList.item(0) != null) {
					errorMap.put("errorLineNumber", nodeList.item(0).getAttributes().getNamedItem("lineNum").getNodeValue());
				}
			} catch(XPathExpressionException e) {
				e.printStackTrace();
			}
		} else {
			errorMap.put("errorLineNumber", "1");
		}
		return errorMap;
	}
	
	public static Document readXML(InputStream is, final String lineNumAttribName) throws IOException, SAXException {
		final Document doc;
		SAXParser parser;
		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			parser = factory.newSAXParser();
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			doc = docBuilder.newDocument();
		} catch (ParserConfigurationException e) {
			throw new RuntimeException("Can't create SAX parser / DOM builder.", e);
		}

		final Stack<Element> elementStack = new Stack<Element>();
		final StringBuilder textBuffer = new StringBuilder();
		DefaultHandler handler = new DefaultHandler() {
			private Locator locator;

			@Override
			public void setDocumentLocator(Locator locator) {
				this.locator = locator; // Save the locator, so that it can be
										// used later for line tracking when
										// traversing nodes.
			}

			@Override
			public void startElement(String uri, String localName, String qName, Attributes attributes)
					throws SAXException {
				addTextIfNeeded();
				Element el = doc.createElement(qName);
				for (int i = 0; i < attributes.getLength(); i++)
					el.setAttribute(attributes.getQName(i), attributes.getValue(i));
				el.setAttribute(lineNumAttribName, String.valueOf(locator.getLineNumber()));
				elementStack.push(el);
			}

			@Override
			public void endElement(String uri, String localName, String qName) {
				addTextIfNeeded();
				Element closedEl = elementStack.pop();
				if (elementStack.isEmpty()) { // Is this the root element?
					doc.appendChild(closedEl);
				} else {
					Element parentEl = elementStack.peek();
					parentEl.appendChild(closedEl);
				}
			}

			@Override
			public void characters(char ch[], int start, int length) throws SAXException {
				textBuffer.append(ch, start, length);
			}

			// Outputs text accumulated under the current node
			private void addTextIfNeeded() {
				if (textBuffer.length() > 0) {
					Element el = elementStack.peek();
					Node textNode = doc.createTextNode(textBuffer.toString());
					el.appendChild(textNode);
					textBuffer.delete(0, textBuffer.length());
				}
			}
		};
		parser.parse(is, handler);

		return doc;
	}

}