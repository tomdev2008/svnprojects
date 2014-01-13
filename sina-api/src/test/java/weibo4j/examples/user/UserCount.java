package weibo4j.examples.user;

import com.sina.weibo.api.Users;
import com.sina.weibo.json.JSONArray;
import com.sina.weibo.model.WeiboException;

import weibo4j.examples.oauth2.Log;

public class UserCount {

	public static void main(String[] args) {
		//		String access_token = args[0];
		//		String uids =  args[1];
		String access_token = new String("2.00dzHBCC05umf81823c6cbd7KZPJXE");
		String uids = new String("1862087393");

		Users um = new Users();
		um.client.setToken(access_token);
		try {
			JSONArray user = um.getUserCount(uids);
			Log.logInfo(user.toString());
		} catch (WeiboException e) {
			e.printStackTrace();
		}
	}

}
