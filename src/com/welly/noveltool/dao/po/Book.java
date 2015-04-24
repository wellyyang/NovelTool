package com.welly.noveltool.dao.po;

import java.util.Calendar;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.welly.noveltool.util.BookBean;

@DatabaseTable
public class Book {

	@DatabaseField(generatedId = true)
	private long id;
	@DatabaseField
	private String filename;
	@DatabaseField
	private String name;
	@DatabaseField(index = true)
	private String author;
	@DatabaseField
	private boolean newest;
	@DatabaseField(defaultValue = "0")
	private int score = 0;
	@DatabaseField
	private long length;
	@DatabaseField(index = true)
	private String type;
	@DatabaseField
	private String path;
	@DatabaseField
	private long lastmodified;
	@DatabaseField
	private int year;
	@DatabaseField
	private int month;
	@DatabaseField(foreign = true
			, foreignAutoCreate = true
			, columnName = "bc_id"
//			, foreignAutoRefresh=true
			)
	private BookContent bc;

	public Book() {

	}

	public Book(BookBean bean) {
		name = bean.getBookname();
		filename = bean.getFile().getName();
		author = bean.getAuthor();
		newest = false;
		score = 0;
		length = bean.getLength();
		type = bean.getType();
		path = bean.getPath();
		lastmodified = bean.getFile().lastModified();
		year = Calendar.getInstance().get(Calendar.YEAR);
		month = Calendar.getInstance().get(Calendar.MONTH);
	}

	public Book(BookBean bean, int year, int month) {
		name = bean.getBookname();
		filename = bean.getFile().getName();
		author = bean.getAuthor();
		newest = false;
		score = 0;
		length = bean.getLength();
		type = bean.getType();
		path = bean.getPath();
		lastmodified = bean.getFile().lastModified();
		this.year = year;
		this.month = month;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public boolean isNewest() {
		return newest;
	}

	public void setNewest(boolean newest) {
		this.newest = newest;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public long getLength() {
		return length;
	}

	public void setLength(long length) {
		this.length = length;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public long getLastmodified() {
		return lastmodified;
	}

	public void setLastmodified(long lastmodified) {
		this.lastmodified = lastmodified;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public BookContent getBc() {
		return bc;
	}

	public void setBc(BookContent bc) {
		this.bc = bc;
	}
}
