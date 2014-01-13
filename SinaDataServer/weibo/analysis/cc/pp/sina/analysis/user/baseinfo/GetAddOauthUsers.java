package cc.pp.sina.analysis.user.baseinfo;

import java.io.IOException;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.HashMap;

import org.apache.commons.httpclient.HttpException;
import org.codehaus.jackson.JsonNode;

import cc.pp.sina.jdbc.UbiaJDBC;
import cc.pp.sina.utils.HttpUtils;
import cc.pp.sina.utils.JsonUtils;

/**
 * Title: 获取增加的授权用户信息
 * @author wanggang
 * @version 1.1
 * @since 2013-05-27
 */
public class GetAddOauthUsers implements Serializable {

	/**
	 * 默认序列化版本号
	 */
	private static final long serialVersionUID = 1L;
	// t2.pp.cc
	private static final String BASEURL = new String("http://t2.pp.cc/api/bind/index?type=");
	private static final String TOKEN = new String("b14d48ede1f7c2f7779585ef3f9102f0");

	/**
	 * 测试函数
	 * @param args
	 * @throws IOException 
	 * @throws HttpException 
	 * @throws SQLException 
	 */
	public static void main(String[] args) throws HttpException, IOException, SQLException {

		GetAddOauthUsers gaau = new GetAddOauthUsers();
//		HashMap<Integer,String> addusers = gaau.getAddUsersByDay("sina", "0");
//		System.out.println(addusers.size());
		gaau.insertAddUsersByDay("sina", "0", "192.168.1.27");
		System.out.println("OK");
	}

	/**
	 * 
	 * @param type
	 * @param bid
	 * @return
	 * @throws HttpException
	 * @throws IOException
	 */
	public HashMap<Integer,String> getAddUsersByDay(String type, String bid) throws HttpException, IOException {

		HashMap<Integer,String> addusers = new HashMap<Integer,String>();
		String url = this.getUrl(type, bid);
		HttpUtils httpUtils = new HttpUtils();
		String data = httpUtils.getAddUsers(url);
		JsonNode jsondata = JsonUtils.getJsonNode(data, "list");   // 字段名改下
		String info = new String();
		int k = 0;
		for (int i = 0; i < jsondata.size(); i++) {
			info = jsondata.get(i).get("bind_username").toString().replaceAll("\"", "");
			addusers.put(k++, info);
		}

		return addusers;
	}

	/**
	 * 插入每天新增数据到数据库中
	 * @param type
	 * @param bid
	 * @param ip
	 * @throws HttpException
	 * @throws IOException
	 * @throws SQLException
	 */
	public void insertAddUsersByDay(String type, String bid, String ip) throws HttpException, IOException, SQLException {

		String url = this.getUrl(type, bid);
		HttpUtils httpUtils = new HttpUtils();
		String data = httpUtils.getAddUsers(url);
		JsonNode jsondata = JsonUtils.getJsonNode(data, "list");   // 字段名改下
		String bind = new String();
		String username = new String();
		String accesstoken = new String();
		String expires = new String();
		UbiaJDBC ubiajdbc = new UbiaJDBC(ip);
		if (ubiajdbc.mysqlStatus()) {
			for (int i = 0; i < jsondata.size(); i++) {
				bind = jsondata.get(i).get("bid").toString().replaceAll("\"", "");
				username = jsondata.get(i).get("bind_username").toString().replaceAll("\"", "");
				accesstoken = jsondata.get(i).get("bind_access_token").toString().replaceAll("\"", "");
				expires = jsondata.get(i).get("expires").toString().replaceAll("\"", "");
				ubiajdbc.insertT2Users(bind, username, accesstoken, expires);
			}
			ubiajdbc.sqlClose();
		}
	}

	/**
	 * 获取url
	 * @param type
	 * @return
	 */
	public String getUrl(String type, String bid) {
		String url = BASEURL + type + "&bid=" + bid + "&token=" + TOKEN;
		return url;
	}

}
