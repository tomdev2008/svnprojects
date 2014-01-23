package cc.pp.sina.utils;

import java.io.IOException;
import java.sql.SQLException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonNode;

import cc.pp.sina.common.Encryption;
import cc.pp.sina.jdbc.WeiboJDBC;

public class GetWeiboLists {

	private static Logger logger = Logger.getLogger(HttpUtils.class);
	private String ip = "";
	// http://www.pp.cc/api/analysis/getweibolist?token=b14d48ede1f7c2f7779585ef3f9102f0
	private static final String BASEDURL = "http://www.pp.cc/api/analysis/"; // http://user.svc.pp.cc/analysis/
	private static final String WBLISTS = "getweibolist";
	private static final String UPDATEINFO = "updateinfo";
	private static final String KEY = "mnvrjierrdqdiefxanjp";
	private static final String TOKEN = "b14d48ede1f7c2f7779585ef3f9102f0";

	/**
	 * 构造函数
	 * @param ip
	 */
	public GetWeiboLists(String ip) {
		this.ip = ip;
	}

	/**
	 * @param args
	 * @throws SQLException 
	 */
	public static void main(String[] args) throws SQLException {

		GetWeiboLists getwb = new GetWeiboLists("192.168.1.172");
//		getwb.getWbLists();
		System.out.println(getwb.getAddWeibos(getwb.getUpdateUrl("4961606", "sina",
				"http://weibo.com/3493242132/zF2gW0vHC")));
	}

	/**
	 * 获取微博列表，并存储
	 * @return
	 * @throws SQLException 
	 */
	public void getWbLists() throws SQLException {

		String data = this.getAddWeibos(this.getWblistUrl());
		JsonNode jsondata = JsonUtil.getJsonNode(data, "data");
		String type, uid, url;
		WeiboJDBC weibojdbc = new WeiboJDBC(this.ip);
		if (weibojdbc.mysqlStatus()) {
			for (int i = 0; i < jsondata.size(); i++) {
				type = jsondata.get(i).get("type").toString().replaceAll("\"", "");
				uid = jsondata.get(i).get("uid").toString().replaceAll("\"", "");
				url = jsondata.get(i).get("url").toString().replaceAll("\"", "");
				if ("sina".equals(type)) {
					weibojdbc.insertWbLists("sinaweibolists", uid, url);
				} else if ("tencent".equals(type)) {
					weibojdbc.insertWbLists("tencentweibolists", uid, url);
				}
			}
			weibojdbc.sqlClose();
		}
	}

	/**
	 * 从T2获取微博数据
	 * @param url
	 * @return
	 */
	public String getAddWeibos(String url) {

		String html = new String();
		HttpClient httpclient = new HttpClient();
		GetMethod method = new GetMethod(url);
		try {
			method.getParams().setParameter("http.protocol.cookie-policy", CookiePolicy.BROWSER_COMPATIBILITY);
			httpclient.executeMethod(method);
			int status = method.getStatusCode();
			if (status == HttpStatus.SC_OK) {
				html = method.getResponseBodyAsString();
			} else {
				html = null;
			}
		} catch (HttpException e) {
			logger.info("error message http: " + e);
		} catch (IOException e) {
			e.printStackTrace();
			logger.info("error message io: " + e);
		} finally {
			method.releaseConnection();
		}

		return html;
	}

	/**
	 * 获取微博列表URL
	 * @return
	 */
	public String getWblistUrl() {

		return BASEDURL + WBLISTS + "?token=" + TOKEN;
	}

	/**
	 * 获取微博更新URL
	 * @param uid
	 * @param type
	 * @param wburl
	 * @return
	 */
	public String getUpdateUrl(String uid, String type, String wburl) {
		
		String url = BASEDURL + UPDATEINFO + "?";
		// MD5加密
		String token = Encryption.getSignature(uid, type, wburl, KEY);
		url = url + "uid=" + uid + "&type=" + type + "&url=" + wburl + "&token=" + token;
		return url;
	}
}
