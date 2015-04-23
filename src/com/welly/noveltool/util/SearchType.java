package com.welly.noveltool.util;

/**
 * 检索类型,目前有作者和类型两种
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
