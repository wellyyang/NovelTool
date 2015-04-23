package com.welly.noveltool.util;

import java.awt.Component;
import java.util.HashSet;
import java.util.Set;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * ui外观修改工具,目前rt.jar中仅保留了METAL
 * @author welly
 *
 */
public class LookAndFeelUtil {
	
	private LookAndFeelUtil(){
		
	}
	
	public static String METAL = "javax.swing.plaf.metal.MetalLookAndFeel";
	public static String WINDOWS = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";
	public static String WINDOWS_CLASSIC = "com.sun.java.swing.plaf.windows.WindowsClassicLookAndFeel";
	public static String MOTIF = "com.sun.java.swing.plaf.motif.MotifLookAndFeel";
//	public static String MAC = "com.sun.java.swing.plaf.mac.MacLookAndFeel";
//	public static String GTK = "com.sun.java.swing.plaf.gtk.GTKLookAndFeel";
	public static String CROSS_PLANTFORM = UIManager.getCrossPlatformLookAndFeelClassName();
	public static String DEFAULT = UIManager.getSystemLookAndFeelClassName();
	
	private static Set<String> set = new HashSet<String>();
	
	static {
		set.add(METAL);
		set.add(WINDOWS);
		set.add(WINDOWS_CLASSIC);
		set.add(MOTIF);
//		set.add(MAC);
//		set.add(GTK);
		set.add(CROSS_PLANTFORM);
		set.add(DEFAULT);
	}

	/**
	 * 设置外观
	 * @param lookAndFeel 外观类型,请使用LookAndFeelUtil中常量,自定义类型如设置失败,将使用METAL
	 * @param component 待设置ui的父组件
	 */
	public static void setLookAndFeel(String lookAndFeel, Component component){
		try {
			if (!set.contains(lookAndFeel)){
				lookAndFeel = METAL;
			}
			UIManager.setLookAndFeel(lookAndFeel);
			SwingUtilities.updateComponentTreeUI(component);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
