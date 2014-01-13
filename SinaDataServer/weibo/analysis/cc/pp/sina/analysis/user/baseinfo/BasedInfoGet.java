package cc.pp.sina.analysis.user.baseinfo;

import java.io.IOException;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.HashMap;

import net.sf.json.JSONObject;

import org.apache.commons.httpclient.HttpException;

import cc.pp.sina.algorithms.UnionMoreHashMap;
import cc.pp.sina.jdbc.UbiaJDBC;

import com.sina.weibo.api.Users;
import com.sina.weibo.json.JSONArray;
import com.sina.weibo.json.JSONException;
import com.sina.weibo.model.WeiboException;

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
	 * @throws SQLException 
	 * @throws JSONException 
	 * @throws WeiboException 
	 * @throws IOException 
	 * @throws HttpException 
	 */
	public static void main(String[] args) throws WeiboException, JSONException, SQLException, HttpException, IOException {

		BasedInfoGet big = new BasedInfoGet("192.168.1.27");
		GetAddOauthUsers gaau = new GetAddOauthUsers();
		HashMap<Integer,String> sinausers = gaau.getAddUsersByDay("sina", "0");
		big.insertBaseinfo(sinausers);
	}

	/**
	 * 获取基本信息
	 * @throws WeiboException
	 * @throws JSONException
	 * @throws SQLException
	 */
	public void insertBaseinfo(HashMap<Integer,String> ppusers) throws WeiboException, JSONException, SQLException {

		UbiaJDBC usermysql = new UbiaJDBC(ip);
		if (usermysql.mysqlStatus()) 
		{
			String uids = new String();
			for (int i = 0; i < ppusers.size();) 
			{
				uids = this.getUids(ppusers, i);

				int flag = this.insertUserData(usermysql, uids, "insertUserinfo");
				if (flag == 0) {
					i++;
					continue;
				}

				if (ppusers.size() - 100 > i) {
					i = i + 100;
				} else {
					i = ppusers.size();
				}
			}

			usermysql.sqlClose();
		}
	}

	/**
	 * 插入初始信息
	 * @throws WeiboException
	 * @throws JSONException
	 * @throws SQLException
	 */
	public void insertInitinfo(HashMap<Integer,String> ppusers) throws WeiboException, JSONException, SQLException {

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

				if (ppusers.size() - 100 > i) {
					i = i + 100;
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
	 * @throws SQLException 
	 * @throws WeiboException 
	 * @throws JSONException 
	 */
	public int insertUserData(UbiaJDBC usermysql, String uids, String insername) throws SQLException, WeiboException, JSONException {
		/********获取accesstoken和用户username*********/
		String accesstoken = usermysql.getAccessToken();
		/*****************采集用户信息******************/
		Users um = new Users();
		um.client.setToken(accesstoken);
		JSONArray users = um.getUserCount(uids);
		if (users == null) {
			return 0;
		}
		String username = new String();
		int followerscount = 0;
		int friendscount = 0;
		int statusescount = 0;
		JSONObject json;
		for (int j = 0; j < users.length(); j++) {
			json = JSONObject.fromObject(users.get(j).toString());
			username = json.getString("id");
			followerscount = json.getInt("followers_count");
			friendscount = json.getInt("friends_count");
			statusescount = json.getInt("statuses_count");
			if ("insertUserinfo".equals(insername)) {
				usermysql.insertUserinfo(username, followerscount, friendscount, statusescount);
			} else if ("insertTodayResult".equals(insername)) {
				UbiaUtils ubiaUtils = new UbiaUtils();
				usermysql.insertTodayResult1("sinauserinforesult", username + ubiaUtils.getTodayDate(), username,
						ubiaUtils.getTodayDate(), followerscount, friendscount, statusescount,
						0, 0, 0, "0.0%", "0.0%", "0.0%");
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
		String tablename = "sinauserinforesult";
		if (myjdbc.mysqlStatus()) {
			// 获取昨天五张数据表的数据
			HashMap<String,String> yesterdayinfo1 = myjdbc.getYesterdayResult(tablename + 0, date);
			HashMap<String,String> yesterdayinfo2 = myjdbc.getYesterdayResult(tablename + 1, date);
			HashMap<String,String> yesterdayinfo3 = myjdbc.getYesterdayResult(tablename + 2, date);
			HashMap<String,String> yesterdayinfo4 = myjdbc.getYesterdayResult(tablename + 3, date);
			HashMap<String,String> yesterdayinfo5 = myjdbc.getYesterdayResult(tablename + 4, date);
			// 合并HashMap
			UnionMoreHashMap umhm = new UnionMoreHashMap();
			HashMap<String,String> yesterdayinfo = umhm.unionHashMap(yesterdayinfo1, yesterdayinfo2, 
					yesterdayinfo3, yesterdayinfo4, yesterdayinfo5);

			myjdbc.sqlClose();

			return yesterdayinfo;
		} else {
			return null;
		}
	}

	/**
	 * 获取今天的数据
	 * @return
	 * @throws SQLException
	 */
	public HashMap<String,String> getTodayData(String date) throws SQLException {

		UbiaJDBC myjdbc = new UbiaJDBC(this.ip);
		String tablename = "sinauserinforesult";
		if (myjdbc.mysqlStatus()) {
			// 获取昨天五张数据表的数据
			HashMap<String,String> todayinfo1 = myjdbc.getTodayResult(tablename + 0, date);
			HashMap<String,String> todayinfo2 = myjdbc.getTodayResult(tablename + 1, date);
			HashMap<String,String> todayinfo3 = myjdbc.getTodayResult(tablename + 2, date);
			HashMap<String,String> todayinfo4 = myjdbc.getTodayResult(tablename + 3, date);
			HashMap<String,String> todayinfo5 = myjdbc.getTodayResult(tablename + 4, date);
			// 合并HashMap
			UnionMoreHashMap umhm = new UnionMoreHashMap();
			HashMap<String,String> todaydayinfo = umhm.unionHashMapNum(todayinfo1, todayinfo2,
					todayinfo3, todayinfo4, todayinfo5);

			myjdbc.sqlClose();

			return todaydayinfo;
		} else {
			return null;
		}
	}

	/**
	 * 获取用户的信息
	 * @param usernames
	 * @return
	 * @throws WeiboException
	 * @throws JSONException
	 * @throws SQLException
	 */
	public HashMap<String,String> getBaseinfo(HashMap<Integer,String> usernames) throws WeiboException, JSONException, SQLException {

		UbiaJDBC usermysql = new UbiaJDBC(this.ip);
		if (usermysql.mysqlStatus()) 
		{
			HashMap<String,String> result = new HashMap<String,String>();
			String uids = new String();
			/*****************获取所有pp用户****************/
			for (int i = 0; i < usernames.size();) 
			{
				uids = this.getUids(usernames, i);
				/********获取accesstoken和用户username*********/
				String accesstoken = usermysql.getAccessToken();
				/*****************采集用户信息******************/
				Users um = new Users();
				um.client.setToken(accesstoken);
				JSONArray users = um.getUserCount(uids);
				if (users == null) {
					i++;
					continue;
				}

				String username = new String();
				String info = new String();
				JSONObject json;
				for (int j = 0; j < users.length(); j++) {
					json = JSONObject.fromObject(users.get(j).toString());
					username = json.getString("id");
					info = json.getString("followers_count") + "," + json.getString("friends_count") + "," + json.getString("statuses_count");
					result.put(username, info);
				}

				if (usernames.size() - 100 > i) {
					i = i + 100;
				} else {
					i = usernames.size();
				}
			}
			usermysql.sqlClose();
			return result;
		} else {
			return null;
		}
	}

	/**
	 * 获取uid串
	 * @param usernames
	 * @return
	 */
	public String getUids(HashMap<Integer,String> usernames, int i ) {

		String uids = null;
		if (usernames.size() - 100 > i) {
			for (int k = 0; k < 100; k++) {
				uids += usernames.get(i+k) + ",";
			}
			i = i + 100;
		} else {
			for (int k = i; k < usernames.size(); k++) {
				uids += usernames.get(k) + ",";
			}
			i = usernames.size();
		}
		uids = uids.substring(0, uids.length() - 1);

		return uids;
	}

}
