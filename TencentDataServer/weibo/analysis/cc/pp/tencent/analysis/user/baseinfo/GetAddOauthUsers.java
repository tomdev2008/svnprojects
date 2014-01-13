package cc.pp.tencent.analysis.user.baseinfo;

import java.io.IOException;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.HashMap;

import org.apache.commons.httpclient.HttpException;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.JsonNode;

import cc.pp.tencent.jdbc.UbiaJDBC;
import cc.pp.tencent.utils.HttpUtils;
import cc.pp.tencent.utils.JsonUtils;

/**
 * Title: 获取新增用户
 * @author wanggang
 * @version 1.1
 * @since 2013-05-27
 */
public class GetAddOauthUsers implements Serializable {
	
	/**
	 * 默认的序列化版本号
	 */
	private static final long serialVersionUID = 1L;
	
	// http://t2.pp.cc/api/bind/index?type=sina&token=b14d48ede1f7c2f7779585ef3f9102f0
	// http://t2.pp.cc/api/bind/index?type=
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
		gaau.insertAddUsersByDay("tencent", "0", "192.168.1.27");

	}
	
	/**
	 * 获取每天增加的用户
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
			if (StringUtils.isEmpty(info)) {
				continue;
			}
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
		String tokensecret = new String();
		String expires = new String();
		UbiaJDBC ubiajdbc = new UbiaJDBC(ip);
		if (ubiajdbc.mysqlStatus()) {
			for (int i = 0; i < jsondata.size(); i++) {
				bind = jsondata.get(i).get("bid").toString().replaceAll("\"", "");
				username = jsondata.get(i).get("bind_username").toString().replaceAll("\"", "");
				accesstoken = jsondata.get(i).get("bind_access_token").toString().replaceAll("\"", "");
				tokensecret = jsondata.get(i).get("bind_token_secret").toString().replaceAll("\"", "");
				expires = jsondata.get(i).get("expires").toString().replaceAll("\"", "");
				ubiajdbc.insertT2Users(bind, username, accesstoken, tokensecret, expires);
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
