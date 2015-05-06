package tz.preflects.exceptions;

import tz.core.exceptions.TZException;

/**
 * 
 * @author terrazero
 * @created Mar 9, 2015
 * 
 * @file BootLoaderException.java
 * @project TZS
 * @identifier TZ.System.Exception
 *
 */
public class LoaderException extends TZException {

	private static final long serialVersionUID = 1L;

	public LoaderException(Exception e, String message, String debug) {
		super(e, message, debug);
	}

}
