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
			throw new CoreException(new Status(IStatus.ERROR, Constants.CORE,
					e.getMessage(), e));
		}

	}

	private void parseProduct(IResource resource) {
		// TODO Auto-generated method stub

	}

	private void parseFeature(IResource resource) {
		// TODO Auto-generated method stub

	}

	private void parseManifest(IResource resource) throws CoreException {
		new ManifestParser().parse(resource);
	}
}