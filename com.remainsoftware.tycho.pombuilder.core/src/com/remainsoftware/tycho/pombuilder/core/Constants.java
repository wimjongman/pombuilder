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

import org.osgi.framework.FrameworkUtil;

public class Constants {

	public static final String CORE = FrameworkUtil.getBundle(Constants.class).getSymbolicName();

	public static final String BUILDER_ID = CORE + "." + "pombuilder";

	public static final String POM_NATURE = CORE + "." + "pomnature";

}
