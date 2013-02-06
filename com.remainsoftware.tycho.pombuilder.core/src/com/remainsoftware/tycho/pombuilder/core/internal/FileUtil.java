/*******************************************************************************
 * Copyright (c) 2013 cowwoc at bbs dot darktech dot org
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Gili (cowwoc at bbs dot darktech dot org) - initial implementation
 *     http://stackoverflow.com/users/14731/gili
 ******************************************************************************/

package com.remainsoftware.tycho.pombuilder.core.internal;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Pattern;

public class FileUtil {

	public static void main(String[] args) throws IOException {

		File file2 = new File("C:\\a\\b");
		File file = new File("C:\\a\\e");

		System.out.println(file.getAbsolutePath());
		System.out.println(FileUtil.getRelativeFile(file2, file));

	}

	/**
	 * Returns the path of one File relative to another.
	 * 
	 * @param target
	 *            the target directory
	 * @param base
	 *            the base directory
	 * @return target's path relative to the base directory
	 * @throws IOException
	 *             if an error occurs while resolving the files' canonical names
	 */
	public static String getRelativeFile(File target, File base)
			throws IOException {
		String[] baseComponents = base.getCanonicalPath().split(
				Pattern.quote(File.separator));
		String[] targetComponents = target.getCanonicalPath().split(
				Pattern.quote(File.separator));

		// skip common components
		int index = 0;
		for (; index < targetComponents.length && index < baseComponents.length; ++index) {
			if (!targetComponents[index].equals(baseComponents[index]))
				break;
		}

		StringBuilder result = new StringBuilder();
		if (index != baseComponents.length) {
			// backtrack to base directory
			for (int i = index; i < baseComponents.length; ++i)
				result.append(".." + File.separator);
		}
		for (; index < targetComponents.length; ++index)
			result.append(targetComponents[index] + File.separator);
		if (!target.getPath().endsWith("/") && !target.getPath().endsWith("\\")) {
			// remove final path separator
			result.delete(result.length() - File.separator.length(),
					result.length());
		}
		return result.toString();
	}

	/**
	 * Searches the String in the file and returns the line number or -1 if it
	 * could not be found. of one File relative to another.
	 * 
	 * @param file
	 * @param string
	 * @return the line number containing the string or -1 if no such line.
	 */
	public static int calculateLine(File file, String string) {

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
					closeInputStream(fis);
					return result;
				}
			}
		} catch (Exception e) {
		}

		return -1;
	}

	/**
	 * Closes the stream.
	 * 
	 * @param stream
	 */
	public static void closeInputStream(InputStream stream) {
		try {
			stream.close();
		} catch (IOException e) {
		}
	}
}
