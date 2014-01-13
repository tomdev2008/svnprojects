package weibo4j.examples.friendships;

import com.sina.weibo.api.Friendships;
import com.sina.weibo.model.User;
import com.sina.weibo.model.WeiboException;

import weibo4j.examples.oauth2.Log;

public class CreateFriendships {

	public static void main(String[] args) {
		String access_token = args[0];
		String uid = args[1];
		Friendships fm = new Friendships();
		fm.client.setToken(access_token);
		try {
			User user = fm.createFriendshipsById(uid);
			Log.logInfo(user.toString());
		} catch (WeiboException e) {
			e.printStackTrace();
		}

	}

}