package com.welly.noveltool.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.welly.noveltool.dao.po.Book;
import com.welly.noveltool.util.FavoriteAutherType;
import com.welly.noveltool.util.SearchType;

public class SearchManager {
	
	private static SearchManager sm = new SearchManager();
	// ��������
	private String cond;
	// ��ȫƥ�仹��ģ��ƥ��
	private boolean equal;
	// ��������
	private SearchType mainType;
	// ����
	private String score;
	// �ղ���������:����,���ղ�,δ�ղ�
	private FavoriteAutherType favoriteAuthorType;
	// ��ǰ�ؼ���,������ѡ��Ľڵ���Ϣ
	private String currentKey;
	// ��ǰչʾ������б�
	private List<Book> bookList = new ArrayList<Book>();
	// ��ǰѡ��ĵ���·��
	private String path;
	// ��ǰ���չʾ�Ĺؼ���
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
