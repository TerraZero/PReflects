package tz.preflects;

import java.lang.annotation.Annotation;

import tz.preflects.api.Reflectable;

public class InfoWrapper<annot extends Annotation> implements Reflectable {
	
	private Reflect reflect;
	private annot annotation;
	
	public InfoWrapper(Reflect reflect, annot annotation) {
		this.reflect = reflect;
		this.annotation = annotation;
	}

	@Override
	public Reflect reflect() {
		return this.reflect;
	}
	
	public annot annotation() {
		return this.annotation;
	}

}
