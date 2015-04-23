package com.welly.noveltool.util;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

public class FileSearcher extends RecursiveTask<List<BookBean>>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7844544819377957378L;
	private File dir;
	
	public FileSearcher(File dir){
		this.dir = dir;
	}
	
	@Override
	protected List<BookBean> compute() {
		List<BookBean> retList = new ArrayList<BookBean>();
		
		List<ForkJoinTask<List<BookBean>>> taskList = new ArrayList<ForkJoinTask<List<BookBean>>>();
		

		File[] files = dir.listFiles(new FileFilter() {
			
			@Override
			public boolean accept(File f) {
				// ֻ����Ŀ¼����txt�ļ�,�Ҳ�Ϊ�����ļ�
				return (f.isDirectory() || f.getName().toLowerCase().endsWith(".txt")) && !f.isHidden();
			}
		});
		
		if (files == null){
			// ĳЩʱ��listFiles����null,dir·��Ϊ������,ԭ����
			// �鿴listFiles����,��list�����л����isValid��鵱ǰ·���Ƿ�Ϸ�,������windows�Դ���ĳ���ļ���·��,����ͨ�����޷��鿴
			return new ArrayList<>();
		}
		
		for (File file : files) {
			// ������ļ�,����뷵���б�,�����Ŀ¼,����������б�
			if (file.isFile()){
				String name = file.getName();
				if (name.contains("  ")){ // ������ո��滻Ϊ�����ո�
					name = name.replaceAll("\\s{2,}", " ");
					File newFile = new File(file.getParent(), name);
					file.renameTo(newFile);
					retList.add(new BookBean(newFile));
				} else {
					retList.add(new BookBean(file));
				}
			}else{
				taskList.add(new FileSearcher(file));
			}
		}
		
		List<BookBean> tmpList;
		Collection<ForkJoinTask<List<BookBean>>> collection = invokeAll(taskList);
		if (collection != null){
			for (ForkJoinTask<List<BookBean>> task: collection) {
				if (task == null){
					continue;
				}
				tmpList = task.join();
				if (tmpList != null && tmpList.size() > 0){
					retList.addAll(tmpList);
				}
			}
		}
		
		return retList;
	}

}