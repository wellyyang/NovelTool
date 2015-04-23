package com.welly.noveltool;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import com.welly.noveltool.dao.SqliteHelper;
import com.welly.noveltool.model.MainFrame;

/**
 * 入口类,main方法所在类,调用主窗口并显示
 * 
 */

public class Entry {
	
	private static String version = "2.4";

	public static void main(String[] args) {		
		MainFrame mainFrame = new MainFrame("小说管理工具v" + version);
		mainFrame.setVisible(true);
		mainFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		mainFrame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent windowevent) {
				mainFrame.setVisible(false);
				SqliteHelper.cleanCache();
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				System.exit(0);
			}
		});
	}
}

