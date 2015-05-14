package tz.preflects;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import tz.core.logger.Log;

public class ReflectField {

	protected Field field;
	protected Class<?> reflectClass;
	protected Object reflect;
	
	public ReflectField(Field field, Class<?> reflectClass, Object reflect) {
		this.field = field;
		this.reflectClass = reflectClass;
		this.reflect = reflect;
	}
	
	public Class<?> reflect() {
		return this.reflectClass;
	}
	
	@SuppressWarnings("unchecked")
	public<type> type getReflect() {
		return (type)this.reflect;
	}
	
	public Class<?> type() {
		return this.field.getType();
	}
	
	public String name() {
		return this.field.getName();
	}
	
	
	
	public <annot extends Annotation> annot getAnnotation(Class<annot> annotation) {
		if (this.hasAnnotation(annotation)) {
			return this.field.getAnnotation(annotation);
		}
		return null;
	}
	
	public <annot extends Annotation> annot annotation(Class<annot> annotation) {
		return this.field.getAnnotation(annotation);
	}
	
	public boolean hasAnnotation(Class<? extends Annotation> annotation) {
		return this.field.isAnnotationPresent(annotation);
	}
	
	
	
	@SuppressWarnings("unchecked")
	public<type> type get() {
		try {
			return (type)this.field.get(this.reflect);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			Log.fatal(Log.ident("Reflect", "Field"), "Can not get the value from the field [0]", this.field + "");
			return null;
		}
	}
	
	public void set(Object value) {
		try {
			this.field.set(this.reflect, value);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			Log.fatal(Log.ident("Reflect", "Field"), "Can not set the value of the field [0] to [1]", this.field + "", value + "");
		}
	}
	
	public Reflect backReflect() {
		return new Reflect(this.reflectClass, this.reflect);
	}
	
}
