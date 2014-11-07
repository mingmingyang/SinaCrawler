package org.ymm.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;




import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.jdom.JDOMException;
import org.ymm.control.ControlIO;
import org.ymm.util.PageInfoVO;
import org.ymm.util.WebFilter;


public class SinaMain {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Thread thread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Logger logger = Logger.getLogger(SinaMain.class);
			    System.setProperty( "org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog" );
		    	PropertyConfigurator.configure("myLog.properties");
				
				ControlIO controlIO = new ControlIO();
				BufferedReader reader = null;
				String uid = null;
				
				
				DefaultHttpClient client = null;
				ArrayList<String> arrayList_Text = null;
				ArrayList<String> arrayList_Date = null;
				try {
					reader = controlIO.getUserIOConnect();
					uid = controlIO.getUserIO(reader);//
				} catch (JDOMException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				int n = 1;
				System.out.println("uid:"+uid);
				while(uid != null){
					logger.info("正在执行第"+n+"次");
					
					logger.info("正在爬取:"+uid);
					//String url = "http://weibo.com/u/"+uid;
					
					//String html = Login.getContent(url);
					//logger.info("html:"+html);
					try {
					int maxPage=WebFilter.getPageMax(uid);
					System.out.println("maxPage:"+maxPage);
					for(int i=1;i<=maxPage;i++)
					{
						PageInfoVO info=WebFilter.getPerPage(uid, ""+i);
						controlIO.setData(info.getListText(),info.getListTime(), uid);//需要修改
					}
					
						//arrayList_Text = textfilter.getText(html);
						//arrayList_Date = textfilter.getTime(html,Integer.parseInt(arrayList_Text.get(arrayList_Text.size())));
						//需要添加时间信息
					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
						try {
							uid = controlIO.getUserIO(reader);
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						n++;
						continue;
					}
					
					
					//controlIO.setData(arrayList_Text,arrayList_Date, uid);//需要修改
					
					try {
						uid = controlIO.getUserIO(reader);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					n++;
				}
			}
		});
		thread.start();
	}

}
