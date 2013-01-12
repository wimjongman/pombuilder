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

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

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

			if (resource.getName().equals("feature.xml")) {
				parseFeature(resource);
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

		FileInputStream fisManifest = null;
		boolean fisManifestOpen = false;

		try {
			fisManifest = new FileInputStream(resource.getLocation().toFile());
			HashMap<String, String> map = new HashMap<String, String>();
			ManifestElement.parseBundleManifest(fisManifest, map);
			fisManifestOpen = true;

			IPom pom = new Pom(resource);
			pom.setVersion(map.get("Bundle-Version"));
			pom.setArtifactId(map.get("Bundle-SymbolicName"));
			pom.setGroupId(map.get("Bundle-SymbolicName"));
			pom.setParentProject(map.get("Parent-Project"));
			pom.write();
		}

		finally {
			if (fisManifestOpen) {
				closeInputStream(fisManifest);
			}
		}
	}



	private void closeInputStream(InputStream stream) {
		try {
			stream.close();
		} catch (IOException e) {
		}
	}
}