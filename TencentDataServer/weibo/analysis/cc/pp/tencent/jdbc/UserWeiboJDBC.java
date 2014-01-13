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
 * Title: 用户微博分析的JDBC
 * @author wanggang
 * @version 1.1
 * @since 2013-05-27
 */
public class UserWeiboJDBC implements DataBaseConstant {

	private Connection conn;
	private final String driver = DRIVER;
	private String url = new String();
	private String user = USER;
	private String password = PASSWORD;

	/**
	 * @ construct
	 * @param ip
	 */
	public UserWeiboJDBC(String ip) {
		this.url = "jdbc:mysql://" + ip + ":3306/pp_fenxi?useUnicode=true&characterEncoding=utf-8";
	}

	public UserWeiboJDBC(String ip, String user, String password) {
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
	 * 获取单个token、secret
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

	public List<String> getAllTokens() throws SQLException {

		List<String> result = new ArrayList<String>();
		String sql = new String("SELECT `access_token`,`token_secret` FROM `member_bind_tencent`");
		Statement statement = this.conn.createStatement();
		ResultSet rs = statement.executeQuery(sql);
		if (rs.next()) {
			result.add(rs.getString("access_token") + "," + rs.getString("token_secret"));
		}
		rs.close();
		statement.close();

		return result;
	}

	/**
	 * 更新token信息
	 * @param token
	 * @throws SQLException
	 */
	public void updateAccessToken(String token) throws SQLException {
		long time = System.currentTimeMillis()/1000;
		String sql = new String("UPDATE `member_bind_tencent` SET `expires` = " + time + " WHERE `access_token` = \""
				+ token + "\"");
		Statement statement = this.conn.createStatement();
		statement.executeUpdate(sql);
		statement.close();
	}

	/**
	 * 删除token信息
	 * @param token
	 * @throws SQLException
	 */
	public void deleteAccessToken(String token) throws SQLException {
		String sql = new String("DELETE FROM `member_bind_tencent` WHERE `access_token` = \"" + token + "\"");
		Statement statement = this.conn.createStatement();
		statement.execute(sql);
		statement.close();
	}

	/**
	 * 插入用户全量微博数据
	 * @throws SQLException
	 */
	public void insertUserWeibosInfo(String tablename, String name, String nick, String openid, String id, String text,
			String origtext, int count, int mcount, String from, String fromurl, String province_code,
			String city_code, int emotiontype, String geo, int isrealname, int isvip, String location, String jing,
			int self, boolean isoriginal, int status, int type, String wei, long timestamp) throws SQLException {

		long time = System.currentTimeMillis() / 1000;
		String sql = "INSERT INTO " + tablename + " (`name`,`nick`,`openid`,`wid`,`text`,`origtext`,"
				+ "`count`,`mcount`,`from`,`fromurl`,`province_code`,`city_code`,`emotiontype`,"
				+ "`geo`,`isrealname`,`isvip`,`location`,`jing`,`self`,`isoriginal`,`status`,`type`,"
				+ "`wei`,`timestamp`,`lasttime`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		PreparedStatement pstmt = null;
		try {
			pstmt = this.conn.prepareStatement(sql);
			pstmt.setString(1, name);
			pstmt.setString(2, nick);
			pstmt.setString(3, openid);
			pstmt.setString(4, id);
			pstmt.setString(5, text);
			pstmt.setString(6, origtext);
			pstmt.setInt(7, count);
			pstmt.setInt(8, mcount);
			pstmt.setString(9, from);
			pstmt.setString(10, fromurl);
			pstmt.setString(11, province_code);
			pstmt.setString(12, city_code);
			pstmt.setInt(13, emotiontype);
			pstmt.setString(14, geo);
			pstmt.setInt(15, isrealname);
			pstmt.setInt(16, isvip);
			pstmt.setString(17, location);
			pstmt.setString(18, jing);
			pstmt.setInt(19, self);
			pstmt.setBoolean(20, isoriginal);
			pstmt.setInt(21, status);
			pstmt.setInt(22, type);
			pstmt.setString(23, wei);
			pstmt.setLong(24, timestamp);
			pstmt.setLong(25, time);
			pstmt.execute();
		} finally {
			if (pstmt != null) {
				pstmt.close();
			}
		}
	}

	public void createUserWeiboTable(String tablename) throws SQLException {

		String sql = new String("CREATE TABLE "
				+ tablename //
						+ " (`id` int(10) NOT NULL AUTO_INCREMENT,`name` char(50) NOT NULL,"
						+ "`nick` char(50) NOT NULL,`openid` char(50) NOT NULL,"
						+ "`wid` char(30) NOT NULL,`text` varchar(400) NOT NULL,"
						+ "`origtext` varchar(400) NOT NULL,`count` int(8) NOT NULL,"
						+ "`mcount` int(8) NOT NULL,`from` char(50) NOT NULL,"
						+ "`fromurl` char(250) NOT NULL,`province_code` char(5) NOT NULL,"
						+ "`city_code` char(5) NOT NULL,`emotiontype` int(2) NOT NULL,"
						+ "`geo` char(150) NOT NULL,`isrealname` int(2) NOT NULL,"
						+ "`isvip` int(2) NOT NULL,`location` char(250) NOT NULL,"
						+ "`jing` char(250) NOT NULL,`self` int(2) NOT NULL,"
						+ "`isoriginal` tinyint(1) NOT NULL,`status` int(2) NOT NULL,"
						+ "`type` int(2) NOT NULL,`wei` char(50) NOT NULL,"
						+ "`timestamp` int(10) NOT NULL,`lasttime` int(10) NOT NULL,"
						+ "PRIMARY KEY (`id`)) ENGINE=MyISAM DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;");
		PreparedStatement pstmt = null;
		try {
			pstmt = this.conn.prepareStatement(sql);
			pstmt.execute();
		} finally {
			if (pstmt != null) {
				pstmt.close();
			}
		}
	}

	/**
	 * 获取腾讯用户名
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

	public HashMap<String, String> getTencentUsers(String tablename) throws SQLException {

		HashMap<String, String> result = new HashMap<String, String>();
		String sql = "SELECT `username` FROM " + tablename;
		PreparedStatement pstmt = this.conn.prepareStatement(sql);
		ResultSet rs = pstmt.executeQuery();
		while (rs.next()) {
			result.put(rs.getString(1), "ok");
		}
		rs.close();
		pstmt.close();

		return result;
	}

	public void deleteBatch(String tablename, List<String> tencentusers) throws SQLException {

		String sql = "DELETE FROM " + tablename + " WHERE `username` = ?";
		PreparedStatement pstmt = null;
		try {
			pstmt = this.conn.prepareStatement(sql);
			for (String user : tencentusers) {
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

	public void insertUnusedUid(String tablename, String username) throws SQLException {

		String sql = new String("INSERT INTO " + tablename + " (`username`) VALUES (\"" + username + "\")");
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
