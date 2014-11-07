package org.ymm.login;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Properties;

import org.ymm.util.LoginUserVO;

public class User {

	public static LoginUserVO getLoginUser(String property_path) {

		LoginUserVO vo = readProperties(property_path)[0];

		return vo;
	}

	public static LoginUserVO[] readProperties(String property_path) {
		LoginUserVO[] vos = null;
		Properties props = new Properties();
		try {
			InputStream in = new BufferedInputStream(new FileInputStream(
					property_path));
			props.load(in);
			vos = new LoginUserVO[props.size()];
			Enumeration<?> en = props.propertyNames();
			int i = 0;
			while (en.hasMoreElements()) {
				String key = (String) en.nextElement();
				String value = props.getProperty(key);
				vos[i] = new LoginUserVO();
				vos[i].setUsername(key);
				vos[i].setPassword(value);
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("login_user.properties is not exist!");
			e.printStackTrace();

		}
		return vos;
	}

	public static void writeProperties(String property_path, String username,
			String password) {
		Properties props = new Properties();
		try {
			InputStream in = new BufferedInputStream(new FileInputStream(
					property_path));
			props.load(in);
			OutputStream out = new FileOutputStream(property_path);
			props.setProperty(username, password);
			props.store(out, "Update " + username + " account!");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args)
	{
		writeProperties("./login_user.properties","","");
	}
}
