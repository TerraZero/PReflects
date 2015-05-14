package tz.preflects;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import tz.preflects.loader.ReflectFile;
import tz.preflects.loader.ReflectLoader;

/**
 * 
 * @author Terra
 * @created 19.02.2015
 * 
 * @file Reflects.java
 * @project G7C
 * @identifier TZ.Reflect
 *
 */
public class Reflects {

	public static Class<?>[] extractClasses(Object... objs) {
		Class<?>[] types = new Class<?>[objs.length];
		for (int i = 0; i < types.length; i++) {
			types[i] = objs[i].getClass();
		}
		return types;
	}

	public static boolean isParameter(Class<?>[] parameters, Class<?>[] definition) {
		return Reflects.isParameter(parameters, definition, false);
	}

	public static boolean isParameter(Class<?>[] parameters, Class<?>[] definition, boolean vararg) {
		if (!vararg && definition.length != parameters.length || vararg && parameters.length < definition.length - 1) return false;
		Class<?> para = null;
		for (int i = 0; i < parameters.length; i++) {
			if (i < definition.length) {
				para = definition[i];
				if (vararg && i + 1 == definition.length) {
					para = para.getComponentType();
				}
			}
			if (!Reflects.isClass(para, parameters[i]))
				return false;
		}
		return true;
	}

	public static Object[] getParameter(Object[] parameters, Class<?>[] definition, boolean vararg) {
		if (vararg) {
			Object[] paras = new Object[definition.length];
			Object[] vars = null;
			if (parameters.length >= definition.length) {
				vars = (Object[])Array.newInstance(definition[definition.length - 1].getComponentType(), parameters.length - paras.length + 1);
			}
			paras[paras.length - 1] = vars;
			for (int i = 0; i < parameters.length; i++) {
				if (i < definition.length - 1) {
					paras[i] = parameters[i];
				} else {
					vars[i - definition.length + 1] = parameters[i];
				}
			}
			return paras;
		} else {
			return parameters;
		}
	}

	public static boolean isFunction(Method function, Object... parameters) {
		return Reflects.isParameter(Reflects.extractClasses(parameters), function.getParameterTypes(), function.isVarArgs());
	}

	public static List<Method> searchFunctions(Class<?> base, String name) {
		List<Method> search = new ArrayList<Method>();
		Method[] methods = base.getMethods();
		
		for (int i = 0; i < methods.length; i++) {
			if (methods[i].getName().equals(name)) {
				search.add(methods[i]);
			}
		}
		return search;
	}
	
	public static Method getFunctions(Class<?> reflect, String name, Class<?>[] parameters) {
		for (Method method : reflect.getMethods()) {
			if (method.getName().equals(name) && Reflects.isParameter(parameters, method.getParameterTypes(), method.isVarArgs())) {
				return method;
			}
		}
		return null;
	}
	
	public static List<Method> getFunctions(Class<?> reflect, Class<? extends Annotation> annotation) {
		List<Method> functions = new ArrayList<Method>(16);
		Method[] methods = reflect.getMethods();
		
		for (Method method : methods) {
			if (method.isAnnotationPresent(annotation)) {
				functions.add(method);
			}
		}
		return functions;
	}

	public static Field getField(Class<?> base, String name) {
		Field[] fields = base.getFields();
		for (int i = 0; i < fields.length; i++) {
			if (fields[i].getName().equals(name)) {
				return fields[i];
			}
		}
		return null;
	}

	public static Constructor<?> getConstructor(Class<?> base, Class<?>[] parameters) {
		Constructor<?>[] constructors = base.getConstructors();
		for (int i = 0; i < constructors.length; i++) {
			if (Reflects.isParameter(parameters,
					constructors[i].getParameterTypes(),
					constructors[i].isVarArgs()))
				return constructors[i];
		}
		return null;
	}

	public static Class<?> getClassOfPrimitive(Class<?> primitiveClass) {
		if (primitiveClass.isAssignableFrom(boolean.class)) return Boolean.class;
		if (primitiveClass.isAssignableFrom(float.class)) return Float.class;
		if (primitiveClass.isAssignableFrom(void.class)) return Void.class;
		if (primitiveClass.isAssignableFrom(int.class)) return Integer.class;
		if (primitiveClass.isAssignableFrom(short.class)) return Short.class;
		if (primitiveClass.isAssignableFrom(double.class)) return Double.class;
		if (primitiveClass.isAssignableFrom(long.class)) return Long.class;
		if (primitiveClass.isAssignableFrom(byte.class))return Byte.class;
		return primitiveClass;
	}

	public static Class<?> getPrimitiveOfClass(Class<?> type) {
		if (type.isAssignableFrom(Boolean.class)) return boolean.class;
		if (type.isAssignableFrom(Float.class)) return float.class;
		if (type.isAssignableFrom(Void.class)) return void.class;
		if (type.isAssignableFrom(Integer.class)) return int.class;
		if (type.isAssignableFrom(Short.class)) return short.class;
		if (type.isAssignableFrom(Double.class)) return double.class;
		if (type.isAssignableFrom(Long.class)) return long.class;
		if (type.isAssignableFrom(Byte.class)) return byte.class;
		return type;
	}

	public static boolean isClass(Class<?> from, Class<?> to) {
		if (from.isPrimitive() || to.isPrimitive()) {
			from = Reflects.getClassOfPrimitive(from);
			to = Reflects.getClassOfPrimitive(to);
		}
		return from.isAssignableFrom(to);
	}

	public static boolean isStatic(Field field) {
		return Modifier.isStatic(field.getModifiers());
	}

	public static boolean isStatic(Method method) {
		return Modifier.isStatic(method.getModifiers());
	}

	public static boolean isType(Class<?> type, Object object) {
		return type.isInstance(object);
	}

	public static <type> type cast(Class<type> type, Object object) {
		if (Reflects.isType(type, object)) {
			return type.cast(object);
		} else {
			return null;
		}
	}
	
	public static boolean isImplement(Class<?> base, Class<?> implement) {
		return !base.isInterface() && !base.isAnnotation() && !base.isEnum() && !Modifier.isAbstract(base.getModifiers()) && implement.isAssignableFrom(base);
	}
	
	public static<annot extends Annotation> void forAnnotation(Class<annot> annotation, Consumer<InfoWrapper<annot>> consumer) {
		for (ReflectFile file : ReflectLoader.sysloader().load()) {
			annot annot = file.reflect().getAnnotation(annotation);
			if (annot != null) {
				consumer.accept(new InfoWrapper<annot>(file.reflect(), annot));
			}
		}
	}

}
