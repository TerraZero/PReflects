package tz.preflects.loader;

import java.io.File;
import java.util.zip.ZipEntry;

import tz.preflects.Reflect;
import tz.preflects.i.Reflectable;

/**
 * 
 * @author terrazero
 * @created Mar 9, 2015
 * 
 * @file Boot.java
 * @project TZS
 * @identifier TZ.System
 *
 */
public class ReflectFile implements Reflectable {
	
	public static String getZipName(String zipname) {
		String[] parts = zipname.split("/");
		String name = parts[parts.length - 1];
		
		return name.substring(0, name.length() - 6);
	}
	
	public static String getZipPath(String zipname) {
		String[] parts = zipname.split("/");
		
		return zipname.substring(0, zipname.length() - parts[parts.length - 1].length() - 1);
	}
	
	public static String getFileName(String filename) {
		return filename.substring(0, filename.length() - 6);
	}
	
	

	protected String name;
	protected String path;
	protected Reflect reflect;
	
	// source
	protected ZipEntry entry;
	protected File file;
	
	public ReflectFile(ZipEntry entry) {
		this.entry = entry;
		this.name = ReflectFile.getZipName(entry.getName());
		this.path = ReflectFile.getZipPath(entry.getName());
	}
	
	public ReflectFile(File file, String path) {
		this.file = file;
		this.name = ReflectFile.getFileName(file.getName());
		this.path = path;
	}
	
	public boolean isZip() {
		return this.entry != null;
	}
	
	public boolean isFile() {
		return this.file != null;
	}
	
	public String name() {
		return this.name;
	}
	
	public String id() {
		String file = this.path + "/" + this.name;
		return file.replace('/',  '.');
	}
	
	public Reflect reflect() {
		if (this.reflect == null) {
			this.reflect = new Reflect(this.id());
		}
		return this.reflect;
	}
	
}
