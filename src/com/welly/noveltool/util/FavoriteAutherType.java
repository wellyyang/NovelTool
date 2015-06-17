package com.welly.noveltool.util;

public enum FavoriteAutherType {
	ALL("所有"), COLLECTED("已收藏"), UNCOLLECTED("未收藏");

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
