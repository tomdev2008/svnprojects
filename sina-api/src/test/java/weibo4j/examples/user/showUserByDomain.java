package weibo4j.examples.user;

import com.sina.weibo.api.Users;
import com.sina.weibo.model.User;
import com.sina.weibo.model.WeiboException;

import weibo4j.examples.oauth2.Log;

public class showUserByDomain {

	public static void main(String[] args) {
		String access_token = args[0];
		String domain = args[1];
		Users um = new Users();
		um.client.setToken(access_token);
		try {
			User user = um.showUserByDomain(domain);
			Log.logInfo(user.toString());
		} catch (WeiboException e) {
			e.printStackTrace();
		}
	}

}
