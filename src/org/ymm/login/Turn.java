package org.ymm.login;

public class Turn {
	
	public String turn_out(String code){
		StringBuffer sb = new StringBuffer(code);
		int pos;
		while((pos = sb.indexOf("\\u")) >-1 ){
			String tmp = sb.substring(pos,pos+6);
			sb.replace(pos, pos + 6, Character.toString((char)Integer.parseInt(tmp.substring(2),16)));
		}
		code = sb.toString();
		String code_all = code.replaceAll("\\\\", "");
		return code_all;
	}
	
}
