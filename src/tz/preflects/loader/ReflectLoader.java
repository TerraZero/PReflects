package tz.preflects.loader;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import tz.preflects.exceptions.LoaderException;

/**
 * 
 * @author Terra
 * @created 13.02.2015
 * 
 * @file BootLoader.java
 * @project G7C
 * @identifier TZ.Reflect.Boot
 *
 */
public class ReflectLoader {
	
	private static ReflectLoader sysloader;
	private static URLClassLoader loader;
	private static Method addMethod;
	private static URL defaultURL;
	
	public static ReflectLoader sysloader() {
		if (ReflectLoader.sysloader == null) {
			ReflectLoader.sysloader = new ReflectLoader();
		}
		return ReflectLoader.sysloader;
	}
	
	public static URLClassLoader loader() {
		if (ReflectLoader.loader == null) {
			ReflectLoader.loader = (URLClassLoader)ClassLoader.getSystemClassLoader();
			ReflectLoader.defaultURL = ReflectLoader.loader.getURLs()[0];
		}
		return ReflectLoader.loader;
	}
	
	public static void addLoaderSource(File file) {
		try {
			ReflectLoader.addLoaderSource(file.toURI().toURL());
		} catch (MalformedURLException e) {
			throw new LoaderException(e, "Couldn't transform file to URL", "Couldn't transform file to URL");
		}
	}
	
	public static void addLoaderSource(URL url) {
		if (ReflectLoader.addMethod == null) {
			try {
				ReflectLoader.addMethod = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
			} catch (NoSuchMethodException | SecurityException e) {
				throw new LoaderException(e, "Couldn't get method addURL of class loader", "Couldn't get method addURL of class loader");
			}
			ReflectLoader.addMethod.setAccessible(true);
		}
		try {
			ReflectLoader.addMethod.invoke(ReflectLoader.loader(), new Object[] {url});
		} catch (Exception e) {
			throw new LoaderException(e, "Couldn't add URL to class loader", "Couldn't add URL to class loader");
		}
	}
	
	public static URL defaultURL() {
		if (ReflectLoader.defaultURL == null) {
			ReflectLoader.loader();
		}
		return ReflectLoader.defaultURL;
	}
	
	public static String defaultPath() {
		return ReflectLoader.defaultURL().getPath();
	}
	
	protected List<ReflectFile> boots;
	
	private ReflectLoader() {
		
	}
	
	public List<ReflectFile> load() {
		return this.load(false);
	}
	
	public List<ReflectFile> load(boolean force) {
		if (force || this.boots == null) {
			this.init();
		}
		return this.boots;
	}
	
	public void init() {
		this.boots = new ArrayList<ReflectFile>(1024);
		try {
			for (URL url : ReflectLoader.loader().getURLs()) {
				String file = url.getFile();
				
				if (file.endsWith(".jar")) {
					ZipInputStream zip = new ZipInputStream(url.openStream());
					this.loadZip(this.boots, zip);
					zip.close();
				} else {
					this.loadFile(this.boots, file, "");
				}
			}
		} catch (Exception e) {
			throw new LoaderException(e, "Unexpected Exception in SysLoader", "Unexpected Exception in SysLoader");
		}
	}
	
	public void loadZip(List<ReflectFile> boots, ZipInputStream zip) throws IOException {
		ZipEntry entry = null;
		
		while ((entry = zip.getNextEntry()) != null) {
			if (entry.getName().endsWith(".class")) {
				ReflectFile file = new ReflectFile(entry);
				if (file.id().startsWith("tz")) {
					boots.add(file);
				}
			}
		}
	}
	
	public void loadFile(List<ReflectFile> boots, String path, String internpath) {
		for (File f : new File(path).listFiles()) {
			if (f.isDirectory()) {
				this.loadFile(boots, path + "/" + f.getName(), internpath + "/" + f.getName());
			} else if (f.isFile() && f.getName().endsWith(".class")) {
				ReflectFile file = new ReflectFile(f, (internpath.length() == 0 ? "" : internpath.substring(1)));
				if (file.id().startsWith("tz")) {
					boots.add(file);
				}
			}
		}
	}
	
}
