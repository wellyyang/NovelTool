package com.welly.noveltool.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.welly.noveltool.dao.po.Book;
import com.welly.noveltool.util.FavoriteAutherType;
import com.welly.noveltool.util.SearchType;

public class SearchManager {
	
	private static SearchManager sm = new SearchManager();
	// 检索条件
	private String cond;
	// 完全匹配还是模糊匹配
	private boolean equal;
	// 检索类型
	private SearchType mainType;
	// 评分
	private String score;
	// 收藏作者类型:所有,已收藏,未收藏
	private FavoriteAutherType favoriteAuthorType;
	// 当前关键词,即树上选择的节点信息
	private String currentKey;
	// 当前展示的书的列表
	private List<Book> bookList = new ArrayList<Book>();
	// 当前选择的导入路径
	private String path;
	// 当前左侧展示的关键词
	private String[] keys;
	
	private SearchManager(){
		
	}
	
	public static SearchManager getInstanse(){
		return sm;
	}

	public String getCond() {
		return cond;
	}

	public void setCond(String cond) {
		this.cond = cond;
	}

	public boolean isEqual() {
		return equal;
	}

	public void setEqual(boolean equal) {
		this.equal = equal;
	}

	public SearchType getMainType() {
		return mainType;
	}

	public void setMainType(SearchType type) {
		this.mainType = type;
	}

	public String getScore() {
		return score;
	}

	public void setScore(String score) {
		this.score = score;
	}

	public List<Book> getBookList() {
		return Collections.unmodifiableList(bookList);
	}

	public String getCurrentKey() {
		return currentKey;
	}

	public void setCurrentKey(String currentKey) {
		this.currentKey = currentKey;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public FavoriteAutherType getFavoriteAuthorType() {
		return favoriteAuthorType;
	}

	public void setFavoriteAuthorType(FavoriteAutherType favoriteAuthorType) {
		this.favoriteAuthorType = favoriteAuthorType;
	}

	public String[] getKeys() {
		return keys;
	}

}
