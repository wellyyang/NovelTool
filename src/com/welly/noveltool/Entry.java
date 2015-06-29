package com.welly.noveltool;

import javax.swing.JFrame;
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
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		mainFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		/*mainFrame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent windowevent) {
				int ret = JOptionPane.showConfirmDialog(mainFrame, "是否需要后台整理数据库文件?", "确认窗口"
						, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
				if (ret == JOptionPane.YES_OPTION){
					mainFrame.setVisible(false);
					SqliteHelper.cleanCache();
				}
				System.exit(0);
			}
		});*/
	}
}

