package cc.pp.sina.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import cc.pp.sina.analysis.ppusers.PPSinaUser;
import cc.pp.sina.analysis.ppusers.PPTencentUser;
import cc.pp.sina.constant.DataBaseConstant;

public class PPUJDBC implements DataBaseConstant {

	private Connection conn;
	private final String driver = DRIVER;
	private String url = new String();
	private String user = USER;
	private String password = PASSWORD;

	/**
	 * @ construct
	 * @param ip
	 */
	public PPUJDBC(String ip) {
		this.url = "jdbc:mysql://" + ip + ":3306/pp_fenxi?useUnicode=true&characterEncoding=utf-8";
	}

	public PPUJDBC(String ip, String user, String password) {
		this.url = "jdbc:mysql://" + ip + ":3306/pp_fenxi?useUnicode=true&characterEncoding=utf-8";
		this.user = user;
		this.password = password;
	}

	/**
	 * @ 判断链接
	 * @return
	 */
	public boolean mysqlStatus() {

		boolean status = true;
		try {
			Class.forName(this.driver);
			this.conn = DriverManager.getConnection(this.url, this.user, this.password);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			status = false;
		} catch (SQLException e) {
			e.printStackTrace();
			status = false;
		} catch (Exception e) {
			e.printStackTrace();
			status = false;
		}

		return status;
	}

	/**
	 * 获取一定数量有效token
	 * @param nums
	 * @return
	 * @throws SQLException
	 */
	public String[] getAccessToken(int nums) throws SQLException {

		String[] result = new String[nums];
		String[] tokens = this.getAccessTokenone(nums);
		long time = System.currentTimeMillis() / 1000;
		int flag = 0;
		long expiresin = 0;

		for (int i = 0; i < tokens.length; i++) {
			expiresin = Long.parseLong(tokens[i].substring(tokens[i].indexOf(",") + 1));
			if (expiresin < time) {
				this.deleteAccessToken(result[0]);
			} else {
				result[flag++] = tokens[i].substring(0, tokens[i].indexOf(","));
				this.updateAccessToken(result[0]);
			}
			if (flag >= nums) {
				break;
			}
		}

		while (flag < nums) {
			tokens = this.getAccessTokenone(nums);
			for (int i = 0; i < tokens.length; i++) {
				expiresin = Long.parseLong(tokens[i].substring(tokens[i].indexOf(",") + 1));
				if (expiresin < time) {
					this.deleteAccessToken(result[0]);
				} else {
					result[flag++] = tokens[i].substring(0, tokens[i].indexOf(","));
					this.updateAccessToken(result[0]);
				}
				if (flag >= nums) {
					break;
				}
			}
		}

		return result;
	}

	/**
	 * 获取一定数据量token
	 * @param nums
	 * @return
	 * @throws SQLException
	 */
	public String[] getAccessTokenone(int nums) throws SQLException {

		String[] result = new String[nums * 2];
		String sql = new String("SELECT `oauth_token`,`expires_in` FROM `member_bind_sina` ORDER BY `lasttime` LIMIT "
				+ nums * 2);
		Statement statement = this.conn.createStatement();
		ResultSet rs = statement.executeQuery(sql);
		int i = 0;
		while (rs.next()) {
			result[i++] = rs.getString("oauth_token") + "," + rs.getString("expires_in");
		}
		rs.close();
		statement.close();

		return result;
	}

	/**
	 * 获取单个有效token
	 * @return
	 * @throws SQLException
	 */
	public String getAccessToken() throws SQLException {

		String[] result = this.getAccessTokenone();
		long time = System.currentTimeMillis() / 1000;
		while (Integer.parseInt(result[1]) < time) {
			this.deleteAccessToken(result[0]);
			result = this.getAccessTokenone();
		}
		this.updateAccessToken(result[0]);

		return result[0];
	}

	/**
	 * 获取单个token
	 * @return
	 * @throws SQLException
	 */
	public String[] getAccessTokenone() throws SQLException {

		String[] result = new String[2];
		String sql = new String("SELECT `oauth_token`,`expires_in` FROM `member_bind_sina` LIMIT 1");
		Statement statement = this.conn.createStatement();
		ResultSet rs = statement.executeQuery(sql);
		if (rs.next()) {
			result[0] = rs.getString("oauth_token");
			result[1] = rs.getString("expires_in");
		}
		rs.close();
		statement.close();

		return result;
	}

	/**
	 * 更新token的时间
	 * @param token
	 * @throws SQLException
	 */
	public void updateAccessToken(String token) throws SQLException {

		long time = System.currentTimeMillis() / 1000;
		String sql = new String("UPDATE `member_bind_sina` SET `lasttime` = " + time + " WHERE `oauth_token` = \""
				+ token + "\"");
		Statement statement = this.conn.createStatement();
		statement.executeUpdate(sql);
		statement.close();
	}

	/**
	 * 删除无效token
	 * @param token
	 * @throws SQLException
	 */
	public void deleteAccessToken(String token) throws SQLException {

		String sql = new String("DELETE FROM `member_bind_sina` WHERE `oauth_token` = \"" + token + "\"");
		Statement statement = this.conn.createStatement();
		statement.execute(sql);
		statement.close();
	}

