package cc.pp.tencent.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import cc.pp.tencent.constant.DataBaseConstant;

public class PPJDBC implements DataBaseConstant {

	private Connection conn;
	private final String driver = DRIVER;
	private String url = new String();
	private String user = USER;
	private String password = PASSWORD;

	/**
	 * @construct
	 * @param ip
	 */
	public PPJDBC(String ip) {
		this.url = "jdbc:mysql://" + ip + ":3306/pp_fenxi?useUnicode=true&characterEncoding=utf-8";
	}

	public PPJDBC(String ip, String user, String password) {
		this.url = "jdbc:mysql://" + ip + ":3306/pp_fenxi?useUnicode=true&characterEncoding=utf-8";
		this.user = user;
		this.password = password;
	}

	/**
	 * @ 判断连接状态
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
	 * 获取一定数量有效token、secret，并更新
	 * @param nums
	 * @return
	 * @throws SQLException
	 */
	public String[] getAccessTokens(int nums) throws SQLException {

		String[] result = this.getAccessTokenNums(nums);
		String accesstoken = null;
		for (int i = 0; i < result.length; i++) {
			accesstoken = result[i].substring(0, result[i].indexOf(","));
			this.updateAccessToken(accesstoken);
		}

		return result;
	}

	/**
	 * 获取一定数量的token、secret
	 * @param nums
	 * @return
	 * @throws SQLException
	 */
	public String[] getAccessTokenNums(int nums) throws SQLException {

		String[] result = new String[nums];
		String sql = new String(
				"SELECT `access_token`,`token_secret` FROM `member_bind_tencent` ORDER BY `expires` LIMIT " + nums);
		Statement statement = this.conn.createStatement();
		ResultSet rs = statement.executeQuery(sql);
		int i = 0;
		while (rs.next()) {
			result[i++] = rs.getString("access_token") + "," + rs.getString("token_secret");
		}
		rs.close();
		statement.close();

		return result;
	}

	/**
	 * 获取token数据，并更新
	 * @return
	 * @throws SQLException
	 */
	public String[] getAccessToken() throws SQLException {

		String[] result = this.getAccessTokenone();
		this.updateAccessToken(result[0]);

		return result;
	}

	/**
	 * 获取token数据
	 * @return
	 * @throws SQLException
	 */
	public String[] getAccessTokenone() throws SQLException {

		String[] result = new String[2];
		String sql = new String(
				"SELECT `access_token`,`token_secret` FROM `member_bind_tencent` ORDER BY `expires` LIMIT 1");
		Statement statement = this.conn.createStatement();
		ResultSet rs = statement.executeQuery(sql);
		if (rs.next()) {
			result[0] = rs.getString("access_token");
			result[1] = rs.getString("token_secret");
		}
		rs.close();
		statement.close();

		return result;
	}

	/**
	 * 更新token
	 * @param token
	 * @throws SQLException
	 */
	public void updateAccessToken(String token) throws SQLException {
		long time = System.currentTimeMillis() / 1000;
		String sql = new String("UPDATE `member_bind_tencent` SET `expires` = " + time + " WHERE `access_token` = \""
				+ token + "\"");
		Statement statement = this.conn.createStatement();
		statement.executeUpdate(sql);
		statement.close();
	}

	/**
	 * 删除token
	 * @param token
	 * @throws SQLException
	 */
	public void deleteAccessToken(String token) throws SQLException {
		String sql = new String("DELETE FROM `member_bind_tencent` WHERE `access_token` = \"" + token + "\"");
		Statement statement = this.conn.createStatement();
		statement.execute(sql);
		statement.close();
	}

	public void insertUsersInfo(String tablename, String username, String nickname, int isvip, int fanscount,
			int friendscount, int exp, int isrealname, int level, int sex) throws SQLException {

		String sql = "INSERT INTO " + tablename + " (`username`,`nickname`,`isvip`,`fanscount`, " + //
				"`friendscount`,`exp`,`isrealname`,`level`,`sex`) VALUES (?,?,?,?,?,?,?,?,?)";
		PreparedStatement pstmt = null;
		try {
			pstmt = this.conn.prepareStatement(sql);
			pstmt.setString(1, username);
			pstmt.setString(2, nickname);
			pstmt.setInt(3, isvip);
			pstmt.setInt(4, fanscount);
			pstmt.setInt(5, friendscount);
			pstmt.setInt(6, exp);
			pstmt.setInt(7, isrealname);
			pstmt.setInt(8, level);
			pstmt.setInt(9, sex);
			pstmt.executeUpdate();
		} finally {
			if (pstmt != null) {
				pstmt.close();
			}
		}
	}

	public void insertUsersInfo(String tablename, String username, String nickname, String description, int isvip,
			int fanscount, int friendscount, int weibocount, int exp, int isrealname, int level, int sex,
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

	/**
	 * 获取新浪用户名
	 * @param tablename
	 * @param num
	 * @return
	 * @throws SQLException
	 */
	public List<String> getTencentUsers(String tablename, int num) throws SQLException {

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
