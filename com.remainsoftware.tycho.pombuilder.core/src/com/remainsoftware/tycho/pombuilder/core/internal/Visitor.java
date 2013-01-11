/*******************************************************************************
 * Copyright (c) 2013 Remain Software and others http://remainsoftware.com
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Gerd Wuetherich (gerd@gerd-wuetherich.de) - initial implementation
 *     Wim Jongman - adapted to pom builder
 ******************************************************************************/
package com.remainsoftware.tycho.pombuilder.core.internal;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.osgi.util.ManifestElement;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * <p>
 * </p>
 * 
 * @author Wim Jongman
 */
public class Visitor implements IResourceVisitor, IResourceDeltaVisitor {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean visit(IResource resource) throws CoreException {

		if (resource.getType() != IResource.FILE) {
			return true;
		}

		handle(resource);

		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean visit(IResourceDelta delta) throws CoreException {

		if (delta.getKind() == IResourceDelta.ADDED) {
			return visit(delta.getResource());

		} else if (delta.getKind() == IResourceDelta.REMOVED) {

		} else if (delta.getKind() == IResourceDelta.CHANGED) {
			return visit(delta.getResource());
		}

		return true;
	}

	/**
	 * <p>
	 * </p>
	 * 
	 * @param resource
	 * @throws CoreException
	 */
	private void handle(IResource resource) throws CoreException {

		if (resource.getName().equals("MANIFEST.MF")) {
			parseManifest(resource);
			return;
		}

		if (resource.getName().equals("feature.xml")) {
			parseFeature(resource);
			return;
		}

		if (resource.getName().endsWith(".product")) {
			parseProduct(resource);
			return;
		}

	}

	private void parseProduct(IResource resource) {
		// TODO Auto-generated method stub

	}

	private void parseFeature(IResource resource) {
		// TODO Auto-generated method stub

	}

	private void parseManifest(IResource resource) {

		FileInputStream fisManifest = null;
		boolean fisManifestOpen = false;

		try {

			fisManifest = new FileInputStream(resource.getLocation().toFile());
			HashMap<String, String> map = new HashMap<String, String>();
			ManifestElement.parseBundleManifest(fisManifest, map);
			fisManifestOpen = true;

			writePomBundle(resource.getProject(), map.get("Bundle-Version"), map.get("Bundle-SymbolicName"),
					map.get("Parent-Project"));

			System.out.println(map);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		finally {
			if (fisManifestOpen) {
				closeInputStream(fisManifest);
			}
		}
	}

	private void writePomBundle(IProject project, String version, String symbolicName, String parentProject) {

		try {

			File dir = new File(project.getLocationURI());
			String pomPath = dir.getAbsolutePath() + File.separator + "pom.xml";
			File pomFile = new File(pomPath);

			if (pomFile.exists()) {
				return;
			}

			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.newDocument();

			Element projectElement = doc.createElement("project");
			doc.appendChild(projectElement);

			Element modelVersion = doc.createElement("modelversion");
			modelVersion.appendChild(doc.createTextNode("4.0.0"));
			projectElement.appendChild(modelVersion);

			Element parent = doc.createElement("parent");
			projectElement.appendChild(parent);

			Element parentGroupId = doc.createElement("groupId");
			parentGroupId.appendChild(doc.createTextNode("replace.with.real.parent.groupId"));
			parent.appendChild(parentGroupId);

			Element parentArtifactId = doc.createElement("artifactId");
			parentArtifactId.appendChild(doc.createTextNode("replace.with.real.parent.artifactId"));
			parent.appendChild(parentArtifactId);

			Element parentVersion = doc.createElement("version");
			parentVersion.appendChild(doc.createTextNode("replace.with.real.parent.version"));
			parent.appendChild(parentVersion);

			Element groupId = doc.createElement("groupId");
			groupId.appendChild(doc.createTextNode("replace.with.real.groupId"));
			projectElement.appendChild(groupId);

			Element artifactId = doc.createElement("artifactId");
			artifactId.appendChild(doc.createTextNode(symbolicName));
			projectElement.appendChild(artifactId);

			Element versionElement = doc.createElement("version");
			versionElement.appendChild(doc.createTextNode(version));
			projectElement.appendChild(versionElement);

			Element packaging = doc.createElement("packaging");
			packaging.appendChild(doc.createTextNode("eclipse-plugin"));
			projectElement.appendChild(packaging);

			// write the content
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(pomFile);

			transformer.transform(source, result);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private static String getTagValue(String sTag, Element eElement) {
		NodeList nlList = eElement.getElementsByTagName(sTag).item(0).getChildNodes();

		Node nValue = (Node) nlList.item(0);

		return nValue.getNodeValue();
	}

	private void closeInputStream(InputStream stream) {
		try {
			stream.close();
		} catch (IOException e) {
		}
	}

	/**
	 * <p>
	 * </p>
	 * 
	 * @param icompilationUnit
	 * @throws CoreException
	 */
	private void parse(IResource resource) throws CoreException {

		// get the output file
		IFolder folder = resource.getProject().getFolder("/");
		IFile file = folder.getFile("pom.xml");

		if (!file.exists()) {

			// TODO create pom
		}

		// finally we have to refresh the local folder
		folder.refreshLocal(IResource.DEPTH_INFINITE, null);
	}
}