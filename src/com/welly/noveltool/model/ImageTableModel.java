package com.welly.noveltool.model;

import java.util.Vector;

import javax.swing.table.DefaultTableModel;

public class ImageTableModel extends DefaultTableModel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2699096448675328003L;

	public ImageTableModel(int rowCount, int i) {
		super(rowCount, i);
	}

	public boolean isCellEditable(int i, int j) {
		return j == 3;
	};
	
	@SuppressWarnings("rawtypes")
	public java.lang.Class<?> getColumnClass(int i) {
		Vector v = (Vector) super.dataVector.elementAt(0);
		if(v.elementAt(i) != null){
			return v.elementAt(i).getClass();
		}else{
			return String.class;
		}
	};
}
