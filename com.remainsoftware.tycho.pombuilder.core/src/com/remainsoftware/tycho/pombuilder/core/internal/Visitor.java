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
import java.io.StringBufferInputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.osgi.util.ManifestElement;
import org.osgi.framework.BundleException;

import com.remainsoftware.tycho.pombuilder.core.Constants;

/**
 * <p>
 * </p>
 * 
 * @author Gerd W&uuml;therich (gerd@gerd-wuetherich.de)
 */
public class Visitor implements IResourceVisitor, IResourceDeltaVisitor {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean visit(IResource resource) throws CoreException {

		//
		if (resource.getType() != IResource.FILE) {
			return true;
		}

		//
		handle(resource);

		//
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean visit(IResourceDelta delta) throws CoreException {

		//
		if (delta.getKind() == IResourceDelta.ADDED) {

			//
			return visit(delta.getResource());

		} else if (delta.getKind() == IResourceDelta.REMOVED) {

			//
			// GeneratedComponentDescriptionsStore.deleteGeneratedFiles(delta.getResource().getProject(),
			// delta.getResource()
			// .getFullPath());

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

		//
		if (!resource.getName().equals("MANIFEST.MF")) {
			return;
		}

		parseManifest(resource);

	}

	private void parseManifest(IResource resource) {

		FileInputStream fisManifest = null;
		boolean fisManifestOpen = false;

		FileInputStream fisPom = null;
		boolean fisPomOpen = false;

		try {

			fisManifest = new FileInputStream(resource.getLocation().toFile());
			HashMap<String, String> map = new HashMap<String, String>();
			ManifestElement.parseBundleManifest(fisManifest, map);
			fisManifestOpen = true;
			
			writePomBundle(map.get("Bundle-Version"), map.get("Bundle-SymbolicName"));

			System.out.println(map);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		finally {
			if (fisManifestOpen) {
				closeInputStream(fisManifest);
			}
			if (fisPomOpen) {
				closeInputStream(fisPom);
			}
		}
	}

	private void writePomBundle(String version, String manifest) {
		// TODO Auto-generated method stub
		
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