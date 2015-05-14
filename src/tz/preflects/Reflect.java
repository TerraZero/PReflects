package tz.preflects;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import tz.core.logger.Log;
import tz.preflects.exceptions.ReflectException;
import tz.preflects.loader.ReflectLoader;

/**
 * 
 * @author Terra
 * @created 19.02.2015
 * 
 * @file Reflect.java
 * @project G7C
 * @identifier TZ.Reflect
 *
 */
public class Reflect {

	protected Class<?> reflectClass;
	protected Object reflect;
	
	public Reflect() {
		
	}
	
	public Reflect(String load) {
		this.reflect(load);
	}
	
	public Reflect(Object reflect) {
		this.reflect(reflect);
	}
	
	public Reflect(Class<?> reflectClass, Object reflect) {
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
	
	/**
	 * Load a reflecttype for this reflect
	 * 
	 * @param load - the name of the class
	 * @throws ReflectException ON ClassNotFoundException
	 * @return THIS
	 */
	public Reflect reflect(String load) {
		try {
			// TODO einen eigenen classloader erzeugen
			this.reflectClass = ReflectLoader.loader().loadClass(load);
			this.reflect = null;
		} catch (ClassNotFoundException e) {
			throw new ReflectException(e, "reflect", "reflect", this);
		} catch (NoClassDefFoundError e) {
			Log.warning(Log.ident("Reflect", "load"), "Class [0] will be ignored.", load);
		}
		return this;
	}
	
	public Reflect reflect(Object reflect) {
		this.reflect = reflect;
		this.reflectClass = reflect.getClass();
		return this;
	}
	
	public String name() {
		return this.reflectClass.getSimpleName();
	}
	
	/**
	 * DEFAULT-PARAM force = true
	 * 
	 * @see instantiate(boolean, Object...)
	 * @param args - args for the constructor
	 * @return THIS
	 */
	public Reflect instantiate(Object... args) {
		return this.instantiate(false, args);
	}
	
	/**
	 * Create a NEW object of reflecttype into THIS object
	 * 
	 * @see Reflects.getConstructor(Class<?>, Class<?>[])
	 * @see Reflects.extractClasses(Object...)
	 * @see Reflects.getParameter(Object[], Class<?>[], boolean)
	 * @param force - IF already instantiate THAN create a NEW object
	 * @param args - args for the constructor
	 * @throws ReflectException ON InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
	 * @return THIS
	 */
	public Reflect instantiate(boolean force, Object... args) {
		if (this.reflect == null || force) {
			try {
				if (args.length == 0) {
					this.reflect = this.reflectClass.newInstance();
				} else {
					Constructor<?> c = Reflects.getConstructor(this.reflectClass, Reflects.extractClasses(args));
					this.reflect = c.newInstance(Reflects.getParameter(args, c.getParameterTypes(), c.isVarArgs()));
				}
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				throw new ReflectException(e, "instantiate", "instantiate", this);
			}
		}
		return this;
	}
	
	
	/**
	 * Invoke a method of reflecttype(static) or reflectobject(non-static) 
	 * 
	 * @see Reflects.getFunctions(Class<?>, String, Class<?>[])
	 * @see Reflects.extractClasses(Object...)
	 * @param function - the method name
	 * @param parameters - params for the method
	 * @throws ReflectException ON SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
	 * @return THIS
	 */
	@SuppressWarnings("unchecked")
	public<type> type call(String function, Object... parameters) {
		try {
			Method method = Reflects.getFunctions(this.reflectClass, function, Reflects.extractClasses(parameters));
			return (type)method.invoke(this.reflect, parameters);
		} catch (SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new ReflectException(e, "call", "call", this);
		}
	}
	
	/**
	 * Invoke all methods with the defined annotation
	 * 
	 * @see Reflects.getFunctions(Class<?>, Class<? extends Annotation>)
	 * @param annotation - the defined annotation
	 * @param parameters - the params for the invoke methods
	 * @throws ReflectException ON IllegalAccessException | IllegalArgumentException | InvocationTargetException
	 * @return THIS
	 */
	public Reflect call(Class<? extends Annotation> annotation, Object... parameters) {
		List<Method> functions = Reflects.getFunctions(this.reflectClass, annotation);
		
		if (!functions.isEmpty()) {
			for (Method function : functions) {
				try {
					function.invoke(this.reflect, parameters);
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					throw new ReflectException(e, "call", "call", this);
				}
			}
		}
		return this;
	}
	
	public ReflectField field(String field) {
		try {
			return new ReflectField(this.reflectClass.getField(field), this.reflectClass, this.reflect);
		} catch (NoSuchFieldException | SecurityException e) {
			throw new ReflectException(e, "get", "get", this);
		}
	}
	
	public <annot extends Annotation> List<ReflectField> fields(Class<annot> annotation) {
		List<ReflectField> fields = new ArrayList<ReflectField>();
		for (Field field : this.reflectClass.getFields()) {
			if (field.isAnnotationPresent(annotation)) {
				fields.add(new ReflectField(field, this.reflectClass, this.reflect));
			}
		}
		return fields;
	}
	
	/**
	 * Get the value of a field from reflecttype(static) or reflectobject(non-static)
	 * 
	 * @param field - the field name
	 * @throws ReflectException ON IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException
	 * @return the value of the field
	 */
	@SuppressWarnings("unchecked")
	public<type> type get(String field) {
		try {
			return (type)this.reflectClass.getField(field).get(this.reflect);
		} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
			throw new ReflectException(e, "get", "get", this);
		}
	}
	
	/**
	 * Set the value of a field from reflecttype(static) or reflectobject(non-static)
	 * 
	 * @param field - the field name
	 * @param set - the value to set
	 * @throws ReflectException ON IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException
	 * @return THIS
	 */
	public Reflect set(String field, Object set) {
		try {
			this.reflectClass.getField(field).set(this.reflect, set);
		} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
			throw new ReflectException(e, "set", "set", this);
		}
		return this;
	}
	
	
	
	public <annot extends Annotation> annot getAnnotation(Class<annot> annotation) {
		if (this.hasAnnotation(annotation)) {
			return this.reflectClass.getAnnotation(annotation);
		}
		return null;
	}
	
	public <annot extends Annotation> annot annotation(Class<annot> annotation) {
		return this.reflectClass.getAnnotation(annotation);
	}
	
	public boolean hasAnnotation(Class<? extends Annotation> annotation) {
		return this.reflectClass.isAnnotationPresent(annotation);
	}
	
	
	
	public boolean implement(Class<?> implement) {
		return Reflects.isImplement(this.reflectClass, implement);
	}
	
}
