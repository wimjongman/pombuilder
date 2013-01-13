/*******************************************************************************
 * Copyright (c) 2013 Remain Software and others http://remainsoftware.com
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Wim Jongman - initial API and implementation
 ******************************************************************************/
package com.remainsoftware.tycho.pombuilder.core;

import java.io.File;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;

/**
 * Tycho pom file model. This interface is not intended to be implemented by
 * clients.
 * 
 * @author Wim Jongman
 * 
 */
public interface IPom {

	/**
	 * @param version
	 * @return this
	 */
	IPom setVersion(String version);

	/**
	 * @see #setVersion(String)
	 * 
	 */
	String getVersion();

	/**
	 * Sets the artifactId which is the bundle symbolic name, the feature name,
	 * the product id etc.
	 * 
	 * @param artifactId
	 * @return this
	 */
	IPom setArtifactId(String artifactId);

	/**
	 * @see #setArtifactId(String)
	 * 
	 */
	String getArtifactId();

	/**
	 * Sets the maven group id which is probably the same as the artifcatId.
	 * 
	 * @param groupId
	 * @return this
	 * 
	 * @see #setArtifactId(String)
	 */
	IPom setGroupId(String groupId);

	/**
	 * @see #setGroupId(String)
	 * 
	 */
	String getGroupId();

	/**
	 * Sets all the required parent attributes based on the pom.xml that the
	 * passed parameter points to and returns that pom. The project is just the
	 * simple projectname, not an URL or anything.
	 * 
	 * @param project
	 *            the project that contains the parent pom
	 * @throws PomBuilderException
	 *             if the project does not exist
	 * @return the parent pom
	 */
	IPom setgetParentProject(String project) throws PomBuilderException;

	/**
	 * @return the {@link IPom} that is the parent of this pom. Could be null if
	 *         {@link #setParentProject(String)} was never called.
	 * @see #setParent(String)
	 */
	IPom getParent();

	/**
	 * Sets the packaging type.
	 * 
	 * @param packaging
	 *            the maven packaging type
	 *            (http://wiki.eclipse.org/Tycho/Packaging_Types).
	 * @return this
	 */
	IPom setPackaging(String packaging);

	/**
	 * @see #setPackaging(String)
	 */
	String getPackaging();

	/**
	 * 
	 * @return the {@link File} that backs this {@link IPom}. Can not be null.
	 */
	File getFile();

	/**
	 * Writes the pom.xml
	 * 
	 * @return
	 * @throws PomBuilderException
	 */
	IPom write() throws PomBuilderException;

	/**
	 * Sets the model version.
	 * 
	 * @param modelVersion
	 *            the maven model version.
	 * @return this
	 */
	IPom setModelVersion(String modelVersion);

	/**
	 * @return the model version
	 * @see #getModelVersion()
	 */
	String getModelVersion();

	/**
	 * 
	 * @return the project where this pom is located
	 */
	IProject getProject();

	/**
	 * 
	 * @return the resource representing this pom
	 */
	IResource getResource();
}
