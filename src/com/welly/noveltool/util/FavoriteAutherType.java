package com.welly.noveltool.util;

public enum FavoriteAutherType {
	ALL("����"), COLLECTED("���ղ�"), UNCOLLECTED("δ�ղ�");

	private String s;

	private FavoriteAutherType(String s) {
		this.s = s;
	}

	@Override
	public String toString() {
		return s;
	}

	public static FavoriteAutherType parse(String s) {
		FavoriteAutherType type = null;
		try {
			type = valueOf(s);
			return type;
		} catch (Exception e) {
			for (FavoriteAutherType t: FavoriteAutherType.values()){
				if (t.toString().equals(s)){
					return t;
				}
			}
			return null;
		}
	}
}
