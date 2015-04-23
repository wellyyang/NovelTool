package com.welly.noveltool.util;

import java.io.File;

/**
 * txt�ļ���Ϣbean
 * @author welly
 *
 */
public class BookBean implements Comparable<BookBean>{
	
	private File f;
	// ����
	private String author;
	// ����
	private String bookname;
	// ԭʼ����
	private String type;
	// ��������
	private String[] types;
	// �ļ���С bytes
	private long length;
	// �ļ�����·��
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
			// ������������ͬ����Ϊ��ͬһ����
			BookBean book = (BookBean) obj;
			if (this.getAuthor().equalsIgnoreCase(book.getAuthor()) && 
					this.getBookname().equalsIgnoreCase(book.getBookname())){
				return true;
			}
		}
		return false;
	}

	// ������������ͬ����Ϊ��ͬһ����
	@Override
	public int hashCode() {
		return author.toLowerCase().hashCode() * 37 + bookname.toLowerCase().hashCode();
	}

	/**
	 * ��ͬtxt,�����ļ���С��������
	 * ������������ͬ,�����ļ���С��������
	 * ������������ͬ,�ļ���С��ͬ,��������޸�ʱ�併������
	 * ���򷵻�0
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
