package tz.preflects.exceptions;

import tz.core.exceptions.TZException;
import tz.preflects.Reflect;


/**
 * 
 * @author Terra
 * @created 19.02.2015
 * 
 * @file ReflectException.java
 * @project G7C
 * @identifier TZ.Reflect.Exception
 *
 */
public class ReflectException extends TZException {

	private static final long serialVersionUID = 1L;
	
	protected Reflect reflect;

	public ReflectException(String message, String debug) {
		super(message, debug);
	}

	public ReflectException(Exception e, String message, String debug) {
		super(e, message, debug);
	}
	
	public ReflectException(Exception e, String message, String debug, Reflect reflect) {
		super(e, message, debug);
		this.reflect = reflect;
	}
	
	public Reflect reflect() {
		return this.reflect;
	}

}
