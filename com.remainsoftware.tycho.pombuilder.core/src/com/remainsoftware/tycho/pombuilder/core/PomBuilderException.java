package com.remainsoftware.tycho.pombuilder.core;

public class PomBuilderException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5878306230853377558L;

	public PomBuilderException(Exception e) {
		super(e);
	}

	public PomBuilderException(String string) {
		super(string);
	}
}