	public void insertUsersInfo(String tablename, String username, String nickname, String description, int gender,
			int province, int fanscount, int friendscount, int weibocount, int verify, int verifytype,
			String verifiedreason, long lastwbcreated) throws SQLException {

		String sql = "INSERT INTO " + tablename + " (`username`,`nickname`,`description`," + //
				"`gender`,`province`,`fanscount`,`friendscount`,`weibocount`,`verify`,`verifytype`," + //
				"`verifiedreason`,`lastwbcreated`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
		PreparedStatement pstmt = null;
		try {
			pstmt = this.conn.prepareStatement(sql);
			pstmt.setString(1, username);
			pstmt.setString(2, nickname);
			//			pstmt.setString(3, description.replaceAll("x", ""));
			pstmt.setString(3, "0");
			pstmt.setInt(4, gender);
			pstmt.setInt(5, province);
			pstmt.setInt(6, fanscount);
			pstmt.setInt(7, friendscount);
			pstmt.setInt(8, weibocount);
			pstmt.setInt(9, verify);
			pstmt.setInt(10, verifytype);
			pstmt.setString(11, verifiedreason);
			pstmt.setLong(12, lastwbcreated);
			pstmt.executeUpdate();
		} finally {
			if (pstmt != null) {
				pstmt.close();
			}
		}
	}


	public void insertUsersInfo(String tablename, String username, String nickname, String description, int gender,
			int province, int fanscount, int friendscount, int weibocount, int verify, int verifytype,
			String verifiedreason) throws SQLException {

		String sql = "INSERT INTO " + tablename + " (`username`,`nickname`,`description`," + //
				"`gender`,`province`,`fanscount`,`friendscount`,`weibocount`,`verify`,`verifytype`," + //
				"`verifiedreason`) VALUES (?,?,?,?,?,?,?,?,?,?,?)";
		PreparedStatement pstmt = null;
		try {
			pstmt = this.conn.prepareStatement(sql);
			pstmt.setString(1, username);
			pstmt.setString(2, nickname);
			//			pstmt.setString(3, description.replaceAll("x", ""));
			pstmt.setString(3, "0");
			pstmt.setInt(4, gender);
			pstmt.setInt(5, province);
			pstmt.setInt(6, fanscount);
			pstmt.setInt(7, friendscount);
			pstmt.setInt(8, weibocount);
			pstmt.setInt(9, verify);
			pstmt.setInt(10, verifytype);
			pstmt.setString(11, verifiedreason);
			pstmt.executeUpdate();
		} finally {
			if (pstmt != null) {
				pstmt.close();
			}
		}
	}
	
