package org.ymm.util;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.apache.commons.httpclient.HttpException;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.ymm.login.Login;

public class WebFilter {

	/**
	 * @param args
	 */
	public static String end_id = "";
	public static int count = 1;
	public static String pl_name = "";
    public static String page_id="";
	public static int getPageMax(String uid) throws InvalidKeyException,
			HttpException, NoSuchAlgorithmException, InvalidKeySpecException,
			NoSuchPaddingException, IllegalBlockSizeException,
			BadPaddingException, IOException {
		Logger logger = Logger.getLogger(WebFilter.class);
		System.setProperty("org.apache.commons.logging.Log",
				"org.apache.commons.logging.impl.NoOpLog");
		PropertyConfigurator.configure("myLog.properties");
		int max = 1;
		String mainUrl = "http://weibo.com/u/" + uid;
		// mainUrl="http://weibo.com/renzhiqiang";
		String html = Login.getContent(mainUrl);
		// System.out.println(html);

		Document doc = getMainDoc(html);
		page_id=getPageId(html);
		//System.out.println(page_id);
		// System.out.println("doc:"+doc);
		Elements elements = doc.select("div[action-type=feed_list_item]");
		if (elements.isEmpty()) {
			return -1;
		}
		MidVO mainVO = getMainMidVO(doc);
		if (!mainVO.isLazyFlag()) {
			System.out.println("lazyFlag!!!");
			return max;
		}
		String max_id = mainVO.getEnd();
		end_id = mainVO.getStart();
		String pagebar = "";
		int i = 0;
		boolean flag = true;
		while (flag)
		// count++;
		{
			pagebar = "" + i;
			/*String url = "http://weibo.com/aj/mblog/mbloglist?_wv=5&page=1&count=15&max_id="
					+ max_id
					+ "&pre_page=1&end_id="
					+ end_id
					+ "&pagebar="
					+ pagebar + "&_k=0&_t=0&uid=" + uid + "&__rnd=0";
			*/
			 String url=updateUrl(uid,"1",pagebar);
			// url="http://weibo.com/p/aj/v6/mblog/mbloglist?domain=100505&ajwvr=6&pre_page=1&page=1&max_id=&end_id="+end_id+"&count=15&pagebar="+pagebar+"&max_msign=&filtered_min_id=&id=100505"+"uid"+"&script_uri=/p/100505"+uid+"/home&feed_type=0&from=page_100505&mod=TAB&domain_op=100505&__rnd=1415126891107";
			System.out.println("maxpageurl:" + url);
			html = Login.getContent(url);
			doc = getLazyDoc(html);
			
			MidVO lazyMidVO = getLazyMid(doc);
			flag = lazyMidVO.isLazyFlag();
			//System.out.println("flag:" + flag);
			// System.out.println("doc:"+doc);
			max_id = lazyMidVO.getEnd();
			i++;
			if (flag == false) {
				String tempPage = getDivPage(doc);
				if (tempPage == null) {
					return max;
				} else {
					max = Integer.parseInt(tempPage);
				}
			}
		}

		return max;
	}

	public static String getDivPage(Document doc) {
		String page = null;
        //System.out.println("doc:\n"+doc);
		Elements content = doc.select("div[node-type=feed_list_page]");
		if (content.isEmpty()) {
			return page;
		} else {
			Elements elePage = content
					.select("a[action-type=feed_list_page_more]");
			String maxPage = elePage.last().attr("action-data");
			page = maxPage.substring(maxPage.lastIndexOf("=") + 1);
		}
		return page;
	}
    public static String getPageId(String html)
    {
    	Document doc = Jsoup.parse(html);
		Elements elements = doc.select("script[type=text/javascript]");
		//System.out.println(elements.last());
		String content=elements.last().toString();
		//System.out.println(content);
		String temp=content.substring(content.indexOf("[\'page_id\']=\'")+13);
		return temp.substring(0,temp.indexOf("\'"));
    }
	public static Document getMainDoc(String html) {
		Document doc = Jsoup.parse(html);
		Elements elements1 = doc.select("script");
		String s = null;
		for (int i = 0; i < elements1.size(); i++) {
			s = elements1.get(i).outerHtml();
			// System.out.println("i:"+i);
			if (s.contains("\"domid\":\"Pl_Official_MyProfileFeed__")) {
				break;
			}
		}
		System.out.println("s:" + s);
        String temp=s.substring(s.indexOf("Pl_Official_MyProfileFeed__"));
        pl_name=temp.substring(0,temp.indexOf("\""));
        System.out.println("pl_name:" + pl_name);
		String json = s.substring(s.indexOf("(") + 1, s.lastIndexOf(")"));
		// System.out.println(json);
		String h = JsonIO.getValue(json, "html");
		Document doc1 = Jsoup.parse(h);
		return doc1;
	}

