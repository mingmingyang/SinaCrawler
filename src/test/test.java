package test;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.NameValuePair;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import org.ymm.util.PreLoginInfo;

public class test {

	static String SINA_PK = "EB2A38568661887FA180BDDB5CABD5F21C7BFD59C090CB2D24"
			+ "5A87AC253062882729293E5506350508E7F9AA3BB77F4333231490F915F6D63C55FE2F08A49B353F444AD39"
			+ "93CACC02DB784ABBB8E42A9B1BBFFFB38BE18D78E87A0E41B9B8F73A928EE0CCEE"
			+ "1F6739884B9777E4FE9E88A1BBE495927AC4A799B3181D6442443";
	static String username = "westlife_ymm";
	static String passwd = "..005678";

	private static final Log logger = LogFactory.getLog(test.class);

	public static String rsaCrypt(String modeHex, String exponentHex, String messageg)
			throws IllegalBlockSizeException, BadPaddingException,
			NoSuchAlgorithmException, InvalidKeySpecException,
			NoSuchPaddingException, InvalidKeyException,
			UnsupportedEncodingException {
		KeyFactory factory = KeyFactory.getInstance("RSA");

		BigInteger m = new BigInteger(modeHex, 16); /* public exponent */
		BigInteger e = new BigInteger(exponentHex, 16); /* modulus */
		RSAPublicKeySpec spec = new RSAPublicKeySpec(m, e);

		RSAPublicKey pub = (RSAPublicKey) factory.generatePublic(spec);
		Cipher enc = Cipher.getInstance("RSA");
		enc.init(Cipher.ENCRYPT_MODE, pub);

		byte[] encryptedContentKey = enc.doFinal(messageg.getBytes("GB2312"));

		return new String(Hex.encodeHex(encryptedContentKey));
	}

	public static void main(String[] args) throws HttpException,  InvalidKeyException, IllegalBlockSizeException,
			BadPaddingException, NoSuchAlgorithmException,
			InvalidKeySpecException, NoSuchPaddingException, JSONException, IOException {
		DefaultHttpClient client = new DefaultHttpClient();
		client.getParams().setParameter("http.protocol.cookie-policy",
				CookiePolicy.BROWSER_COMPATIBILITY);
		client.getParams().setParameter(
				HttpConnectionParams.CONNECTION_TIMEOUT, 5000);

		HttpPost post = new HttpPost(
				"http://login.sina.com.cn/sso/login.php?client=ssologin.js(v1.4.2)");

		PreLoginInfo info = getPreLoginBean(client);

		long servertime = info.servertime;
		String nonce = info.nonce;

		String pwdString = servertime + "\t" + nonce + "\n" + passwd;
		//pwdString=passwd;
		String sp = rsaCrypt(SINA_PK, "10001", pwdString);

		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("entry", "weibo"));
		nvps.add(new BasicNameValuePair("gateway", "1"));
		nvps.add(new BasicNameValuePair("from", ""));
		nvps.add(new BasicNameValuePair("savestate", "7"));
		nvps.add(new BasicNameValuePair("useticket", "1"));
		nvps.add(new BasicNameValuePair("ssosimplelogin", "1"));
		nvps.add(new BasicNameValuePair("vsnf", "1"));
		// new NameValuePair("vsnval", ""),
		nvps.add(new BasicNameValuePair("su", encodeUserName(username)));
		nvps.add(new BasicNameValuePair("service", "miniblog"));
		nvps.add(new BasicNameValuePair("servertime", servertime + ""));
		nvps.add(new BasicNameValuePair("nonce", nonce));
		nvps.add(new BasicNameValuePair("pwencode", "rsa2"));
		nvps.add(new BasicNameValuePair("rsakv", info.rsakv));
		nvps.add(new BasicNameValuePair("sp", sp));
		nvps.add(new BasicNameValuePair("encoding", "UTF-8"));
		nvps.add(new BasicNameValuePair("prelt", "115"));
		nvps.add(new BasicNameValuePair("returntype", "META"));
		nvps.add(new BasicNameValuePair(
				"url",
				"http://weibo.com/ajaxlogin.php?framelogin=1&callback=parent.sinaSSOController.feedBackUrlCallBack"));

		post.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));

		HttpResponse response = client.execute(post);
		String entity = EntityUtils.toString(response.getEntity());

entity=new String(entity.getBytes("ISO-8859-1"),"GBK"); 
//System.out.println(entity);
		String url = entity.substring(
				entity.lastIndexOf("http://weibo.com/ajaxlogin.php?"),
				entity.lastIndexOf("\""));
		//System.out.println("url:"+url.charAt(url.length()-2));
		System.out.println("url:"+url);
		logger.debug("url:" + url);
		
		// 获取到实际url进行连接
		HttpGet getMethod = new HttpGet(url);

		response = client.execute(getMethod);
		
		entity = EntityUtils.toString(response.getEntity());
		entity = entity.substring(entity.indexOf("userdomain") + 13,
				entity.lastIndexOf("\""));
		logger.debug(entity);

		getMethod = new HttpGet("http://weibo.com/humingchun?wvr=5&lf=reg");
		response = client.execute(getMethod);
		entity = EntityUtils.toString(response.getEntity());
		entity = new String(entity.getBytes("ISO-8859-1"),"UTF-8"); 
		System.out.println("su:"+encodeUserName(username));
		System.out.println("sp:"+sp);
		System.out.println(entity);
		// Document doc =
		// Jsoup.parse(EntityUtils.toString(response.getEntity()));
		//System.out.println(responseString);
		logger.debug(entity);
	}

	private static PreLoginInfo getPreLoginBean(HttpClient client)
			throws HttpException, IOException, JSONException {

		String serverTime = getPreLoginInfo(client);
		//System.out.println("");
		JSONObject jsonInfo = JSONObject.fromObject(serverTime);
		PreLoginInfo info = new PreLoginInfo();
		info.nonce = jsonInfo.getString("nonce");
		info.pcid = jsonInfo.getString("pcid");
		info.pubkey = jsonInfo.getString("pubkey");
		info.retcode = jsonInfo.getInt("retcode");
		info.rsakv = jsonInfo.getString("rsakv");
		info.servertime = jsonInfo.getLong("servertime");
		return info;
	}

	public static String getPreLoginInfo(HttpClient client)
			throws ParseException, IOException {
		String preloginurl = "http://login.sina.com.cn/sso/prelogin.php?entry=sso&"
				+ "callback=sinaSSOController.preloginCallBack&su="
				+ "dW5kZWZpbmVk"
				+ "&rsakt=mod&client=ssologin.js(v1.4.2)"
				+ "&_=" + getCurrentTime();
		HttpGet get = new HttpGet(preloginurl);

		HttpResponse response = client.execute(get);

		String getResp = EntityUtils.toString(response.getEntity());

		int firstLeftBracket = getResp.indexOf("(");
		int lastRightBracket = getResp.lastIndexOf(")");

		String jsonBody = getResp.substring(firstLeftBracket + 1,
				lastRightBracket);
		System.out.println(jsonBody);
		return jsonBody;

	}

	private static String getCurrentTime() {
		long servertime = new Date().getTime() / 1000;
		return String.valueOf(servertime);
	}

	private static String encodeUserName(String email) {
		email = email.replaceFirst("@", "%40");// MzM3MjQwNTUyJTQwcXEuY29t
		email = Base64.encodeBase64String(email.getBytes());
		return email;
	}

}