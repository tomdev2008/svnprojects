package cc.pp.tencent.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cc.pp.tencent.constant.DataBaseConstant;

/**
 * Title: 用户分析的JDBC
 * @author wanggang
 * @version 1.1
 * @since 2013-05-27
 */
public class UserJDBC implements DataBaseConstant {

	private Connection conn;
	private final String driver = DRIVER;
	private String url = new String();
	private String user = USER;
	private String password = PASSWORD;

	/**
	 * @ construct
	 * @param ip
	 */
	public UserJDBC(String ip) {
		this.url = "jdbc:mysql://" + ip + ":3306/pp_fenxi?useUnicode=true&characterEncoding=utf-8";
	}

	public UserJDBC(String ip, String user, String password) {
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
	 * 获取多个token、secret，并更新
	 * @return
	 * @throws SQLException
	 */
	public String[] getAccessToken() throws SQLException {

		String[] result = this.getAccessTokenone();
		this.updateAccessToken(result[0]);

		return result;
	}

	/**
	 * 获取多个token、secret
	 * @return
	 * @throws SQLException
	 */
	public String[] getAccessTokenone() throws SQLException {

		String[] result = new String[2];
		String sql = new String("SELECT `access_token`,`token_secret` FROM `member_bind_tencent` ORDER BY `expires` LIMIT 1");
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
	 * 更新token数据
	 * @param token
	 * @throws SQLException
	 */
	public void updateAccessToken(String token) throws SQLException {

		long time = System.currentTimeMillis()/1000;
		String sql = new String("UPDATE `member_bind_tencent` SET `expires` = " + time + " WHERE `access_token` = \"" + token + "\"");
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

		String sql = new String("DELETE FROM `member_bind_tencent` WHERE `access_token` = \"" + token + "\"");
		Statement statement = this.conn.createStatement();
		statement.execute(sql);
		statement.close();
	}

	public List<String> getAllTokens() throws SQLException {

		List<String> result = new ArrayList<String>();
		String sql = new String("SELECT `access_token`,`token_secret` FROM `member_bind_tencent`");
		Statement statement = this.conn.createStatement();
		ResultSet rs = statement.executeQuery(sql);
		while (rs.next()) {
			result.add(rs.getString("access_token") + "," + rs.getString("token_secret"));
		}
		rs.close();
		statement.close();

		return result;
	}

	/**
	 * 插入用户数据
	 * @param username
	 * @param nickname
	 * @param headurl
	 * @param sex
	 * @param city
	 * @param fansnum
	 * @param friendsnum
	 * @param isvip
	 * @param addv
	 * @throws SQLException
	 */
	public void insertUserinfo(String username, String nickname, String headurl, int sex, 
			int city, int fansnum, int friendsnum, int isvip, int addv) throws SQLException {

		long time = System.currentTimeMillis()/1000;
		String sql = new String("INSERT INTO `tencentuser` (`username`,`nickname`,`head`,`gender`,`province`," +
				"`fanscount`,`friendscount`,`isvip`,`addv`,`lasttime`) VALUES (\"" + username + "\",\""
				+ nickname + "\",\"" + headurl + "\"," + sex + "," + city + "," + fansnum + ","
				+ friendsnum + "," + isvip + "," + addv + "," + time + ")");
		//		System.out.println(sql);
		Statement statement = this.conn.createStatement();
		statement.execute(sql);
		statement.close();
	}

	/**
	 * 判断用户是否存在用户列表里面
	 * @param username
	 * @return
	 * @throws SQLException
	 */
	public boolean isUserExist(String username) throws SQLException {

		boolean status = false;
		String sql = new String("SELECT `username` FROM `tencentuser` WHERE `username` = \"" + username + "\"");
		Statement statement = this.conn.createStatement();
		ResultSet rs = statement.executeQuery(sql);
		if (rs.next()) {
			status = true;
		}
		statement.close();
		rs.close();

		return status;
	}

	/**
	 * 插入分析结果
	 * @param username
	 * @param fanscount
	 * @param result
	 * @throws SQLException
	 */
	public void insertResult(String username, int fanscount, String result) throws SQLException {

		long time = System.currentTimeMillis()/1000;
		result = result.substring(1);
		result = result.substring(0, result.length() - 1);
		String sql = new String("INSERT INTO `TencentUserFansAnalysisResult` (`username`,`fanscount`,`result`,`lasttime`) VALUES (\"" +
				username + "\"," + fanscount + ",\"" + result.replaceAll("\"", "\\\\\"") + "\"," + time + ")");
		Statement statement = this.conn.createStatement();
		statement.execute(sql);
		statement.close();
	}

	/**
	 * 插入转发用户信息
	 * @param username
	 * @param nickname
	 * @param description
	 * @param location
	 * @param headurl
	 * @param sex
	 * @param fanscount
	 * @param weibocount
	 * @param friendscount
	 * @param favouritescount
	 * @throws SQLException
	 */
	public void insertRepostersInfo(String username, String nickname, String description, String location, 
			String headurl, String sex, int fanscount, int weibocount, int friendscount, int favouritescount) throws SQLException {

		long time = System.currentTimeMillis()/1000;
		String sql = new String("INSERT INTO `repostersinfo` (`username`,`nickname`,`description`,`location`," +
				"`headurl`,`sex`,`fanscount`,`weibocount`,`friendscount`,`favouritescount`,`lasttime`) VALUES (\"" +
				username + "\",\"" + nickname.replaceAll("\"", "\\\\\"") + "\",\"" + description.replaceAll("\"", "\\\\\"") + "\",\"" +
				location + "\",\"" + headurl + "\",\"" + sex + "\"," + fanscount + "," + weibocount + "," + friendscount + "," +
				favouritescount + "," + time + ")");
		Statement statement = this.conn.createStatement();
		statement.execute(sql);
		statement.close();
	}

	/**
	 * 获取某个用户的历史粉丝数据
	 * @param username
	 * @return
	 * @throws SQLException
	 */
	public HashMap<String, String> getFansAddInfo(String username) throws SQLException {

		String sql = new String("SELECT `date`,`followerscount` FROM `tencentuserinforesult` WHERE `username` = \""
				+ username + "\"");
		Statement statement = this.conn.createStatement();
		ResultSet rs = statement.executeQuery(sql);
		HashMap<String, String> data = new HashMap<String, String>();
		while (rs.next()) {
			data.put(rs.getString("date"), rs.getString("followerscount"));
		}
		rs.close();
		statement.close();

		return data;
	}

	/**
	 * 插入结果数据
	 * @param username
	 * @param result
	 * @throws SQLException
	 */
	public void insertFansResult(String username, String result) throws SQLException {

		long time = System.currentTimeMillis() / 1000;
		String sql = new String("INSERT INTO `tencentfansresult` (`username`,`fansresult`,`lasttime`) " + "VALUES (\""
				+ username + "\",\"" + result.replaceAll("\"", "\\\\\"") + "\"," + time + ")");
		Statement statement = this.conn.createStatement();
		statement.execute(sql);
		statement.close();
	}

	public HashMap<String, String> getResult(String tablename) throws SQLException {

		String sql = new String("SELECT `username`,`fansresult` FROM " + tablename);
		Statement statement = this.conn.createStatement();
		ResultSet rs = statement.executeQuery(sql);
		HashMap<String, String> result = new HashMap<String, String>();
		while (rs.next()) {
			result.put(rs.getString("username"), rs.getString("fansresult"));
		}
		rs.close();
		statement.close();

		return result;
	}

	public String getLastFansCount(String username) throws SQLException {

		String sql = new String("SELECT `followerscount` FROM `tencentuserinforesult` WHERE `username` = \"" + username
				+ "\" ORDER BY `lasttime` DESC LIMIT 1");
		Statement statement = this.conn.createStatement();
		ResultSet rs = statement.executeQuery(sql);
		String result;
		if (rs.next()) {
			result = rs.getString("followerscount");
		} else {
			result = null;
		}
		rs.close();
		statement.close();

		return result;
	}

	public void updateResult(String username, String fansresult) throws SQLException {

		long time = System.currentTimeMillis() / 1000;
		String sql = new String("UPDATE `tencentfansresult` SET `fansresult` = \""
				+ fansresult.replaceAll("\"", "\\\\\"")
				+ "\",`lasttime` = " + time + " WHERE `username` = \"" + username + "\"");
		Statement statement = this.conn.createStatement();
		statement.executeUpdate(sql);
		statement.close();
	}

	/**
	 * 获取腾讯未进行粉丝分析的用户
	 * @return
	 * @throws SQLException 
	 */
	public HashMap<String, String> getUnAnalyzedUsers() throws SQLException {

		HashMap<String, String> result = new HashMap<String, String>();
		String sql = new String("SELECT `bid`,`username` FROM `tencentusers` WHERE `isfansanalyzed` = 0");
		Statement statement = this.conn.createStatement();
		ResultSet rs = statement.executeQuery(sql);
		while (rs.next()) {
			if ("OK".equals(result.get(rs.getString("username")))) {
				this.deleteTencentUser(rs.getString("bid"));
			} else {
				result.put(rs.getString("username"), "OK");
			}
		}
		rs.close();
		statement.close();

		return result;
	}

	public void deleteTencentUser(String bid) throws SQLException {

		String sql = new String("DELETE FROM `tencentusers` WHERE `bid` = " + bid);
		Statement statement = this.conn.createStatement();
		statement.execute(sql);
		statement.close();
	}

	/**
	 * 更新腾讯某个用户数据，粉丝分析完以后更新isfansanalyzed
	 * @param token
	 * @throws SQLException
	 */
	public void updateTencentUser(String username) throws SQLException {

		String sql = new String("UPDATE `tencentusers` SET `isfansanalyzed` = 1 WHERE `username` = \"" + username
				+ "\"");
		Statement statement = this.conn.createStatement();
		statement.executeUpdate(sql);
		statement.close();
	}

	public void insertFansResults(String tablename, String username, String fanssum, String activefanssum,
			String source, String verifiedtype, String hottags, String gradebyfans, String age, String gender,
			String location, String fansQuality, String top40byfans, String addVRatio, String activetimeline,
			String fansaddtimeline) throws SQLException {

		long time = System.currentTimeMillis() / 1000;
		String sql = "INSERT INTO " + tablename + " (`username`,`fanssum`,`activefanssum`,`source`," + //
				"`verifiedtype`,`hottags`,`gradebyfans`,`age`,`gender`,`location`,`fansQuality`," + //
				"`top40byfans`,`addVRatio`,`activetimeline`,`fansaddtimeline`,`lasttime`) " + //
				"VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		PreparedStatement pstmt = null;
		try {
			pstmt = this.conn.prepareStatement(sql);
			pstmt.setString(1, username);
			pstmt.setString(2, fanssum);
			pstmt.setString(3, activefanssum);
			pstmt.setString(4, source);
			pstmt.setString(5, verifiedtype);
			pstmt.setString(6, hottags);
			pstmt.setString(7, gradebyfans);
			pstmt.setString(8, age);
			pstmt.setString(9, gender);
			pstmt.setString(10, location);
			pstmt.setString(11, fansQuality);
			pstmt.setString(12, top40byfans);
			pstmt.setString(13, addVRatio);
			pstmt.setString(14, activetimeline);
			pstmt.setString(15, fansaddtimeline);
			pstmt.setLong(16, time);
			pstmt.execute();
		} finally {
			if (pstmt != null) {
				pstmt.close();
			}
		}
	}

	public void insertFansResults(String tablename, String username, String fanssum, String activefanssum,
			String verifiedtype, String hottags, String gender) throws SQLException {

		String sql = "INSERT INTO " + tablename + " (`username`,`fanssum`,`activefanssum`," + //
				"`verifiedtype`,`hottags`,`gender`) VALUES (?,?,?,?,?,?)";
		PreparedStatement pstmt = null;
		try {
			pstmt = this.conn.prepareStatement(sql);
			pstmt.setString(1, username);
			pstmt.setString(2, fanssum);
			pstmt.setString(3, activefanssum);
			pstmt.setString(4, verifiedtype);
			pstmt.setString(5, hottags);
			pstmt.setString(6, gender);
			pstmt.execute();
		} finally {
			if (pstmt != null) {
				pstmt.close();
			}
		}
	}

	public HashMap<String, String> getTencetUsers(String tablename) throws SQLException {

		HashMap<String, String> result = new HashMap<String, String>();
		String sql = "SELECT `username` FROM " + tablename;
		PreparedStatement pstmt = null;
		try {
			pstmt = this.conn.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				result.put(rs.getString(1), "ok");
			}
		} finally {
			if (pstmt != null) {
				pstmt.close();
			}
		}
		return result;
	}

	public HashMap<String, HashMap<String, String>> getUserResult(String tablename) throws SQLException {

		HashMap<String, HashMap<String, String>> result = new HashMap<String, HashMap<String, String>>();
		String sql = "SELECT `username`,`fanssum`,`activefanssum`,`verifiedtype`,`hottags`,`gender` FROM " + tablename;
		PreparedStatement pstmt = null;
		try {
			pstmt = this.conn.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				HashMap<String, String> temp = new HashMap<String, String>();
				temp.put("fanssum", rs.getString(2));
				temp.put("activefanssum", rs.getString(3));
				temp.put("verifiedtype", rs.getString(4));
				temp.put("hottags", rs.getString(5));
				temp.put("gender", rs.getString(6));
				result.put(rs.getString(1), temp);
			}
		} finally {
			if (pstmt != null) {
				pstmt.close();
			}
		}
		return result;
	}

	/**
	 * @ 关闭数据库
	 * @throws SQLException
	 */
	public void sqlClose() throws SQLException {
		this.conn.close();
	}

}
