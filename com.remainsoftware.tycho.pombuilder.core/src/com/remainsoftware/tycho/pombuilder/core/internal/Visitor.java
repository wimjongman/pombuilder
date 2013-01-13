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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osgi.util.ManifestElement;

import com.remainsoftware.tycho.pombuilder.core.Constants;
import com.remainsoftware.tycho.pombuilder.core.IPom;
import com.remainsoftware.tycho.pombuilder.core.PomBuilderException;

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

		try {

			if (resource.getName().equals("MANIFEST.MF")) {
				parseManifest(resource);
				return;
			}

			if (resource.getName().endsWith(".product")) {
				parseProduct(resource);
				return;
			}
		} catch (Exception e) {
			throw new CoreException(new Status(IStatus.ERROR, Constants.CORE, e.getMessage(), e));
		}

	}

	private void parseProduct(IResource resource) {
		// TODO Auto-generated method stub

	}

	private void parseFeature(IResource resource) {
		// TODO Auto-generated method stub

	}

	private void parseManifest(IResource resource) throws Exception {

		// remove previous markers
		IMarker[] markers = resource.findMarkers(Constants.POM_PROBLEM_MARKER, false, IResource.DEPTH_ZERO);
		for (IMarker marker : markers) {
			marker.delete();
		}

		FileInputStream fisManifest = null;
		boolean fisManifestOpen = false;

		try {
			fisManifest = new FileInputStream(resource.getLocation().toFile());
			HashMap<String, String> map = new HashMap<String, String>();
			ManifestElement.parseBundleManifest(fisManifest, map);
			fisManifestOpen = true;

			IPom pom = new Pom(resource);
			pom.setModelVersion("4.0.0");
			pom.setVersion(map.get("Bundle-Version"));
			pom.setArtifactId(map.get("Bundle-SymbolicName"));
			pom.setGroupId(map.get("Bundle-SymbolicName"));
			pom.setPackaging("eclipse-plugin");

			if (map.get("Parent-Project") == null || map.get("Parent-Project").trim().length() == 0) {
				IMarker marker = resource.createMarker(Constants.POM_PROBLEM_MARKER);
				marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
				marker.setAttribute(IMarker.MESSAGE, "Variable '" + Constants.PARENT_PROJECT
						+ "' has not been defined.");
				marker.setAttribute(IMarker.LINE_NUMBER, 1);
			} else {
				try {
					pom.setgetParentProject(map.get("Parent-Project"));
				} catch (PomBuilderException e) {
					IMarker marker = resource.createMarker(Constants.POM_PROBLEM_MARKER);
					marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_WARNING);
					marker.setAttribute(IMarker.MESSAGE, e.getMessage());
					marker.setAttribute(IMarker.LINE_NUMBER,
							calculateLine(resource.getLocation().toFile(), "Parent-Project"));

				}
			}

			pom.write();
		}

		finally {
			if (fisManifestOpen) {
				closeInputStream(fisManifest);
			}
		}
	}

	private int calculateLine(File file, String string) {

		try {

			FileInputStream fis = new FileInputStream(file);
			InputStreamReader reader = new InputStreamReader(fis);
			BufferedReader lineReader = new BufferedReader(reader);
			int result = 0;
			while (lineReader.ready()) {
				result++;
				String line = lineReader.readLine();
				if (line.startsWith(string)) {
					lineReader.close();
					reader.close();
					fis.close();
					return result;
				}
			}
		} catch (Exception e) {
		}

		return 1;
	}

	private void closeInputStream(InputStream stream) {
		try {
			stream.close();
		} catch (IOException e) {
		}
	}
}