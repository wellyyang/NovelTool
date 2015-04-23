package com.welly.noveltool.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 文件名解析工具
 * @author welly
 *
 */
public class FilenameParser {
	
	// 正常文件
	private static Pattern fullPattern;
	// 无作者文件
	private static Pattern noAutherPattern;
	// 无类型文件
	private static Pattern noTypePattern;
	// 只有书名的文件
	private static Pattern onlyNamePattern;
	
	static {
		// group1为书名,group2为作者,group3为类型标签
		fullPattern = Pattern.compile("^.*《(.*)》.*" +
				"(?:[Bb][Yy]|作者)\\s*[：:]?[\\s]*([\\s\\S&&[^\\[\\(【]]*)" + 
				".*[\\(（](.*)[\\)）].*$");
		// group1为书名,group2为类型标签
		noAutherPattern = Pattern.compile("^.*《(.*)》.*" +
				"[\\s]*[\\(（](.*)[\\)）].*$");
		// group1为书名,group2为作者标签
		noTypePattern = Pattern.compile("^.*《(.*)》.*" +
				"(?:[Bb][Yy]|作者)\\s*[：:]?[\\s]*([\\s\\S&&[^\\[\\(【]]*).*$");
		// group1为书名
		onlyNamePattern = Pattern.compile("^.*《(.*)》.*$");
	}
	
	/**
	 * 获取作者
	 * @param filename
	 * @return
	 */
	public static String getAuthor(String filename){
		filename = removeExt(filename);
		Matcher m = fullPattern.matcher(filename);
		if (m.matches()){
			return m.group(2).trim();
		}
		m = noTypePattern.matcher(filename);
		if (m.matches()){
			return m.group(2).trim();
		}
		return "None";
	}
	
	/**
	 * 获取原始类型
	 * @param filename
	 * @return
	 */
	public static String getType(String filename){
		filename = removeExt(filename);
		Matcher m = fullPattern.matcher(filename);
		if (m.matches()){
			return m.group(3).trim();
		}
		m = noAutherPattern.matcher(filename);
		if (m.matches()){
			return m.group(2).trim();
		} 
		return "None";
	}
	
	/**
	 * 获取所有类型
	 * @param filename
	 * @return
	 */
	public static String[] getTypes(String filename){
		filename = removeExt(filename);
		Matcher m = fullPattern.matcher(filename);
		if (m.matches()){
			String type = m.group(3).trim();
			String[] types = type.split("\\s+");
			return types;
		}
		m = noAutherPattern.matcher(filename);
		if (m.matches()){
			String type = m.group(2).trim();
			String[] types = type.split("\\s+");
			return types;
		}
		return new String[]{"None"};
	}
	
	/**
	 * 获取书名
	 * @param filename
	 * @return
	 */
	public static String getBookname(String filename){
		filename = removeExt(filename);
		Matcher m = fullPattern.matcher(filename);
		if (m.matches()){
			return m.group(1).trim();
		} 
		m = noAutherPattern.matcher(filename);
		if (m.matches()){
			return m.group(1).trim();
		} 
		m = noTypePattern.matcher(filename);
		if (m.matches()){
			return m.group(1).trim();
		}
		m = onlyNamePattern.matcher(filename);
		if (m.matches()){
			return m.group(1).trim();
		}
		return "None";
	}
	
	/**
	 * 移除结尾的扩展名
	 * @param filename
	 * @return
	 */
	private static String removeExt(String filename){
		int dotIndex = filename.lastIndexOf('.');
		if (dotIndex > 0){
			return filename.substring(0, dotIndex);
		}
		return filename;
	}
	
	public static boolean isTypical(String filename){
		return fullPattern.matcher(filename).matches()
			|| noAutherPattern.matcher(filename).matches()
			|| noTypePattern.matcher(filename).matches()
			|| onlyNamePattern.matcher(filename).matches();
	}
	
	public static void main(String[] args) {
		String s = "《神体争夺战之柴犬受孕记》BY锅子阿晴／疯子阿晴／春天疯晴 (abc ddd)【0206更新番外】.txt";
		System.out.println(s);
		System.out.println(getBookname(s));
		System.out.println(getAuthor(s));
		System.out.println(getType(s));
		System.out.println(isTypical(s));
	}
}
