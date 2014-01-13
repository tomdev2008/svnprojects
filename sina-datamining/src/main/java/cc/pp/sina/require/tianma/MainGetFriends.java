package cc.pp.sina.require.tianma;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;

import cc.pp.sina.jdbc.LocalJDBC;

import com.sina.weibo.api.Friendships;
import com.sina.weibo.model.WeiboException;

public class MainGetFriends {

	private final List<String> cates = new ArrayList<String>();
	private final ReaderClassAndKeys reader;
	private final Random random = new Random();

	public MainGetFriends(String filepath) throws IOException {
		reader = new ReaderClassAndKeys(filepath);
		for (List<String> cate : reader.getClassandkeys()) {
			cates.add(cate.get(0));
		}
	}

	/**
	 * @param args
	 * @throws WeiboException 
	 * @throws SQLException 
	 * @throws IOException 
	 */
	public static void main(String[] args) throws SQLException, WeiboException, IOException {

		MainGetFriends getFriends = new MainGetFriends("testdata/classandkeys.txt");
		//		getFriends.dumpUserFriendsToDb();
		getFriends.importDataToTxt();

	}

	/**
	 * 把关注数据写入txt中
	 * @throws IOException
	 * @throws SQLException
	 */
	public void importDataToTxt() throws IOException, SQLException {

		LocalJDBC myJdbc = new LocalJDBC("127.0.0.1", "root", "");
		if (myJdbc.mysqlStatus()) {
			for (String cate : cates) {
				int max = myJdbc.getMaxFriends("friendsinfo_" + cate);
				int page = max / 5000;
				BufferedWriter fw = new BufferedWriter(new FileWriter("friends/frienduids_" + cate + ".txt", true));
				for (int i = 0; i <= page; i++) {
					System.out.println(cate + ": " + i);
					HashMap<String, String> friends = myJdbc.getFriendUids("friendsinfo_" + cate, i * 5000,
							(i + 1) * 5000);
					for (Entry<String, String> temp : friends.entrySet()) {
						//						fw.append(temp.getKey() + "\t" + temp.getValue());
						fw.append(temp.getValue().replaceAll(",", "\t"));
						fw.newLine();
					}
				}
				fw.close();
			}
			myJdbc.sqlClose();
		}
	}

	/**
	 * 采集用户关注信息
	 * @throws SQLException
	 * @throws WeiboException 
	 */
	public void dumpUserFriendsToDb() throws SQLException, WeiboException {

		LocalJDBC jdbc = new LocalJDBC("127.0.0.1", "root", "");
		if (jdbc.mysqlStatus()) {
			for (String cate : cates) {
				jdbc.createFriendsTable("friendsinfo_" + cate);
			}
			List<String> tokens = jdbc.getAllTokens();
			String accesstoken = "";
			for (String cate : cates) {

				System.out.println(cate);

				List<String> sinausers = jdbc.getSinaUsers("sinausers_" + cate, 100000);
				for (String uid : sinausers) {
					accesstoken = tokens.get(random.nextInt(tokens.size()));
					this.importFriendsUidsToDb(jdbc, "friendsinfo_" + cate, accesstoken, uid);
				}
			}
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
	public void importFriendsUidsToDb(LocalJDBC myJdbc, String tablename, String accesstoken, String uid)
			throws SQLException, WeiboException {

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
			myJdbc.insertFriendUids(tablename, uid, friends.length, frienduids);
		} catch (RuntimeException e) {
			//
		}

	}

	public List<String> getCates() {
		return cates;
	}

	public int getCatesSize() {
		return cates.size();
	}

}