	public List<PPSinaUser> getResult(String tablename) throws SQLException {
		
		List<PPSinaUser> result = new ArrayList<PPSinaUser>();
		String sql = "SELECT `username`,`nickname`,`description`,`gender`,`province`,`fanscount`," + //
				"`friendscount`,`weibocount`,`verify`,`verifytype`,`verifiedreason`,`lastwbcreated` FROM " + tablename;
		PreparedStatement pstmt = null;
		try {
			pstmt = this.conn.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				PPSinaUser user = new PPSinaUser();
				user.setUsername(rs.getString(1));
				user.setNickname(rs.getString(2));
				user.setDescription(rs.getString(3));
				user.setGender(rs.getInt(4));
				user.setProvince(rs.getInt(5));
				user.setFanscount(rs.getInt(6));
				user.setFriendscount(rs.getInt(7));
				user.setWeibocount(rs.getInt(8));
				user.setVerify(rs.getInt(9));
				user.setVerifytype(rs.getInt(10));
				user.setVerifiedreason(rs.getString(11));
				user.setLastwbcreated(rs.getLong(12));
				result.add(user);
			}
			return result;
		} finally {
			if (pstmt != null) {
				pstmt.close();
			}
		}
	}

	public void insertTencentUsersInfo(String tablename, String username, String nickname, String description,
			int isvip, int fanscount, int friendscount, int weibocount, int exp, int isrealname, int level, int sex,
			int industrycode, int isent, String province, long lastwbcreated, String verifiedreason)
			throws SQLException {

		String sql = "INSERT INTO " + tablename + " (`username`,`nickname`,`description`,`isvip`," + //
				" `fanscount`,`friendscount`,`weibocount`,`exp`,`isrealname`,`level`,`sex`," + //
				"`industrycode`,`isent`,`province`,`lastwbcreated`,`verifiedreason`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		PreparedStatement pstmt = null;
		try {
			pstmt = this.conn.prepareStatement(sql);
			pstmt.setString(1, username);
			pstmt.setString(2, nickname);
			pstmt.setString(3, description);
			pstmt.setInt(4, isvip);
			pstmt.setInt(5, fanscount);
			pstmt.setInt(6, friendscount);
			pstmt.setInt(7, weibocount);
			pstmt.setInt(8, exp);
			pstmt.setInt(9, isrealname);
			pstmt.setInt(10, level);
			pstmt.setInt(11, sex);
			pstmt.setInt(12, industrycode);
			pstmt.setInt(13, isent);
			if (province.length() > 0) {
				pstmt.setString(14, province);
			} else {
				pstmt.setString(14, "0");
			}

			pstmt.setLong(15, lastwbcreated);
			pstmt.setString(16, verifiedreason);
			pstmt.executeUpdate();
		} finally {
			if (pstmt != null) {
				pstmt.close();
			}
		}
	}
	
    public List<PPTencentUser> getTencentResult(String tablename) throws SQLException {
		
		List<PPTencentUser> result = new ArrayList<PPTencentUser>();
		String sql = "SELECT `username`,`nickname`,`description`,`isvip`,`fanscount`,`friendscount`," + //
				"`weibocount`,`exp`,`isrealname`,`level`,`sex`,`industrycode`,`isent`,`province`," + //
				"`lastwbcreated`,`verifiedreason` FROM " + tablename;
		PreparedStatement pstmt = null;
		try {
			pstmt = this.conn.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				PPTencentUser user = new PPTencentUser();
				user.setUsername(rs.getString(1));
				user.setNickname(rs.getString(2));
				user.setDescription(rs.getString(3));
				user.setIsvip(rs.getInt(4));
				user.setFanscount(rs.getInt(5));
				user.setFriendscount(rs.getInt(6));
				user.setWeibocount(rs.getInt(7));
				user.setExp(rs.getInt(8));
				user.setIsrealname(rs.getInt(9));
				user.setLevel(rs.getInt(10));
				user.setSex(rs.getInt(11));
				user.setIndustrycode(rs.getInt(12));
				user.setIsent(rs.getInt(13));
				user.setProvince(rs.getString(14));
				user.setLastwbcreated(rs.getInt(15));
				user.setVerifiedreason(rs.getString(16));
				result.add(user);
			}
			return result;
		} finally {
			if (pstmt != null) {
				pstmt.close();
			}
		}
	}

	/**
	 * 获取新浪用户名
	 * @param tablename
	 * @param num
	 * @return
	 * @throws SQLException
	 */
	public List<String> getSinaUsers(String tablename, int num) throws SQLException {

		List<String> result = new ArrayList<String>();
		String sql = "SELECT `username` FROM " + tablename + " LIMIT " + num;
		PreparedStatement pstmt = this.conn.prepareStatement(sql);
		ResultSet rs = pstmt.executeQuery();
		while (rs.next()) {
			result.add(rs.getString(1));
		}
		rs.close();
		pstmt.close();

		return result;
	}

	public HashMap<Integer, String> getPPSinaUsers(String tablename) throws SQLException {

		HashMap<String, String> temp = new HashMap<String, String>();
		String sql = "SELECT `username` FROM " + tablename;
		PreparedStatement pstmt = this.conn.prepareStatement(sql);
		ResultSet rs = pstmt.executeQuery();
		while (rs.next()) {
			temp.put(rs.getString(1), "OK");
		}
		HashMap<Integer, String> result = new HashMap<Integer, String>();
		int i = 0;
		for (Entry<String, String> user : temp.entrySet()) {
			result.put(i++, user.getKey());
		}

		rs.close();
		pstmt.close();

		return result;
	}

	/**
	 * 插入用户名
	 * @param tablename
	 * @param uids
	 * @throws SQLException
	 */
	public void insertSinausers(String tablename, List<String> uids) throws SQLException {

		String sql = new String("INSERT INTO " + tablename + " (`username`) VALUES (?)");
		PreparedStatement pstmt = null;
		try {
			pstmt = this.conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
			for (String uid : uids) {
				pstmt.setString(1, uid);
				pstmt.addBatch();
			}
			pstmt.executeBatch();
		} finally {
			if (pstmt != null) {
				pstmt.close();
			}
		}
	}

	public void deleteBatch(String tablename, List<String> sinausers) throws SQLException {

		String sql = "DELETE FROM " + tablename + " WHERE `username` = ?";
		PreparedStatement pstmt = null;
		try {
			pstmt = this.conn.prepareStatement(sql);
			for (String user : sinausers) {
				pstmt.setString(1, user);
				pstmt.addBatch();
			}
			pstmt.executeBatch();
		} finally {
			if (pstmt != null) {
				pstmt.close();
			}
		}
	}

	public void deleteTable(String tablename) throws SQLException {

		String sql = "DROP TABLE " + tablename;
		PreparedStatement pstmt = null;
		try {
			pstmt = this.conn.prepareStatement(sql);
			pstmt.executeUpdate();
		} finally {
			if (pstmt != null) {
				pstmt.close();
			}
		}
	}

	public void insertUnusedUsers(String tablename, String username) throws SQLException {

		String sql = new String("INSERT INTO " + tablename + " (`username`) " + "VALUES (\"" + username + "\")");
		Statement statement = this.conn.createStatement();
		statement.execute(sql);
		statement.close();
	}

	/**
	 * @ 关闭数据库
	 * @throws SQLException
	 */
	public void sqlClose() throws SQLException {
		this.conn.close();
	}

}
