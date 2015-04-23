package com.welly.noveltool.util;

import java.awt.Image;
import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
//import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ForkJoinPool;

import javax.swing.ImageIcon;

public class FileUtil {
	
	public static String getRootPath(){
		URL url = new FileUtil().getClass().getProtectionDomain().getCodeSource().getLocation();
		try {
			return new File(url.toURI()).getParentFile().getAbsolutePath();
		} catch (URISyntaxException e) {
			e.printStackTrace();
			return new File(System.getProperty("user.dir")).getAbsolutePath();
		}
	}
	
	public static List<BookBean> deepSearch(File parentDir){
		// forkjoin���̷߳�ʽ
		try {
			ForkJoinPool forkJoinPool = new ForkJoinPool();
			List<BookBean> retList = forkJoinPool.invoke(new FileSearcher(parentDir));
			return retList;
		} catch (Exception e) {
			return null;
		}
	}
	
	public static String readFile(String filePath){
		StringBuffer sb = new StringBuffer(1024);
		try {
	        InputStream is = new FileInputStream(filePath);
	        String line; // ��������ÿ�ж�ȡ������
	        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
	        line = reader.readLine(); // ��ȡ��һ��
	        while (line != null) { // ��� line Ϊ��˵��������
	        	sb.append(line); // ��������������ӵ� buffer ��
	        	sb.append("\r\n"); // ��ӻ��з�
	            line = reader.readLine(); // ��ȡ��һ��
	        }
	        reader.close();
	        is.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
        return sb.toString();
    }
	
	public static ImageIcon getScoreImage(int i){
		if (i > 5){
			i = 5;
		} else if (i < 0){
			i = 0;
		}
		Image image = Toolkit.getDefaultToolkit().getImage(FileUtil.class.getResource("/" + i + ".png"));
		return new ImageIcon(image.getScaledInstance(90, 18, 1));
	}
	
	public static void writeFile(File f, String content){
        try {  
            FileWriter fw = new FileWriter(f, false);  
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(content);// �����е��ļ�������ַ���  
            bw.close();  
            fw.close();  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
	}
}
