package weibo4j.examples.friendships;

import com.sina.weibo.api.Friendships;
import com.sina.weibo.model.User;
import com.sina.weibo.model.UserWapper;
import com.sina.weibo.model.WeiboException;

import weibo4j.examples.oauth2.Log;

public class GetFollowers {

	public static void main(String[] args) {
		String access_token = args[0];
		Friendships fm = new Friendships();
		fm.client.setToken(access_token);
		String screen_name = args[1];
		try {
			UserWapper users = fm.getFollowersByName(screen_name);
			for (User u : users.getUsers()) {
				Log.logInfo(u.toString());
			}
			System.out.println(users.getNextCursor());
			System.out.println(users.getPreviousCursor());
			System.out.println(users.getTotalNumber());
		} catch (WeiboException e) {
			e.printStackTrace();
		}
	}

}
