package cc.pp.sina.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import cc.pp.sina.constant.DataBaseConstant;

public class UidJDBC implements DataBaseConstant {

	private Connection conn;
	private final String driver = DRIVER;
	private String url = new String();
	private String user = USER;
	private String password = PASSWORD;

	/**
	 * @ construct
	 * @param ip
	 */
	public UidJDBC(String ip) {
		this.url = "jdbc:mysql://" + ip + ":3306/pp_fenxi?useUnicode=true&characterEncoding=utf-8";
	}

	public UidJDBC(String ip, String user, String password) {
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
	 * 获取单个有效token
	 * @return
	 * @throws SQLException
	 */
	public String getAccessToken() throws SQLException {
		String[] result = this.getAccessTokenone();
		long time = System.currentTimeMillis()/1000;
		while (Long.parseLong(result[1]) < time)
		{
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
		String sql = new String("SELECT `oauth_token`,`expires_in` FROM `member_bind_sina` ORDER BY `lasttime` LIMIT 1");
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
	 * 更新token数据
	 * @param token
	 * @throws SQLException
	 */
	public void updateAccessToken(String token) throws SQLException {
		long time = System.currentTimeMillis()/1000;
		String sql = new String("UPDATE `member_bind_sina` SET `lasttime` = " + time + " WHERE `oauth_token` = \"" + token + "\"");
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

	public List<String> getAllTokens() throws SQLException {

		List<String> result = new ArrayList<String>();
		String sql = new String("SELECT `oauth_token` FROM `member_bind_sina`");
		Statement statement = this.conn.createStatement();
		ResultSet rs = statement.executeQuery(sql);
		while (rs.next()) {
			result.add(rs.getString("oauth_token"));
		}
		rs.close();
		statement.close();

		return result;
	}

	/**
	 * 创建表格
	 * @param tablename
	 * @throws SQLException
	 */
	public void createTable(String tablename) throws SQLException {

		String sql = "CREATE TABLE " + tablename + " (`id` int(10) NOT NULL AUTO_INCREMENT,"
				+ "`username` char(10) NOT NULL,PRIMARY KEY (`id`)) "
				+ "ENGINE=MyISAM DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;";
		Statement statement = this.conn.createStatement();
		statement.executeUpdate(sql);
		statement.close();
	}

	public void createUserBaseinfoTable(String tablename) throws SQLException {

		String sql = "CREATE TABLE " + tablename +
				" (`bid` int(10) NOT NULL AUTO_INCREMENT," +
				"`id` char(10) NOT NULL DEFAULT '0' COMMENT '用户UID'," +
				"`screen_name` char(50) NOT NULL DEFAULT '0' COMMENT '微博昵称'," +
				"`name` char(50) NOT NULL DEFAULT '0' COMMENT '友好显示名称'," +
				"`province` int(5) NOT NULL DEFAULT '0' COMMENT '省份编码'," +
				"`city` int(5) NOT NULL DEFAULT '0' COMMENT '城市编码'," +
				"`location` char(50) NOT NULL DEFAULT '0' COMMENT '地址'," +
				"`description` varchar(300) NOT NULL DEFAULT '0' COMMENT '个人描述'," +
				"`url` char(200) NOT NULL DEFAULT '0' COMMENT '用户博客地址'," +
				"`profile_image_url` char(200) NOT NULL DEFAULT '0' COMMENT '自定义头像'," +
				"`domain` char(50) NOT NULL DEFAULT '0' COMMENT '用户个性化URL'," +
				"`gender` char(2) NOT NULL DEFAULT '0' COMMENT '性别,m--男，f--女,n--未知'," +
				"`followers_count` int(8) NOT NULL DEFAULT '0' COMMENT '粉丝数'," +
				"`friends_count` int(8) NOT NULL DEFAULT '0' COMMENT '关注数'," +
				"`statuses_count` int(8) NOT NULL DEFAULT '0' COMMENT '微博数'," +
				"`favourites_count` int(8) NOT NULL DEFAULT '0' COMMENT '收藏数'," +
				"`created_at` int(10) NOT NULL DEFAULT '0' COMMENT '用户创建时间'," +
				"`verified` tinyint(1) NOT NULL DEFAULT '-1' COMMENT '加V与否，是否微博认证用户'," +
				"`verified_type` int(5) NOT NULL DEFAULT '-1' COMMENT '认证类型'," +
				"`avatar_large` char(200) NOT NULL DEFAULT '0' COMMENT '大头像地址'," +
				"`bi_followers_count` int(8) NOT NULL DEFAULT '0' COMMENT '互粉数'," +
				"`remark` char(250) NOT NULL DEFAULT '0' COMMENT '备注信息'," +
				"`verified_reason` varchar(300) NOT NULL DEFAULT '0' COMMENT '认证原因'," +
				"`weihao` char(50) NOT NULL DEFAULT '0' COMMENT '微信号'," +
				"`lasttime` int(10) NOT NULL COMMENT '记录时间'," +
				"PRIMARY KEY (`bid`)" +
				") ENGINE=MyISAM DEFAULT CHARSET=utf8 COMMENT='新浪用户基础信息表（有用信息）' AUTO_INCREMENT=1 ;";
		Statement statement = this.conn.createStatement();
		statement.executeUpdate(sql);
		statement.close();
	}

	/**
	 * 插入表格名
	 * @param tablename
	 * @throws SQLException
	 */
	public void insertTablename(String tablename) throws SQLException {

		String sql = new String("INSERT INTO `tablenames` (`tablename`) VALUES (\"" + tablename + "\")");
		Statement statement = this.conn.createStatement();
		statement.execute(sql);
		statement.close();
	}

	public void insertTablename(String tablename, String name) throws SQLException {

		String sql = new String("INSERT INTO " + tablename + " (`tablename`) VALUES (\"" + name + "\")");
		Statement statement = this.conn.createStatement();
		statement.execute(sql);
		statement.close();
	}

	/**
	 * 获取最新的表格名
	 * @return
	 * @throws SQLException
	 */
	public String getLastTablename() throws SQLException {

		String result = new String();
		String sql = new String("SELECT `tablename` FROM `tablenames` ORDER BY `id` DESC LIMIT 1");
		Statement statement = this.conn.createStatement();
		ResultSet rs = statement.executeQuery(sql);
		if (rs.next()) {
			result = rs.getString("tablename");
			rs.close();
			statement.close();
			return result;
		} else {
			rs.close();
			statement.close();
			return null;
		}
	}

	public String getLastTablename(String tablename) throws SQLException {

		String sql = "SELECT `tablename` FROM " + tablename + " ORDER BY `id` LIMIT 1";
		Statement statement = this.conn.createStatement();
		ResultSet rs = statement.executeQuery(sql);
		String result = null;
		if (rs.next()) {
			result = rs.getString("tablename");

		}
		rs.close();
		statement.close();
		return result;
	}

	public void deleteLastTablename(String tablename, String name) throws SQLException {

		String sql = "DELETE FROM " + tablename + " WHERE `tablename` = \"" + name + "\"";
		Statement statement = this.conn.createStatement();
		statement.executeUpdate(sql);
	}

	/**
	 * 插入用户username
	 * @param tablename
	 * @param uid
	 * @throws SQLException
	 */
	public void inserUids(String tablename, String uid) throws SQLException {

		String sql = "INSERT INTO " + tablename + " (`username`) VALUES (?)";
		PreparedStatement pstmt = null;
		try {
			pstmt = this.conn.prepareStatement(sql);
			pstmt.setString(1, uid);
			pstmt.execute();
		} finally {
			if (pstmt != null) {
				pstmt.close();
			}
		}
	}

	/**
	 * 批量插入用户username
	 * @param tablename
	 * @param uids
	 * @throws SQLException
	 */
	public void insertUidsBatch(String tablename, List<String> uids) throws SQLException {

		String sql = "INSERT INTO " + tablename + " (`username`) VALUES (?)";
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

	/**
	 * 获取一个sina用户username
	 * @param tablename
	 * @return
	 * @throws SQLException
	 */
	public String getSinaUid(String tablename) throws SQLException {

		String result = new String();
		String sql = new String("SELECT `username` FROM " + tablename + " LIMIT 1");
		Statement statement = this.conn.createStatement();
		ResultSet rs = statement.executeQuery(sql);
		if (rs.next()) {
			result = rs.getString("username");
		}
		rs.close();
		statement.close();

		return result;
	}

	public List<String> getSinaUidById(String tablename) throws SQLException {

		List<String> result = new ArrayList<String>();
		String sql = new String("SELECT `id` FROM " + tablename);
		Statement statement = this.conn.createStatement();
		ResultSet rs = statement.executeQuery(sql);
		while (rs.next()) {
			result.add(rs.getString("id"));
		}
		rs.close();
		statement.close();

		return result;
	}

	/**
	 * 插入用户基础信息
	 * @throws SQLException
	 */
	public void inserUserBaseinfo(String tablename, String id, String screen_name, String name, int province, int city,
			String location, String description, String url, String profile_image_url, String domain, String gender,
			int followers_count, int friends_count, int statuses_count, int favourites_count, long created_at,
			boolean verified, int verified_type, String avatar_large, int bi_followers_count, String remark,
			String verified_reason, String weihao) throws SQLException {

		long time = System.currentTimeMillis() / 1000;
		String sql = "INSERT INTO " + tablename + " (`id`,`screen_name`,`name`,`province`,`city`,"
				+ "`location`,`description`,`url`,`profile_image_url`,`domain`,`gender`,`followers_count`,"
				+ "`friends_count`,`statuses_count`,`favourites_count`,`created_at`,`verified`,"
				+ "`verified_type`,`avatar_large`,`bi_followers_count`,`remark`,`verified_reason`,"
				+ "`weihao`,`lasttime`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		PreparedStatement pstmt = null;
		try {
			pstmt = this.conn.prepareStatement(sql);
			pstmt.setString(1, id);
			pstmt.setString(2, screen_name);
			pstmt.setString(3, name);
			pstmt.setInt(4, province);
			pstmt.setInt(5, city);
			pstmt.setString(6, location);
			pstmt.setString(7, description);
			pstmt.setString(8, url);
			pstmt.setString(9, profile_image_url);
			pstmt.setString(10, domain);
			pstmt.setString(11, gender);
			pstmt.setInt(12, followers_count);
			pstmt.setInt(13, friends_count);
			pstmt.setInt(14, statuses_count);
			pstmt.setInt(15, favourites_count);
			pstmt.setLong(16, created_at);
			pstmt.setBoolean(17, verified);
			pstmt.setInt(18, verified_type);
			pstmt.setString(19, avatar_large);
			pstmt.setInt(20, bi_followers_count);
			if (remark == null) {
				pstmt.setString(21, "0");
			} else {
				pstmt.setString(21, remark);
			}
			pstmt.setString(22, verified_reason);
			pstmt.setString(23, weihao);
			pstmt.setLong(24, time);
			pstmt.execute();
		} finally {
			if (pstmt != null) {
				pstmt.close();
			}
		}

	}

	/**
	 * 删除某个用户
	 * @param tablename
	 * @param username
	 * @throws SQLException
	 */
	public void deleteUid(String tablename, String username) throws SQLException {

		String sql = new String("DELETE FROM " + tablename + " WHERE `username` = \"" + username + "\"");
		Statement statement = this.conn.createStatement();
		statement.execute(sql);
		statement.close();
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

	public void insertUnusedUsers(String tablename, String username) throws SQLException {

		String sql = "INSERT INTO " + tablename + " (`username`) " + "VALUES (\"" + username + "\")";
		Statement statement = this.conn.createStatement();
		statement.execute(sql);
		statement.close();
	}

	public void truncateTable(String tablename) throws SQLException {

		String sql = "TRUNCATE TABLE " + tablename;
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
