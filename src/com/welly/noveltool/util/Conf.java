package com.welly.noveltool.util;

import java.io.File;
import java.io.FileInputStream;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

public class Conf {

	private static ResourceBundle bundle;
	
	private static ResourceBundle local = ResourceBundle.getBundle("conf");
	
	static {
		String root = FileUtil.getRootPath();
		File conf = new File(root, "conf.properties");
		if (conf.exists()){
			try {
				bundle = new PropertyResourceBundle(new FileInputStream(conf));
			} catch (Exception e) {
				e.printStackTrace();
				bundle = null;
			}
		}
	}
	
	public static int getPagecount(){
		try {
			return Integer.parseInt(bundle.getString("page_count"));
		} catch (Exception e) {
			return Integer.parseInt(local.getString("page_count"));
		}
	}
	
	public static String getDbpath(){
		try {
			if (getUseAbsPath()){
				return bundle.getString("db_path");
			} else {
				return FileUtil.getRootPath() + File.separator + "book.db";
			}
		} catch (Exception e) {
			if (getUseAbsPath()){
				return local.getString("db_path");
			} else {
				return FileUtil.getRootPath() + File.separator + "book.db";
			}
		}
	}
	
	public static Boolean getUseAbsPath(){
		try {
			return Boolean.parseBoolean(bundle.getString("use_abs_path"));
		} catch (Exception e) {
			return Boolean.parseBoolean(local.getString("use_abs_path"));
		}
	}
	
	public static int getImportTimeout(){
		try {
			return Integer.parseInt(bundle.getString("import_timeout"));
		} catch (Exception e) {
			return Integer.parseInt(local.getString("import_timeout"));
		}
	}
}
