package com.welly.noveltool.util;

/**
 * ��������,Ŀǰ�����ߺ���������
 * @author welly
 *
 */
public enum SearchType {
	AUTHOR, TYPE, SCORE, NAME, DATE, FAVORITE_AUTHOR;
	
	@Override
	public String toString() {
		return super.toString().toLowerCase();
	}
}
