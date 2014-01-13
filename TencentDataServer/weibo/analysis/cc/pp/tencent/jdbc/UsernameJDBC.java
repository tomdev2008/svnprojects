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

import net.sf.json.JSONArray;

import com.tencent.weibo.dao.impl.Comp;
import com.tencent.weibo.dao.impl.Edu;
import com.tencent.weibo.dao.impl.Tag;

public class UsernameJDBC {

	private Connection conn;
	private final String driver = "com.mysql.jdbc.Driver";
	private String url = "";
	private String user = "pp_fenxi";
	private String password = "q#tmuYzC@sqB6!ok@sHd";

	public UsernameJDBC(String ip) {
		this.url = "jdbc:mysql://" + ip + ":3306/pp_fenxi?useUnicode=true&characterEncoding=utf-8";
	}

	public UsernameJDBC(String ip, String user, String password) {
		this.user = user;
		this.password = password;
		this.url = "jdbc:mysql://" + ip + ":3306/pp_fenxi?useUnicode=true&characterEncoding=utf-8";
	}

	/**
	 * 创建用户基础信息表
	 * @param tablename
	 * @throws SQLException
	 */
	public void createBaseInfoTable(String tablename) throws SQLException {

		String sql = "CREATE TABLE "
				+ tablename
 + " (`id` int(10) NOT NULL AUTO_INCREMENT,`name` char(50) NOT NULL,"
				+ "`nick` char(50) NOT NULL,`openid` char(40) NOT NULL,"
				+ "`fansnum` int(8) NOT NULL,`idolnum` int(8) NOT NULL,"
				+ "`favnum` int(8) NOT NULL,`tweetnum` int(8) NOT NULL,"
				+ "`birth_day` int(8) NOT NULL,`birth_month` int(8) NOT NULL,"
				+ "`birth_year` int(8) NOT NULL,`city_code` char(10) NOT NULL,"
				+ "`comp` mediumtext NOT NULL,`country_code` char(10) NOT NULL,"
				+ "`edu` mediumtext NOT NULL,`email` char(50) NOT NULL,"
				+ "`exp` int(8) NOT NULL,`head` char(150) NOT NULL,"
				+ "`homecity_code` char(10) NOT NULL,`homecountry_code` char(10) NOT NULL,"
				+ "`homepage` char(150) NOT NULL,`homeprovince_code` char(10) NOT NULL,"
				+ "`hometown_code` char(10) NOT NULL,`https_head` char(150) NOT NULL,"
				+ "`industry_code` int(10) NOT NULL,`introduction` char(250) NOT NULL,"
				+ "`isent` int(2) NOT NULL,`isrealname` int(2) NOT NULL,"
				+ "`isvip` int(2) NOT NULL,`level` int(3) NOT NULL,"
				+ "`location` char(30) NOT NULL,`mutual_fans_num` int(8) NOT NULL,"
				+ "`province_code` char(10) NOT NULL,`regtime` int(10) NOT NULL,"
				+ "`send_private_flag` int(2) NOT NULL,`sex` int(2) NOT NULL,"
				+ "`tag` mediumtext NOT NULL,`verifyinfo` char(250) NOT NULL,"
				+ "`lasttime` int(10) NOT NULL,PRIMARY KEY (`id`)"
				+ ") ENGINE=MyISAM DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;";
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

	/**
	 * 创建用户关注信息表
	 * @param tablename
	 * @throws SQLException
	 */
	public void createFriendsTable(String tablename) throws SQLException {

		String sql = "CREATE TABLE "
				+ tablename
				+ " (`id` int(10) NOT NULL AUTO_INCREMENT,`username` char(50) NOT NULL,"
				+ "`friendscount` int(8) NOT NULL,`frienduids` mediumtext NOT NULL,"
				+ "PRIMARY KEY (`id`)) ENGINE=MyISAM DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;";
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

	public void createTablenames(String tablename) throws SQLException {

		String sql = new String("CREATE TABLE " + tablename + " (" + "`id` int(10) NOT NULL AUTO_INCREMENT,"
				+ "`tablename` char(30) NOT NULL, PRIMARY KEY (`id`)"
				+ ") ENGINE=MyISAM DEFAULT CHARSET=utf8 AUTO_INCREMENT=1;");
		Statement statement = this.conn.createStatement();
		statement.executeUpdate(sql);
		statement.close();
	}

	/**
	 * 创建用户uid信息表
	 * @param tablename
	 * @throws SQLException
	 */
	public void createUidsTable(String tablename) throws SQLException {

		String sql = "CREATE TABLE " + tablename
				+ " (`id` int(10) NOT NULL AUTO_INCREMENT,`username` char(50) NOT NULL,"
				+ "PRIMARY KEY (`id`)) ENGINE=MyISAM DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;";
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

	/**
	 * 创建简单用户表
	 * @param tablename
	 * @throws SQLException
	 */
	public void createUsersTable(String tablename) throws SQLException {

		String sql = "CREATE TABLE "
				+ tablename
				+ " (`id` int(10) NOT NULL AUTO_INCREMENT,`username` char(50) NOT NULL,"
				+ "PRIMARY KEY (`id`)) ENGINE=MyISAM DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;";
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

	/**
	 * 批量删除用户名
	 * @param tablename
	 * @param sinausers
	 * @throws SQLException
	 */
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

	public String getLastTablename(String tablename) throws SQLException {

		String result = null;
		String sql = new String("SELECT `tablename` FROM " + tablename + " ORDER BY `id` DESC LIMIT 1");
		Statement statement = this.conn.createStatement();
		ResultSet rs = statement.executeQuery(sql);
		if (rs.next()) {
			result = rs.getString("tablename");
		}
		rs.close();
		statement.close();

		return result;
	}

	public int getMaxId(String tablename) throws SQLException {

		int max = 0;
		String sql = new String("SELECT MAX(`id`) `id` FROM " + tablename);
		Statement statement = this.conn.createStatement();
		ResultSet rs = statement.executeQuery(sql);
		if (rs.next()) {
			max = rs.getInt("id");
		}
		rs.close();
		statement.close();

		return max;
	}

	public HashMap<String, String> getTencentUsers(String tablename) throws SQLException {

		HashMap<String, String> result = new HashMap<String, String>();
		String sql = "SELECT `username` FROM " + tablename;
		PreparedStatement pstmt = this.conn.prepareStatement(sql);
		ResultSet rs = pstmt.executeQuery();
		while (rs.next()) {
			result.put(rs.getString(1), "1");
		}
		rs.close();
		pstmt.close();

		return result;
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

	public List<String> getUids(String tablename) throws SQLException {

		List<String> result = new ArrayList<String>();
		String sql = "SELECT `username` FROM " + tablename;
		PreparedStatement pstmt = null;
		try {
			pstmt = this.conn.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				result.add(rs.getString(1));
			}
			rs.close();
		} finally {
			if (pstmt != null) {
				pstmt.close();
			}
		}

		return result;
	}

	public List<String> getUidTTablenames(String tablename) throws SQLException {

		List<String> result = new ArrayList<String>();
		String sql = "SELECT `tablename` FROM " + tablename;
		PreparedStatement pstmt = null;
		try {
			pstmt = this.conn.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				result.add(rs.getString(1));
			}
			rs.close();
		} finally {
			if (pstmt != null) {
				pstmt.close();
			}
		}

		return result;
	}

	/**
	 * 插入用户的关注信息
	 * @param tablename
	 * @param username
	 * @param uids
	 * @throws SQLException
	 */
	public void insertFriendUids(String tablename, String username, List<String> uids) throws SQLException {

		String sql = "INSERT INTO " + tablename + " (`username`,`friendscount`,`frienduids`) VALUES (?,?,?)";
		PreparedStatement pstmt = null;
		try {
			pstmt = this.conn.prepareStatement(sql);
			String uidstr = "";
			for (String uid : uids) {
				uidstr = uidstr + uid + ",";
			}
			if (uidstr.length() > 1) {
				uidstr = uidstr.substring(0, uidstr.length() - 1);
			}
			pstmt.setString(1, username);
			pstmt.setInt(2, uids.size());
			pstmt.setString(3, uidstr);
			pstmt.execute();
		} finally {
			if (pstmt != null) {
				pstmt.close();
			}
		}
	}

	public void insertTablename(String tablename) throws SQLException {

		String sql = new String("INSERT INTO `tencenttablenames` (`tablename`) VALUES (\"" + tablename + "\")");
		Statement statement = this.conn.createStatement();
		statement.execute(sql);
		statement.close();
	}

	/**
	 * 批量插入用户名
	 * @param tablename
	 * @param uids
	 * @throws SQLException
	 */
	public void insertUidsBatch(String tablename, List<String> uids) throws SQLException {

		this.conn.setAutoCommit(false);
		String sql = new String("INSERT INTO " + tablename + " (`username`) VALUES (?)");
		PreparedStatement ps = this.conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_SENSITIVE,
				ResultSet.CONCUR_READ_ONLY);
		for (String uid : uids) {
			ps.setString(1, uid);
			ps.addBatch();
		}
		ps.executeBatch();
		this.conn.commit();
	}

	public void insertUnusedUsers(String tablename, String username) throws SQLException {

		String sql = new String("INSERT INTO " + tablename + " (`username`) " + "VALUES (\"" + username + "\")");
		Statement statement = this.conn.createStatement();
		statement.execute(sql);
		statement.close();
	}

	/**
	 * 插入腾讯用户的基础信息
	 * @param tablename
	 * @throws SQLException
	 */
	public void inserUserBaseInfo(String tablename, String name, String nick, String openid, //
			int fansnum, int favnum, int idolnum, int tweetnum, int birth_day, int birth_month, //
			int birth_year, String city_code, List<Comp> comp, String country_code, List<Edu> edu, //
			String email, int exp, String head, String homecity_code, String homecountry_code, //
			String homepage, String homeprovince_code, String hometown_code, String https_head, //
			int industry_code, String introduction, int isent, int isrealname, int isvip, //
			int level, String location, int mutual_fans_num, String province_code, long regtime, //
			int send_private_flag, int sex, List<Tag> tag, String verifyinfo) throws SQLException {

		long time = System.currentTimeMillis() / 1000;
		String sql = "INSERT INTO " + tablename + " (`name`,`nick`,`openid`,`fansnum`,`favnum`," + //
				"`idolnum`,`tweetnum`,`birth_day`,`birth_month`,`birth_year`,`city_code`,`comp`," + //
				"`country_code`,`edu`,`email`,`exp`,`head`,`homecity_code`,`homecountry_code`," + //
				"`homepage`,`homeprovince_code`,`hometown_code`,`https_head`,`industry_code`," + //
				"`introduction`,`isent`,`isrealname`,`isvip`,`level`,`location`,`mutual_fans_num`," + //
				"`province_code`,`regtime`,`send_private_flag`,`sex`,`tag`,`verifyinfo`,`lasttime`) " + //
				"VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		PreparedStatement pstmt = null;
		try {
			pstmt = this.conn.prepareStatement(sql);
			pstmt.setString(1, name);
			pstmt.setString(2, nick);
			pstmt.setString(3, openid);
			pstmt.setInt(4, fansnum);
			pstmt.setInt(5, favnum);
			pstmt.setInt(6, idolnum);
			pstmt.setInt(7, tweetnum);
			pstmt.setInt(8, birth_day);
			pstmt.setInt(9, birth_month);
			pstmt.setInt(10, birth_year);
			pstmt.setString(11, city_code);
			pstmt.setString(12, JSONArray.fromObject(comp).toString());
			pstmt.setString(13, country_code);
			pstmt.setString(14, JSONArray.fromObject(edu).toString());
			pstmt.setString(15, email);
			pstmt.setInt(16, exp);
			pstmt.setString(17, head);
			pstmt.setString(18, homecity_code);
			pstmt.setString(19, homecountry_code);
			pstmt.setString(20, homepage);
			pstmt.setString(21, homeprovince_code);
			pstmt.setString(22, hometown_code);
			pstmt.setString(23, https_head);
			pstmt.setInt(24, industry_code);
			pstmt.setString(25, introduction);
			pstmt.setInt(26, isent);
			pstmt.setInt(27, isrealname);
			pstmt.setInt(28, isvip);
			pstmt.setInt(29, level);
			pstmt.setString(30, location);
			pstmt.setInt(31, mutual_fans_num);
			pstmt.setString(32, province_code);
			pstmt.setLong(33, regtime);
			pstmt.setInt(34, send_private_flag);
			pstmt.setInt(35, sex);
			pstmt.setString(36, JSONArray.fromObject(tag).toString());
			pstmt.setString(37, verifyinfo);
			pstmt.setLong(38, time);
			pstmt.execute();
		} finally {
			if (pstmt != null) {
				pstmt.close();
			}
		}
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
	 * @ 关闭数据库
	 * @throws SQLException
	 */
	public void sqlClose() throws SQLException {
		this.conn.close();
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


}
