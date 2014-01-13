package cc.pp.sina.test;

import com.sina.weibo.api.Users;
import com.sina.weibo.model.User;
import com.sina.weibo.model.WeiboException;


public class ShowUser {

	public static void main(String[] args) {
//		String access_token = args[0];
//		String uid =args[1];
		String access_token = new String("2.003s_buBdcZIJC2bfa65d0adJGsU4B");
		String uid = new String("1273609757");
		Users um = new Users();
		um.client.setToken(access_token);
		try {
			User user = um.showUserById(uid);
			System.out.println(user);
			System.out.println(user.getStatusCode());
		} catch (WeiboException e) {
			e.printStackTrace();
		}
	}
}