	public static MidVO getMainMidVO(Document doc) {
		MidVO mid = new MidVO();

		Logger logger = Logger.getLogger(WebFilter.class);
		System.setProperty("org.apache.commons.logging.Log",
				"org.apache.commons.logging.impl.NoOpLog");
		PropertyConfigurator.configure("myLog.properties");
		// logger.info(html);

		// System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n"+doc1);
		// logger.info(doc1);
		// getText(doc1);
		// getTime(doc1);
		mid.setStart(getStartMid(doc));
		mid.setEnd(getEndMid(doc));
		mid.setLazyFlag(getLazyload(doc));
		// System.out.println("s:"+mid.getStart()+"\ne:"+mid.getEnd()+"\nlazy:"+mid.isLazyFlag()+"\n");
		return mid;
	}

	public static boolean getLazyload(Document doc) {
		boolean flag = false;
		Elements elements = doc.select("div[node-type=lazyload]");
		if (!elements.isEmpty()) {
			flag = true;
		}
		return flag;
	}

	public static String getStartMid(Document doc) {
		Elements elements = doc.select("div[action-type=feed_list_item]");
		// System.out.println("null:"+elements.size());
		String mid = elements.get(0).attr("mid");
		System.out.println("start_mid:" + mid);
		return mid;
	}

	public static String getEndMid(Document doc) {
		Elements elements = doc.select("div[action-type=feed_list_item]");
		if (elements.isEmpty()) {
			return null;
		}
		String mid = elements.get(elements.size() - 1).attr("mid");
		System.out.println("end_mid:" + mid);
		return mid;
	}

	public static ArrayList<String> getText(Document doc) {

		Logger logger = Logger.getLogger(WebFilter.class);
		System.setProperty("org.apache.commons.logging.Log",
				"org.apache.commons.logging.impl.NoOpLog");
		PropertyConfigurator.configure("myLog.properties");

		ArrayList<String> arrayList = new ArrayList<String>();

		Elements elements = doc.select("div[node-type=feed_list_content]");

		if (elements.isEmpty()) {
			// System.out.println("elements is empty!");
			return arrayList;
		}
		for (Element element : elements) {
			if (element.hasAttr("act_id")) {
				continue;
			}
			// Elements temps=element.select("div[class=WB_text W_f14]");
			Element temps = element;
			logger.info(temps.text());
			// System.out.println("temps:"+temps.text());

			arrayList.add(temps.text());

			// /arrayList.add(element.text());
		}
		// arrayList.add(String.valueOf(i));
		return arrayList;
	}

	public static ArrayList<String> getTime(Document doc) {
		Logger logger = Logger.getLogger(WebFilter.class);
		System.setProperty("org.apache.commons.logging.Log",
				"org.apache.commons.logging.impl.NoOpLog");
		PropertyConfigurator.configure("myLog.properties");
		ArrayList<String> arrayList_time = new ArrayList<String>();
		Elements entities = doc.select("div[action-type=feed_list_item]");
		if (entities.isEmpty()) {
			return arrayList_time;
		}
		//System.out.println("entities:" + entities.size());
		for (Element entity : entities) {
			Elements test = entity.select("a[node-type=feed_list_item_date]");
			//System.out.println("test:" + test.size());
			if (test.isEmpty()) {
				//System.out.println("000:" + entity);
				// return arrayList_time;
				continue;
			}
			Element temp = test.get(test.size() - 1);
			// String sstime="1339033320000";
			String sstime = temp.attr("date");
			//System.out.println("sstime:" + sstime);
			Date date = new Date(Long.parseLong(sstime));

			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
			sstime = sdf.format(date);
			// String time = temp.attr("title");
			// time = time.replace("-", "").replace(" ", "").replace(":", "");
			logger.info("time:" + sstime);

			// System.out.println("time:"+time);
			arrayList_time.add(sstime);
		}

		return arrayList_time;
	}

	public static Document getLazyDoc(String html) {
		String h = JsonIO.getValue(html, "data");
		Document doc = Jsoup.parse(h);
		return doc;
	}
	
	/*public static Document getJsonHtml(String html) {
		String h = JsonIO.getValue(html, "html");
		Document doc = Jsoup.parse(h);
		return doc;
	}*/
	
	public static MidVO getLazyMid(Document doc) {
		MidVO mid = new MidVO();
		Logger logger = Logger.getLogger(WebFilter.class);
		System.setProperty("org.apache.commons.logging.Log",
				"org.apache.commons.logging.impl.NoOpLog");
		PropertyConfigurator.configure("myLog.properties");

		// logger.info(doc);
		mid.setEnd(getEndMid(doc));
		mid.setLazyFlag(getLazyload(doc));
		return mid;
		// getText(doc);
		// getTime(doc);
	}

