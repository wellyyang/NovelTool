package com.welly.noveltool.util;

/**
 * ��������,Ŀǰ�����ߺ���������
 * @author welly
 *
 */
public enum SearchType {
	AUTHOR, TYPE, SCORE, NAME, DATE;
	
	@Override
	public String toString() {
		return super.toString().toLowerCase();
	}
}
