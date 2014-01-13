package cc.pp.tencent.analysis.user.baseinfo;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.HashMap;

import net.sf.json.JSONException;

import org.codehaus.jackson.JsonNode;

import cc.pp.tencent.jdbc.UbiaJDBC;
import cc.pp.tencent.utils.JsonUtils;

import com.tencent.weibo.api.UserAPI;
import com.tencent.weibo.oauthv1.OAuthV1;

/**
 * Title:用户基础信息获取
 * @author wanggang
 * @version 1.1
 * @since 2013-05-27
 */
public class BasedInfoGet implements Serializable {

	/**
	 * 默认序列化版本号
	 */
	private static final long serialVersionUID = 1L;
	
	private String ip = "";

	public BasedInfoGet(String ip) {
		this.ip = ip;
	}

	/**
	 * 测试函数
	 * @param args
	 * @throws Exception 
	 * @throws JSONException 
	 * @throws WeiboException 
	 */
	public static void main(String[] args) throws Exception {

		BasedInfoGet big = new BasedInfoGet("192.168.1.27");
		GetAddOauthUsers gaau = new GetAddOauthUsers();
		HashMap<Integer,String> tencentusers = gaau.getAddUsersByDay("tencent", "0");
		big.insertBaseinfo(tencentusers);

	}
	
	/**
	 * 获取基本信息
	 * @throws Exception
	 */
	public void insertBaseinfo(HashMap<Integer,String> ppusers) throws Exception {

		UbiaJDBC usermysql = new UbiaJDBC(this.ip);
		String uids = new String();
		if (usermysql.mysqlStatus()) 
		{
			for (int i = 0; i < ppusers.size();) 
			{
				uids = this.getUids(ppusers, i);
				
				int flag = this.insertUserData(usermysql, uids, "insertUserinfo");
				if (flag == 0) {
					i++;
					continue;
				}
				
				if (ppusers.size() - 30 > i) {
					i = i + 30;
				} else {
					i = ppusers.size();
				}
			}

			usermysql.sqlClose();
		}
	}
	
	/**
	 * 插入初始信息
	 * @throws Exception 
	 * @throws WeiboException
	 * @throws JSONException
	 */
	public void insertInitinfo(HashMap<Integer,String> ppusers) throws Exception {

		UbiaJDBC usermysql = new UbiaJDBC(this.ip);
		if (usermysql.mysqlStatus()) 
		{
			String uids = new String();
			for (int i = 0; i < ppusers.size();) 
			{
				uids = this.getUids(ppusers, i);

				int flag = this.insertUserData(usermysql, uids, "insertTodayResult");
				if (flag == 0) {
					i++;
					continue;
				}
				
				if (ppusers.size() - 30 > i) {
					i = i + 30;
				} else {
					i = ppusers.size();
				}
			}

			usermysql.sqlClose();
		}
	}
	
	/**
	 * 插入用户数据
	 * @param usermysql
	 * @param uids
	 * @throws Exception 
	 * @throws WeiboException 
	 * @throws JSONException 
	 */
	public int insertUserData(UbiaJDBC usermysql, String uids, String insername) throws Exception {
		/********获取accesstoken和用户username*********/
		String[] accesstoken = usermysql.getAccessToken();
		/*****************采集用户信息******************/
		OAuthV1 oauth = new OAuthV1();
		BasedInfoGet.oauthInit(oauth, accesstoken[0], accesstoken[1]);
		UserAPI user = new UserAPI(oauth.getOauthVersion());
		String usersinfo = user.infos(oauth, "json", uids, "");
		JsonNode jsondata = JsonUtils.getJsonNode(usersinfo, "data");
		JsonNode jsoninfo = JsonUtils.getJsonNode(jsondata, "info");
		String username = new String();
		int followerscount = 0;
		int friendscount = 0;
		if (jsoninfo == null) {
			return 0;
		}
		for (int j = 0; j < jsoninfo.size(); j++) {
			username = jsoninfo.get(j).get("name").toString();
			followerscount = jsoninfo.get(j).get("fansnum").getIntValue();
			friendscount = jsoninfo.get(j).get("idolnum").getIntValue();
			username = username.replaceAll("\"", "");
			
			if ("insertUserinfo".equals(insername)) {
				usermysql.insertUserinfo(username, followerscount, friendscount);
			} else if ("insertTodayResult".equals(insername)) {
				UbiaUtils ubiaUtils = new UbiaUtils();
				usermysql.insertTodayResult1("tencentuserinforesult", username + ubiaUtils.getTodayDate(), 
						username, ubiaUtils.getTodayDate(), followerscount, friendscount, 0, 0, "0.0%", "0.0%");
			}
		}
		
		return 1;
	}
	
