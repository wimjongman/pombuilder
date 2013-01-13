/*******************************************************************************
 * Copyright (c) 2013 	Remain Software and Industrial TSI
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Wim Jongman - initial API and implementation
 ******************************************************************************/
package com.remainsoftware.tycho.pombuilder.core.internal;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Assert;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.remainsoftware.tycho.pombuilder.core.IPom;
import com.remainsoftware.tycho.pombuilder.core.PomBuilderException;

/**
 * This implementation of {@link IPom} represents a pom file in the filesystem
 * and accepts a {@link File} in its constructor. Various operations enable you
 * to manipulate attributes of the pom. Other operations enable you to write out
 * the pom to the filesystem.
 * 
 * @author Wim Jongman
 * 
 */
public class Pom implements IPom {

	private static final String PROJECT = "project";

	private static final String MODELVERSION = "modelversion";

	private static final String PACKAGING = "packaging";

	private static final String VERZION = "version";

	private static final String GROUP_ID = "groupId";

	private static final String ARTIFACT_ID = "artifactId";

	private static final String PARENT = "parent";

	private static final String MODEL_VERSION = "modelVersion";

	private static final String RELATIVE_PATH = "relativePath";

	private final File pomFile;

	private Document document;

	private IPom parentPom;

	private IProject project;

	private final IResource resource;

	/**
	 * Creates a Pom object based on the project of the passed resource. If the
	 * pom exists it will be loaded otherwise it will be initialized but no file
	 * will be created until {@link #write()} has been called.
	 * 
	 * @param resource
	 *            may not be null
	 * @throws PomBuilderException
	 */
	public Pom(IResource resource) throws PomBuilderException {

		Assert.isNotNull(resource);

		this.project = resource.getProject();
		this.resource = project.getFile("pom.xml");

		File dir = new File(resource.getProject().getLocationURI());
		String pomPath = dir.getAbsolutePath() + File.separator + "pom.xml";
		pomFile = new File(pomPath);
		if (pomFile.exists()) {
			loadPom();
		} else {
			initPom();
		}
	}

	/**
	 * Initialize a new pom.xml
	 * 
	 * @throws PomBuilderException
	 */
	private void initPom() throws PomBuilderException {

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = null;
		try {
			dBuilder = dbFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			throw new PomBuilderException(e);
		}
		document = dBuilder.newDocument();

		Element projectElement = document.createElement(PROJECT);
		document.appendChild(projectElement);

		Element modelVersion = document.createElement(MODELVERSION);
		modelVersion.appendChild(document.createTextNode("4.0.0"));
		projectElement.appendChild(modelVersion);

		Element parent = document.createElement(PARENT);
		projectElement.appendChild(parent);

		Element parentGroupId = document.createElement(GROUP_ID);
		parentGroupId.appendChild(document.createTextNode("replace.with.real.parent.groupId"));
		parent.appendChild(parentGroupId);

		Element parentArtifactId = document.createElement(ARTIFACT_ID);
		parentArtifactId.appendChild(document.createTextNode("replace.with.real.parent.artifactId"));
		parent.appendChild(parentArtifactId);

		Element parentVersion = document.createElement(VERZION);
		parentVersion.appendChild(document.createTextNode("replace.with.real.parent.version"));
		parent.appendChild(parentVersion);

		Element groupId = document.createElement(GROUP_ID);
		groupId.appendChild(document.createTextNode("replace.with.real.groupId"));
		projectElement.appendChild(groupId);

		Element artifactId = document.createElement(ARTIFACT_ID);
		artifactId.appendChild(document.createTextNode("replace.with.real.artifactId"));
		projectElement.appendChild(artifactId);

		Element versionElement = document.createElement(VERZION);
		versionElement.appendChild(document.createTextNode("replace.with.real.version"));
		projectElement.appendChild(versionElement);

		Element pack = document.createElement(PACKAGING);
		pack.appendChild(document.createTextNode("replace.with.real.packaging.type"));
		projectElement.appendChild(pack);

	}

	private void loadPom() throws PomBuilderException {

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		try {
			dBuilder = dbFactory.newDocumentBuilder();
			document = dBuilder.parse(pomFile);
		} catch (Exception e) {
			throw new PomBuilderException(e);
		}
	}

	@Override
	public IPom setVersion(String version) {
		createSimpleElementWithText(VERZION, version);
		return this;
	}

	@Override
	public String getVersion() {
		if (document.getElementsByTagName(VERZION).getLength() == 0) {
			return null;
		}

		Node element = document.getElementsByTagName(VERZION).item(0);
		return element.getTextContent();
	}

	@Override
	public IPom setArtifactId(String artifactId) {
		createSimpleElementWithText(ARTIFACT_ID, artifactId);
		return this;
	}

