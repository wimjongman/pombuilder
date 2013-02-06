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
package com.remainsoftware.tycho.pombuilder.core;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

/**
 * The parser interface enables specialization for different types of parsers.
 * 
 * @author Wim Jongman
 * 
 */
public interface IParser {

	/**
	 * Parses the resource.
	 * 
	 * @param resource
	 * @throws CoreException 
	 */
	void parse(IResource resource) throws CoreException;

}
