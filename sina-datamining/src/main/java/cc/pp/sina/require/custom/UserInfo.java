package cc.pp.sina.require.custom;

import java.sql.SQLException;

import cc.pp.sina.jdbc.LocalJDBC;

import com.sina.weibo.api.Users;
import com.sina.weibo.model.User;
import com.sina.weibo.model.WeiboException;

public class UserInfo {

	/**
	 * @param args
	 * @throws WeiboException 
	 * @throws SQLException 
	 */
	public static void main(String[] args) throws WeiboException, SQLException {

		UserInfo userInfo = new UserInfo();
		String accesstoken = "2.00eEQZzBdcZIJC9afb135d07Rbw7NE";
		String uid = "2342318502"; //2342318502、2144684673
		userInfo.importUserinfoToDb(accesstoken, uid);
		System.out.println("OK");
	}

	/**
	 * 获取详细的基础信息
	 * @param user
	 * @param accesstoken
	 * @param uid
	 * @throws WeiboException
	 * @throws SQLException
	 */
	public void importUserinfoToDb(String accesstoken, String uid) throws WeiboException, SQLException {

		String tags = UserTags.userTags(uid, accesstoken);
		Users user = new Users();
		user.client.setToken(accesstoken);
		User userinfo = user.showUserById(uid);
		LocalJDBC myjdbc = new LocalJDBC("127.0.0.1", "root", "");

		if (myjdbc.mysqlStatus()) {

			myjdbc.insertUserBaseInfo("userinfo", userinfo.getId(), userinfo.getScreenName(), //
					userinfo.getProvince(), userinfo.getCity(), userinfo.getLocation(), //
					userinfo.getDescription(), userinfo.getProfileImageUrl(), userinfo.getGender(), //
					userinfo.getFollowersCount(), userinfo.getFriendsCount(), userinfo.getStatusesCount(), //
					userinfo.getFavouritesCount(), userinfo.getCreatedAt().getTime() / 1000, //
					userinfo.getVerifiedType(), userinfo.getVerifiedReason(), userinfo.getBiFollowersCount(), //
					userinfo.getStatus().getCreatedAt().getTime() / 1000, userinfo.getWeihao(), tags);
			myjdbc.sqlClose();
		}

	}

}