	private void createSimpleElementWithText(String elementName, String textValue) {

		Element project = document.getDocumentElement();
		Element element = null;
		if (project.getElementsByTagName(elementName).getLength() > 0) {
			element = (Element) project.getElementsByTagName(elementName).item(0);
		}
		if (element == null) {
			element = document.createElement(elementName);
			project.appendChild(element);
		}

		else {
			NodeList childNodes = element.getChildNodes();
			for (int i = 0; i < childNodes.getLength(); i++) {
				element.removeChild(childNodes.item(i));
			}
		}

		element.appendChild(document.createTextNode(textValue));
	}

	private void createParentElementWithText(String elementName, String textValue) {

		Element project = (Element) document.getElementsByTagName(PROJECT).item(0);
		Element parent = null;
		if (project.getElementsByTagName(PARENT).getLength() == 0) {
			parent = document.createElement(PARENT);
			project.appendChild(parent);
		} else {
			parent = (Element) project.getElementsByTagName(PARENT).item(0);
		}

		Element element = null;

		if (parent.getElementsByTagName(elementName).getLength() > 0) {
			element = (Element) parent.getElementsByTagName(elementName).item(0);
		}

		if (element == null) {
			element = document.createElement(elementName);
			parent.appendChild(element);
		}

		else {
			NodeList childNodes = element.getChildNodes();
			for (int i = 0; i < childNodes.getLength(); i++) {
				element.removeChild(childNodes.item(i));
			}
		}

		element.appendChild(document.createTextNode(textValue));
	}

	@Override
	public String getArtifactId() {
		if (document.getElementsByTagName(ARTIFACT_ID).getLength() == 0) {
			return null;
		}

		Node element = document.getElementsByTagName(ARTIFACT_ID).item(0);
		return element.getTextContent();
	}

	@Override
	public IPom setGroupId(String groupId) {
		createSimpleElementWithText(GROUP_ID, groupId);
		return this;
	}

	@Override
	public String getGroupId() {
		if (document.getElementsByTagName(GROUP_ID).getLength() == 0) {
			return null;
		}

		Node element = document.getElementsByTagName(GROUP_ID).item(0);
		return element.getTextContent();
	}

	@Override
	public IPom setgetParentProject(String projectName) throws PomBuilderException {

		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
		if (project == null) {
			throw new PomBuilderException("Project " + projectName
					+ " was not found. If applicable, check the Parent-Project field in the MANIFEST.MF");
		}

		IResource pom = project.findMember("pom.xml");
		if (pom == null) {
			throw new PomBuilderException("File pom.xml was not found in project " + project
					+ ". Make sure the project and the pom.xml exists before you refer to it.");
		}

		parentPom = new Pom(project);

		setParentGroupId(parentPom.getGroupId());
		setParentArtifactId(parentPom.getArtifactId());
		setParentVersion(parentPom.getVersion());
		setParentRelativePath(parentPom);

		return getParent();
	}

	private IPom setParentRelativePath(IPom parentPom) throws PomBuilderException {

		IProject project = parentPom.getProject();
		File parent = new File(project.getLocationURI());
		File pom = pomFile.getParentFile();

		try {
			createParentElementWithText(RELATIVE_PATH, FileUtil.getRelativeFile(parent, pom));
		} catch (IOException e) {
			throw new PomBuilderException(e);
		}

		return this;
	}

	private void setParentVersion(String version) {
		createParentElementWithText(VERZION, version);
	}

	private void setParentArtifactId(String artifactId) {
		createParentElementWithText(ARTIFACT_ID, artifactId);
	}

	private void setParentGroupId(String groupId) {
		createParentElementWithText(GROUP_ID, groupId);
	}

	@Override
	public IPom getParent() {
		return parentPom;
	}

	@Override
	public IPom setPackaging(String packaging) {
		createSimpleElementWithText(PACKAGING, packaging);
		return this;
	}

	@Override
	public String getPackaging() {
		if (document.getElementsByTagName(PACKAGING).getLength() == 0) {
			return null;
		}

		Node element = document.getElementsByTagName(PACKAGING).item(0);
		return element.getTextContent();
	}

	@Override
	public File getFile() {
		return pomFile;
	}

	@Override
	public IPom write() throws PomBuilderException {
		// write the content
		try {
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			DOMSource source = new DOMSource(document);
			StreamResult result = new StreamResult(pomFile);
			transformer.transform(source, result);
		} catch (Exception e) {
			throw new PomBuilderException(e);
		}
		return this;
	}

	@Override
	public IPom setModelVersion(String modelVersion) {
		createSimpleElementWithText(MODEL_VERSION, modelVersion);
		return this;
	}

	@Override
	public String getModelVersion() {
		if (document.getElementsByTagName(MODEL_VERSION).getLength() == 0) {
			return null;
		}

		Node element = document.getElementsByTagName(MODEL_VERSION).item(0);
		return element.getTextContent();
	}

	@Override
	public IProject getProject() {
		return project;
	}

	@Override
	public IResource getResource() {
		return resource;
	}
}
