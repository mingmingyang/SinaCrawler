package org.ymm.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;





import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.util.JSONUtils;

public class JsonIO {

	/**
	 * @param args
	 */
/*	public static List<Comment> getComments(String html) {
		List<Comment> list = new ArrayList<Comment>();
		JSONObject jo = JSONObject.fromObject(html);
		// String text="["+html+"]";
		// System.out.println(text);
		JSONArray ja = JSONArray.fromObject(JSONObject.fromObject(jo.get("c"))
				.get("l"));
		String[] DATE_FORMAT = { "yyyy-MM-dd HH:mm:ss" };

		MorpherRegistry morpherRegistry = JSONUtils.getMorpherRegistry();

		morpherRegistry.registerMorpher(new DateMorpher(DATE_FORMAT));

		for (int i = 0; i < ja.size(); i++) {
			JSONObject jsonObject1 = JSONObject.fromObject(ja.get(i));

			Comment com = (Comment) JSONObject.toBean(jsonObject1,
					Comment.class);

			list.add(com);

		//	System.out.println(com.getPm_word() + ":" + com.getCreate_time());
		}
		return list;
	}*/
	public static String getValue(String json,String key)
	{
		String html=null;
		JSONObject jo = JSONObject.fromObject(json);
		//System.out.println(jo);
		//System.out.println("size:"+jo.size());
		//Iterator iter=jo.keys();
		/*while(iter.hasNext())
		{
			System.out.println("keys:"+iter.next());
		}*/
		html=jo.get(key).toString();
		return html;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
