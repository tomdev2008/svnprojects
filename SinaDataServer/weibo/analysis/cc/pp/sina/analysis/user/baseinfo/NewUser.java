package cc.pp.sina.analysis.user.baseinfo;

import java.sql.SQLException;

import net.sf.json.JSONObject;
import cc.pp.sina.jdbc.UbiaJDBC;

import com.sina.weibo.api.Users;
import com.sina.weibo.json.JSONArray;
import com.sina.weibo.json.JSONException;
import com.sina.weibo.model.WeiboException;

public class NewUser {
	
	private String ip = "";

	public NewUser(String ip) {
		this.ip = ip;
	}

	/**
	 * 测试函数
	 * @param args
	 * @throws SQLException
	 * @throws JSONException
	 * @throws WeiboException
	 */
	public static void main(String[] args) throws SQLException, JSONException, WeiboException {
		NewUser nu = new NewUser("192.168.1.27");
		String username = new String("1862087393");
		String[] result = nu.getInfo(username);
		for (int i = 0; i < result.length; i++) {
			System.out.println(result[i]);
		}
	}
	
	public String[] getInfo(String username) throws SQLException, JSONException, WeiboException {
		
		String[] info = new String[4];
		UbiaJDBC usermysql = new UbiaJDBC(this.ip);
		if (usermysql.mysqlStatus()) 
		{
			/********获取accesstoken和用户username*********/
			String accesstoken = usermysql.getAccessToken();
			/*****************采集用户信息******************/
			Users um = new Users();
			um.client.setToken(accesstoken);
			JSONArray users = um.getUserCount(username);
			if (users == null) {
				return null;
			}
			JSONObject json = JSONObject.fromObject(users.get(0).toString());
			int followerscount = json.getInt("followers_count");
			int friendscount = json.getInt("friends_count");
			int statusescount = json.getInt("statuses_count");
			/*****************插入到总用户列表******************/
			UbiaUtils ubiaUtils = new UbiaUtils();
			usermysql.insertTodayResult(ubiaUtils.getTableName(username), username + ubiaUtils.getTodayDate(),
					ubiaUtils.getTodayDate(), followerscount, friendscount, statusescount, 0, 0, 0, "0", "0", "0");
			
			usermysql.sqlClose();
		}
		
		return info;
	}

}
