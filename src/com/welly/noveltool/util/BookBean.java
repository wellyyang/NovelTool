package com.welly.noveltool.util;

import java.io.File;

/**
 * txt文件信息bean
 * @author welly
 *
 */
public class BookBean implements Comparable<BookBean>{
	
	private File f;
	// 作者
	private String author;
	// 书名
	private String bookname;
	// 原始类型
	private String type;
	// 所有类型
	private String[] types;
	// 文件大小 bytes
	private long length;
	// 文件绝对路径
	private String path;
	
	private int id;

	public BookBean(File f){
		this.f = f;
		author = FilenameParser.getAuthor(f.getName());
		bookname = FilenameParser.getBookname(f.getName());
		type = FilenameParser.getType(f.getName());
		types = FilenameParser.getTypes(f.getName());
		length = f.length();
		path = f.getAbsolutePath();
	}
	
	public String getAuthor(){
		return author;
	}
	
	public String getBookname(){
		return bookname;
	}
	
	public String getType(){
		return type;
	}
	
	public String[] getTypes(){
		return types;
	}
	
	public File getFile(){
		return f;
	}
	
	public long getLength() {
		return length;
	}
	
	public String getPath() {
		return path;
	}
	
	public int getId(){
		return id;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null){
			return false;
		}
		if (obj instanceof BookBean){
			if (obj == this){
				return true;
			}
			// 书名和作者相同即认为是同一本书
			BookBean book = (BookBean) obj;
			if (this.getAuthor().equalsIgnoreCase(book.getAuthor()) && 
					this.getBookname().equalsIgnoreCase(book.getBookname())){
				return true;
			}
		}
		return false;
	}

	// 书名和作者相同即认为是同一本书
	@Override
	public int hashCode() {
		return author.toLowerCase().hashCode() * 37 + bookname.toLowerCase().hashCode();
	}

	/**
	 * 不同txt,按照文件大小降序排列
	 * 书名和作者相同,按照文件大小降序排列
	 * 书名和作者相同,文件大小相同,按照最后修改时间降序排列
	 * 否则返回0
	 */
	@Override
	public int compareTo(BookBean o) {
		if (!this.equals(o)){
			return f.length() < o.getFile().length()? 1: -1;
		}
		
		if (f.length() != o.getFile().length()){
			return f.length() < o.getFile().length()? 1: -1;
		}
		
		if (f.lastModified() != o.getFile().lastModified()){
			return f.lastModified() < o.getFile().lastModified()? 1: -1;
		}
		
		return 0;
	}
	
	@Override
	public String toString() {
		return this.path;
	}

}
