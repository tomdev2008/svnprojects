package cc.pp.sina.require.tianma;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import cc.pp.sina.jdbc.LocalJDBC;

import com.sina.weibo.api.Friendships;
import com.sina.weibo.model.WeiboException;

public class GetUserFriends {

	/**
	 * 主函数
	 * @param args
	 * @throws WeiboException
	 * @throws SQLException
	 * @throws IOException
	 */
	public static void main(String[] args) throws WeiboException, SQLException, IOException {

		GetUserFriends getUserFriends = new GetUserFriends();
		getUserFriends.importDataToTxt();

	}

	public void removeDuplicate() throws SQLException {

		LocalJDBC myJdbc = new LocalJDBC("127.0.0.1", "root", "");
		if (myJdbc.mysqlStatus()) {
			HashMap<String, String> sinausers = myJdbc.getSinaUsers("sinausers");
			for (Entry<String, String> temp : sinausers.entrySet()) {
				myJdbc.insertSinaUser("sinausers1", temp.getKey());
			}
			myJdbc.sqlClose();
		}
	}

	public void importDataToTxt() throws IOException, SQLException {

		LocalJDBC myJdbc = new LocalJDBC("127.0.0.1", "root", "");
		if (myJdbc.mysqlStatus()) {
			BufferedWriter fw = new BufferedWriter(new FileWriter("frienduids.txt", true));
			for (int i = 0; i < 12; i++) {
				System.out.println(i);
				HashMap<String, String> friends = myJdbc.getFriendUids("friendsinfo", i * 5000, (i + 1) * 5000);
				for (Entry<String, String> temp : friends.entrySet()) {
					fw.append(temp.getKey() + "\t" + temp.getValue());
					fw.newLine();
				}
			}
			fw.close();
			myJdbc.sqlClose();
		}
	}

	public void dumpFriendUids() throws SQLException, WeiboException {

		LocalJDBC myJdbc = new LocalJDBC("127.0.0.1", "root", "");
		if (myJdbc.mysqlStatus()) {
			for (int l = 0; l < 1; l++) {
				System.out.println("page: " + l);
				String[] tokens = myJdbc.getAccessToken(100);
				List<String> sinausers = myJdbc.getSinaUsers("sinausers", 100);
				int i = 0;
				for (String uid : sinausers) {
					importFriendsUidsToDb(myJdbc, tokens[i++], uid);
				}
				myJdbc.deleteBatch("sinausers", sinausers);
			}
			myJdbc.sqlClose();
		}
	}

	/**
	 * 插入关注者uid到数据库
	 * @param myJdbc
	 * @param accesstoken
	 * @param uid
	 * @throws SQLException
	 * @throws WeiboException
	 */
	public void importFriendsUidsToDb(LocalJDBC myJdbc, String accesstoken, String uid) throws SQLException,
			WeiboException {

		Friendships fm = new Friendships();
		fm.client.setToken(accesstoken);
		String[] friends = null;
		try {
			friends = fm.getFriendsIdsByUid(uid, 5000, 0);
			String frienduids = "";
			for (int i = 0; i < friends.length; i++) {
				frienduids = frienduids + friends[i] + ",";
			}
			frienduids = frienduids.substring(0, frienduids.length() - 1);
			myJdbc.insertFriendUids("friendsinfo", uid, friends.length, frienduids);
		} catch (RuntimeException e) {
			//
		}

	}

	/**
	 * 获取一定数量的token
	 * @param nums
	 * @return
	 * @throws SQLException
	 */
	public String[] getTokens(int nums) throws SQLException {

		LocalJDBC myJdbc = new LocalJDBC("127.0.0.1", "root", "");
		if (myJdbc.mysqlStatus()) {
			String[] tokensStrings = myJdbc.getAccessToken(nums);
			myJdbc.sqlClose();
			return tokensStrings;
		} else {
			return null;
		}
	}

}