	public static PageInfoVO getPerPage(String uid, String page)
			throws InvalidKeyException, HttpException,
			NoSuchAlgorithmException, InvalidKeySpecException,
			NoSuchPaddingException, IllegalBlockSizeException,
			BadPaddingException, IOException {

		PageInfoVO info = new PageInfoVO();
		// ArrayList<String> list=new ArrayList<String>();
		ArrayList<String> list_text = new ArrayList<String>();
		ArrayList<String> list_time = new ArrayList<String>();
		String mainUrl = "http://weibo.com/u/" + uid + "?page=" + page;
		System.out.println("mainurl:" + mainUrl);
		String html = Login.getContent(mainUrl);
		Document doc = getMainDoc(html);
		// System.out.println("doc:"+doc);
		Elements elements = doc.select("div[node-type=feed_list_content]");
		if (elements.isEmpty()) {
			info.setListText((list_text));
			info.setListTime(list_time);
			return info;
		}
		// if()
		list_text.addAll(getText(doc));
		list_time.addAll(getTime(doc));
		System.out.println("number_text:" + list_text.size());
		System.out.println("number_time:" + list_time.size());
		MidVO mainVO = getMainMidVO(doc);

		if (!mainVO.isLazyFlag()) {
			info.setListText((list_text));
			info.setListTime(list_time);
			return info;
			// return list;
		}
		/*
		 * if("1".equals(page)) {
		 */
		// getPageMax(uid);
		// }
		String max_id = mainVO.getEnd();
		System.out.println("max_id:" + max_id);
		String pagebar = "";
		int i = 0;
		boolean flag = false;
		do {
			// count++;
			pagebar = "" + i;
			/*String url = "http://weibo.com/aj/mblog/mbloglist?_wv=5&page="
					+ page + "&count=15&max_id=" + max_id + "&pre_page=" + page
					+ "&end_id=" + end_id + "&pagebar=" + pagebar
					+ "&_k=0&uid=" + uid + "&_t=0&__rnd=0";*/
			String url=updateUrl(uid,page,pagebar);
			System.out.println("url:" + url);
			html = Login.getContent(url);
			doc = getLazyDoc(html);
			// System.out.println("lazydoc:"+doc);
			Elements es = doc.select("div[node-type=feed_list_content]");
			if (es.isEmpty()) {
				break;
			}
			list_text.addAll(getText(doc));
			list_time.addAll(getTime(doc));
			System.out.println("gettime:" + getTime(doc).size());
			MidVO lazyMidVO = getLazyMid(doc);
			flag = lazyMidVO.isLazyFlag();
			max_id = lazyMidVO.getEnd();
			i++;
		} while (flag);

		info.setListText((list_text));
		info.setListTime(list_time);
		return info;

	}

	public static String updateUrl(String uid, String page,
			String pagebar) {
		String url = "http://weibo.com/p/aj/v6/mblog/mbloglist?domain=100505&ajwvr=6&pre_page="+page+"&page="
				+ page
				+ "&max_id=&end_id="
				+ end_id
				+ "&count=15&pagebar="
				+ pagebar
				+ "&max_msign=&filtered_min_id=&pl_name="
				+ pl_name
				+ "&id="
				+ page_id
				+ "&script_uri=/"
				+ uid
				+ "/profile&feed_type=0&topnav=1&wvr=5&domain_op=100505&__rnd=1415168196680";
		return url;

	}

	public static void main(String[] args) throws InvalidKeyException,
			HttpException, NoSuchAlgorithmException, InvalidKeySpecException,
			NoSuchPaddingException, IllegalBlockSizeException,
			BadPaddingException, IOException {
		// TODO Auto-generated method stub
		String max_id = "3511233459977784";
		String end_id = "3533367196973423";
		String pagebar = "1";
		String page = "6";
		String uid = "2376025967";
		String url = "http://weibo.com/aj/mblog/mbloglist?_wv=5&page=" + page
				+ "&count=15&max_id=" + max_id + "&pre_page=1&end_id=" + end_id
				+ "&pagebar=" + pagebar + "&_k=0&_t=0&uid=" + uid + "&__rnd=0";

		// uid="1938449230";//34
		// uid="1642637860";//18
		// uid="1880605954";//8
		// uid="1717871843"; //京东
		uid = "2359149064";
		String mainUrl = "http://weibo.com/u/" + uid;
		String temp = "http://weibo.com/aj/mblog/mbloglist?_wv=5&count=15&pagebar="
				+ pagebar + "&_k=0&_t=0&uid=" + uid + "&__rnd=0";
		// System.out.println(temp);
		// String html = login.getContent(temp, client);
		// getPageMax(uid);
		// System.out.println(i);
		PageInfoVO info = getPerPage(uid, page);
		ArrayList<String> text = info.getListText();
		ArrayList<String> time = info.getListTime();
		for (int i = 0; i < text.size(); i++) {
			System.out.println("text:" + text.get(i) + "\ttime:" + time.get(i));
		}
		// getPageMax(html);
		// MidVO mid=getMainPage(html);
		// System.out.println(mid.isLazyFlag());
		// getLazyContent(html);
	}

}
