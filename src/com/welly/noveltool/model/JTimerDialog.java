package com.welly.noveltool.model;

import java.awt.Container;
import java.awt.Frame;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.swing.JDialog;
import javax.swing.JLabel;

public class JTimerDialog extends JDialog {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8973575173996410419L;

	private String msg;
	
	private Integer timeout;
	
	private Runnable run;
	
	private Frame frame;

	private ExecutorService service;
	
	private boolean exceptionFlag = false;
	
	public JTimerDialog(Frame frame, String title, String msg, Integer timeout, Runnable run) {
		super(frame, title);
		this.frame = frame;
		this.msg = msg;
		this.timeout = timeout;
		this.run = run;
	}
	
	@Override
	public void setVisible(boolean flag) {
		if (!flag){
			super.setVisible(flag);
			return;
		}
		Container container = this.getContentPane();
		container.add(new JLabel(msg));
		this.setSize(300, 100);
		this.setResizable(false);
		this.setLocation((frame.getWidth() - this.getWidth()) / 2,
				(frame.getHeight() - this.getHeight()) / 2);
		if (run != null){
			this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
			service = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>()){
				@Override
				protected void afterExecute(Runnable runnable,
						Throwable throwable) {
					super.afterExecute(runnable, throwable);
					if (throwable != null){
						exceptionFlag = true;
					}
				}
			};
			service.execute(run);
			service.shutdown();
			if (timeout != null && timeout > 0){
		        final ScheduledExecutorService s = Executors.newSingleThreadScheduledExecutor();
		        s.scheduleAtFixedRate(new Runnable() {  
		        	
		        	private int i = 0;
		        	
		        	private boolean complete = false;
		              
		            @Override  
		            public void run() {  
		            	i++;
						JLabel label = (JLabel) getContentPane().getComponent(0);
						if (exceptionFlag){
							label.setText("<html>" + msg + "<br>����ʧ��,ԭ��Ϊ�����쳣!</html>");
							complete = true;
						} else if(service != null && service.isTerminated()){
		                	label.setText("<html>���������!<br>����ʱ" + i + "��!</html>");
							complete = true;
		                } else if(timeout >= i) {
							label.setText("<html>" + msg + "<br>�Ѻ�ʱ" + i + "��!</html>");
		                } else {
							label.setText("<html>" + msg + "<br>����ʧ��,ԭ��Ϊ����" + timeout + "����޶�ʱ��!</html>");
							complete = true;
		                }
		                
		                if (complete){
							s.shutdownNow();
							try {
								s.awaitTermination(0, TimeUnit.SECONDS);
								if (service != null){
									service.shutdownNow();
									service.awaitTermination(0, TimeUnit.SECONDS);
								}
							} catch (InterruptedException e) {
								e.printStackTrace();
							} finally {
								setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
							}
		                }
		            }  
		        }, 1, 1, TimeUnit.SECONDS); 
			}
		}
		super.setVisible(flag);
	}
	
	@Override
	public void dispose() {
		if (service != null){
			service.shutdownNow();
			try {
				service.awaitTermination(0, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		super.dispose();
	}
}