	/**
	 * 获取昨天的数据
	 * @return
	 * @throws SQLException
	 */
	public HashMap<String,String> getYesterdayData(String date) throws SQLException {
		
		UbiaJDBC myjdbc = new UbiaJDBC(this.ip);
		String tablename = "tencentuserinforesult";
		if (myjdbc.mysqlStatus()) {
			// 获取昨天五张数据表的数据
			HashMap<String,String> yesterdayinfo = myjdbc.getYesterdayResult(tablename, date);
			myjdbc.sqlClose();
			return yesterdayinfo;
		} else {
			return null;
		}
	}

	/**
	 * 获取用户的信息
	 * @param usernames
	 * @return
	 * @throws Exception
	 */
	public HashMap<String,String> getBaseinfo(HashMap<Integer,String> usernames) throws Exception {

		UbiaJDBC usermysql = new UbiaJDBC(this.ip);
		HashMap<String,String> result = new HashMap<String,String>();
		String uids = new String();
		if (usermysql.mysqlStatus()) 
		{
			/*****************获取所有pp用户****************/
			for (int i = 0; i < usernames.size();) 
			{
				uids = this.getUids(usernames, i);
				/********获取accesstoken和用户username*********/
				String[] accesstoken = usermysql.getAccessToken();
				/*****************采集用户信息******************/
				OAuthV1 oauth = new OAuthV1();
				BasedInfoGet.oauthInit(oauth, accesstoken[0], accesstoken[1]);
				UserAPI user = new UserAPI(oauth.getOauthVersion());
				String usersinfo = user.infos(oauth, "json", uids, "");
				JsonNode jsondata = JsonUtils.getJsonNode(usersinfo, "data");
				JsonNode jsoninfo = JsonUtils.getJsonNode(jsondata, "info");
				String username = new String();
				String followerscount = new String();
				String friendscount = new String();
				String info = new String();

				if (jsoninfo == null) {
					i++;
					continue;
				}
				
				for (int j = 0; j < jsoninfo.size(); j++) {
					username = jsoninfo.get(j).get("name").toString();
					username = username.replaceAll("\"", "");
					followerscount = jsoninfo.get(j).get("fansnum").toString();
					friendscount = jsoninfo.get(j).get("idolnum").toString();
					info = followerscount + "," + friendscount;
					result.put(username, info);
				}
				if (usernames.size() - 30 > i) {
					i = i + 30;
				} else {
					i = usernames.size();
				}
			}
			usermysql.sqlClose();
		}
		
		return result;
	}
	 
	/**
	 * 获取uid串
	 * @param usernames
	 * @return
	 */
	public String getUids(HashMap<Integer,String> usernames, int i ) {
		
		String uids = null;
		if (usernames.size() - 30 > i) {
			for (int k = 0; k < 30; k++) {
				uids += usernames.get(i+k) + ",";
			}
		} else {
			for (int k = i; k < usernames.size(); k++) {
				uids += usernames.get(k) + ",";
			}
		}
		uids = uids.substring(0, uids.length() - 1);
		
		return uids;
	}
	
	/**
	 * @授权参数初始化
	 * @param oauth
	 */
	public static void oauthInit(OAuthV1 oauth, String accesstoken, String tokensecret) {
		/***************皮皮时光机appkey和appsecret***********************/
		oauth.setOauthConsumerKey("11b5a3c188484c3f8654b83d32e19bab");
		oauth.setOauthConsumerSecret("dc5cd31e1ddf556a42a40a1cff7efd5c");
		/**************************************************************/
		//oauth.setOauthCallback("");
		oauth.setOauthToken(accesstoken);
		oauth.setOauthTokenSecret(tokensecret);
	}

}
