package com.welly.noveltool;

import javax.swing.JFrame;
import com.welly.noveltool.model.MainFrame;

/**
 * �����,main����������,���������ڲ���ʾ
 * 
 */

public class Entry {
	
	private static String version = "2.4";

	public static void main(String[] args) {		
		MainFrame mainFrame = new MainFrame("С˵������v" + version);
		mainFrame.setVisible(true);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		mainFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		/*mainFrame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent windowevent) {
				int ret = JOptionPane.showConfirmDialog(mainFrame, "�Ƿ���Ҫ��̨�������ݿ��ļ�?", "ȷ�ϴ���"
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

