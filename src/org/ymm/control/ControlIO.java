package org.ymm.control;

import java.io.BufferedReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

public class ControlIO {

	public BufferedReader getUserIOConnect() throws JDOMException, IOException{
		BufferedReader reader = new BufferedReader(new InputStreamReader(
                new FileInputStream("./sina_ids.txt")));
        return reader;
	}
	public String getUserIO(BufferedReader reader) throws IOException{
		String line = reader.readLine();
	    return line;
	}
	
	public void setData(ArrayList<String> arrayList_text,ArrayList<String> arrayList_Date,String uid){
		ArrayList<String> arrayList_Date2 = new ArrayList<String>();
		
		for(String Date:arrayList_Date){
			Calendar cal=Calendar.getInstance();    
			Date = Date.replace("今天",cal.get(Calendar.MONTH)+"月"+cal.get(Calendar.DATE)+"日");
			Date = Date.replace(":","时");
			arrayList_Date2.add(Date);
		}
		System.out.println("time_size:"+arrayList_Date.size());
		System.out.println("content_size:"+arrayList_text.size());
		for(int i = 0;i < arrayList_text.size();i++){
			File file = new File("./data/"+uid);
			file.mkdirs();
			FileWriter idFileWriter;
	    	PrintWriter idPrintWriter = null;
	    	System.out.println("date:"+arrayList_Date2.get(i));
			File idFile = new File("./data/"+uid+"/"+arrayList_Date2.get(i)+".txt");
			try {
				idFileWriter=new FileWriter(idFile,true);
				idPrintWriter=new PrintWriter(idFileWriter,true); 
				idPrintWriter.write(String.valueOf(arrayList_text.get(i)));
				System.out.println("content:"+arrayList_text.get(i));
			} catch (IOException e) {
				// TODO: handle exception
				e.printStackTrace();
			}finally{
				idPrintWriter.flush();
				idPrintWriter.close();
			}
		}
		
		
	}
	
}
