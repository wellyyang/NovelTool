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
		// forkjoin多线程方式
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
	        String line; // 用来保存每行读取的内容
	        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
	        line = reader.readLine(); // 读取第一行
	        while (line != null) { // 如果 line 为空说明读完了
	        	sb.append(line); // 将读到的内容添加到 buffer 中
	        	sb.append("\r\n"); // 添加换行符
	            line = reader.readLine(); // 读取下一行
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
            bw.write(content);// 往已有的文件上添加字符串  
            bw.close();  
            fw.close();  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
	}
}
