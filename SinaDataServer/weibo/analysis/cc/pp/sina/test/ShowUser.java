package cc.pp.sina.test;

import com.sina.weibo.api.Users;
import com.sina.weibo.json.JSONArray;
import com.sina.weibo.json.JSONException;
import com.sina.weibo.model.WeiboException;


public class ShowUser {

	public static void main(String[] args) throws JSONException {

		Users um = new Users();
		um.client.setToken("2.00GayTpCdcZIJCca884a569cJTMUbD");
		try {
			JSONArray user = um.getUserCount("3242227264,2634461024");
			System.out.println(user.toString());
			System.out.println(user.get(0));
		} catch (WeiboException e) {
			e.printStackTrace();
		}
	}
}
