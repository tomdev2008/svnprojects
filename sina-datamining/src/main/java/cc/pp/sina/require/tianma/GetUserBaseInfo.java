package cc.pp.sina.require.tianma;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import cc.pp.sina.jdbc.LocalJDBC;

import com.sina.weibo.api.Users;
import com.sina.weibo.model.User;
import com.sina.weibo.model.WeiboException;

public class GetUserBaseInfo {

	/**
	 * 
	 * @param args
	 * @throws IOException
	 * @throws SQLException
	 * @throws WeiboException
	 */
	public static void main(String[] args) throws IOException, SQLException, WeiboException {

		GetUserBaseInfo getUserBaseInfo = new GetUserBaseInfo();
		getUserBaseInfo.spiderRepUsersInfo();
		//		String accesstoken = "2.00vyKmPBdcZIJCab7e4cdb505dUSLC";
		//		Users user = new Users();
		//		user.client.setToken(accesstoken);
		//		User userinfo = user.showUserByDomain("l00t");
		//		System.out.println(userinfo);

	}

	/**
	 * 
	 * @throws SQLException
	 * @throws WeiboException
	 */
	public void spiderRepUsersInfo() throws SQLException, WeiboException {

		LocalJDBC myjdbc = new LocalJDBC("127.0.0.1", "root", "");

		if (myjdbc.mysqlStatus()) {

			String[] tokens = myjdbc.getAccessToken(8000);
			List<HashMap<String, String>> repusers = myjdbc.getUsersNickname("singleweiboinfo", 8000);
			int i = 0;
			for (HashMap<String, String> temp : repusers) {

				System.out.println(i);

				Users user = new Users();
				user.client.setToken(tokens[i++]);
				User userinfo = null;
				if ("domainname".equals(temp.get("name"))) {
					userinfo = user.showUserById(temp.get("data"));
				} else {
					userinfo = user.showUserByScreenName(temp.get("data"));
				}

				try {
					if (userinfo.getId().length() > 5) {
						myjdbc.insertUsersInfo("sinauserinfo", userinfo.getId(), userinfo.getScreenName(), //
								0, userinfo.getDescription(), userinfo.getGender(), //
								userinfo.getProvince(), userinfo.getFollowersCount(), userinfo.getFriendsCount(), //
								userinfo.getStatusesCount(), userinfo.isVerified(), userinfo.getVerifiedType(), //
								userinfo.getVerifiedReason(), 0);
					} else {
						//
					}
				} catch (RuntimeException e) {
					//
				}
			}
			myjdbc.sqlClose();
		}
	}

	/**
	 * 
	 * @throws WeiboException
	 * @throws NumberFormatException
	 * @throws IOException
	 * @throws SQLException
	 */
	public void dumpBaseinfo() throws WeiboException, NumberFormatException, IOException, SQLException {

		LocalJDBC myjdbc = new LocalJDBC("127.0.0.1", "root", "");
		if (myjdbc.mysqlStatus()) {
			String[] tokens = myjdbc.getAccessToken(1000);
			BufferedReader fr = new BufferedReader(new FileReader(new File("users.txt")));
			String str = "";
			String uid, usedcount;
			int i = 0;
			while ((str = fr.readLine()) != null) {
				System.out.println(i);
				uid = str.split("\t")[0];
				usedcount = str.split("\t")[1];
				Users user = new Users();
				user.client.setToken(tokens[i++]);
				User userinfo = user.showUserById(uid);
				try {
					if (userinfo.getId().length() > 5) {
						myjdbc.insertUsersInfo("sinauserinfo", userinfo.getId(), userinfo.getScreenName(), //
								Integer.parseInt(usedcount), userinfo.getDescription(), userinfo.getGender(), //
								userinfo.getProvince(), userinfo.getFollowersCount(), userinfo.getFriendsCount(), //
								userinfo.getStatusesCount(), userinfo.isVerified(), userinfo.getVerifiedType(), //
								userinfo.getVerifiedReason(), 0);
					} else {
						myjdbc.insertUsersInfo("sinauserinfo", uid, "", Integer.parseInt(usedcount), "", "", 0, 0, 0,
								0, false, -1, "", 0);
					}
				} catch (RuntimeException e) {
					myjdbc.insertUsersInfo("sinauserinfo", uid, "", Integer.parseInt(usedcount), "", "", 0, 0, 0, 0,
							false, -1, "", 0);
				}
			}
			fr.close();
			myjdbc.sqlClose();
		}
	}

}
