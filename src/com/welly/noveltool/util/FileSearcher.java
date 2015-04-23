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
				// 只接受目录或者txt文件,且不为隐藏文件
				return (f.isDirectory() || f.getName().toLowerCase().endsWith(".txt")) && !f.isHidden();
			}
		});
		
		if (files == null){
			// 某些时候listFiles返回null,dir路径为不存在,原因不明
			// 查看listFiles方法,在list方法中会调用isValid检查当前路径是否合法,可能是windows自带的某种文件夹路径,但普通方法无法查看
			return new ArrayList<>();
		}
		
		for (File file : files) {
			// 如果是文件,则加入返回列表,如果是目录,则加入任务列表
			if (file.isFile()){
				String name = file.getName();
				if (name.contains("  ")){ // 将多个空格替换为单个空格
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