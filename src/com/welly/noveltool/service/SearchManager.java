package com.welly.noveltool.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.welly.noveltool.dao.po.Book;
import com.welly.noveltool.util.SearchType;

public class SearchManager {

	// ��������
	private String cond;
	// ��ȫƥ�仹��ģ��ƥ��
	private boolean equal;
	// ��������
	private SearchType type;
	// ����
	private String score;
	// ��ǰ�ؼ���,������ѡ��Ľڵ���Ϣ
	private String currentKey;
	// ��ǰչʾ������б�
	private List<Book> bookList = new ArrayList<Book>();

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

	public SearchType getType() {
		return type;
	}

	public void setType(SearchType type) {
		this.type = type;
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

}
