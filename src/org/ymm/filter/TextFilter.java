package org.ymm.filter;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class TextFilter {

	public ArrayList<String> getText(String html){
		Document doc1 = Jsoup.parse(html); 
		Elements elements1 = doc1.select("script");  
	    String s = elements1.get(13).outerHtml();
	    String regex = "html\":\"(.*?)\"}";  //��Ϊ���ʽ
	    String html1 = null;
	    Pattern p = Pattern.compile(regex,Pattern.DOTALL);  //
	    Matcher matcher = p.matcher(s);
	    while (matcher.find()) {
	    	html1 = matcher.group(1).toString();
	    }
	    
	    
        ArrayList<String> arrayList = new ArrayList<String>();
        Document doc3 = Jsoup.parse(html1);
        Elements elements = doc3.getElementsByClass("WB_text");
        int i = 0;
        for(Element element:elements){
        	if(element.text().contains("转发微博")){
        		continue;
        	}
        	i++;
        	arrayList.add(element.text());
        }
        arrayList.add(String.valueOf(i));
        return arrayList;
	}
	
	public ArrayList<String> getTime(String html ,int i){
		Document doc1 = Jsoup.parse(html); 
		Elements elements1 = doc1.select("script");  
	    String s = elements1.get(13).outerHtml();
	    String regex = "html\":\"(.*?)\"}";  //��Ϊ���ʽ
	    String html1 = null;
	    Pattern p = Pattern.compile(regex,Pattern.DOTALL);  //
	    Matcher matcher = p.matcher(s);
	    while (matcher.find()) {
	    	html1 = matcher.group(1).toString();
	    }
	    
	    Document doc2 = Jsoup.parse(html1);
        Elements test = doc2.select("a[class=S_link2 WB_time]");
        ArrayList<String> arrayList_text = new ArrayList<String>();
        for(int j = 0;j < i ;j++){
        	Element element = test.get(j);
        	String time = element.attr("title");
        	time = time.replace("-", "").replace(" ", "").replace(":", "");
        	arrayList_text.add(time);
        }
        return arrayList_text;
	}
	
}
