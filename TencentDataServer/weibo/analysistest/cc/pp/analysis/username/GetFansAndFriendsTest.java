package cc.pp.analysis.username;

import java.sql.SQLException;

import cc.pp.tencent.analysis.username.GetFansAndFriends;
import cc.pp.tencent.jdbc.UsernameJDBC;

public class GetFansAndFriendsTest {

	private final GetFansAndFriends getFansAndFriends = new GetFansAndFriends();
	String[] uids = { "cxpolice", "a1211093047" };
	String token = "2c656d405a1d4587b7b99d143102d002,b36e96d43ed75485258f0fdf0cefe143";
	UsernameJDBC myJdbc = new UsernameJDBC("127.0.0.1", "root", "");

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {

		GetFansAndFriendsTest getFansAndFriendsTest = new GetFansAndFriendsTest();
		//		getFansAndFriendsTest.createTableTest();
		getFansAndFriendsTest.getUserBaseInfoTest();
		//		getFansAndFriendsTest.dumpFansToDbTest();
		//		getFansAndFriendsTest.dumpFriendsToDbTest();

	}

	/**
	 * 数据表创建测试
	 * @throws SQLException 
	 */
	public void createTableTest() throws SQLException {

		if (myJdbc.mysqlStatus()) {
			myJdbc.createBaseInfoTable("tencentuserbaseinfo");
			myJdbc.createUidsTable("tencentuids");
			myJdbc.createFriendsTable("tencentfriends");
			myJdbc.createUsersTable("tencentusers");
			myJdbc.createTablenames("tencenttablenames");
			myJdbc.sqlClose();
		}
	}

	/**
	 * 腾讯用户基础信息获取测试
	 * @throws Exception
	 */
	public void getUserBaseInfoTest() throws Exception {

		int[] fansfriends = new int[2];
		if (myJdbc.mysqlStatus()) {
			for (int i = 0; i < uids.length; i++) {
				fansfriends = getFansAndFriends.getUserBaseInfo(myJdbc, token, uids[i]);
				System.out.println(fansfriends[0] + "," + fansfriends[1]);
			}
			myJdbc.sqlClose();
		}
	}

	/**
	 * 用户粉丝获取测试
	 * @throws Exception
	 */
	public void dumpFansToDbTest() throws Exception {

		int index;
		if (myJdbc.mysqlStatus()) {
			index = getFansAndFriends.dumpFansToDb(myJdbc, token, uids[0], 20000, "tencentuids");
			//			index = getFansAndFriends.dumpFansToDb(myJdbc, tokens, uids[0], 10000);
			System.out.println(index);
			myJdbc.sqlClose();
		}
	}

	/**
	 * 用户关注获取测试
	 * @throws Exception
	 */
	public void dumpFriendsToDbTest() throws Exception {

		int index;
		if (myJdbc.mysqlStatus()) {
			index = getFansAndFriends.dumpFriendsToDb(myJdbc, token, uids[0], 379, "tencentuids", "tencentfriends");
			System.out.println(index);
			myJdbc.sqlClose();
		}
	}

}
