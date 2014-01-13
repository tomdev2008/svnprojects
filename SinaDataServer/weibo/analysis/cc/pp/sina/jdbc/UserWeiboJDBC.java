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

import cc.pp.sina.constant.DataBaseConstant;

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
	 * 更新token信息
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
	 * 删除无效token信息
	 * @param token
	 * @throws SQLException
	 */
	public void deleteAccessToken(String token) throws SQLException {
		String sql = new String("DELETE FROM `member_bind_sina` WHERE `oauth_token` = \"" + token + "\"");
		Statement statement = this.conn.createStatement();
		statement.execute(sql);
		statement.close();
	}

	/**
	 * 插入结果数据
	 * @param username
	 * @param fanscount
	 * @param result
	 * @throws SQLException
	 */
	public void insertResult(String username, int fanscount, String result) throws SQLException {
		long time = System.currentTimeMillis()/1000;
		result = result.substring(1);
		result = result.substring(0, result.length() - 1);
		String sql = new String("INSERT INTO `UserFansAnalysisResult` (`username`,`fanscount`,`result`,`lasttime`) VALUES (\"" +
 username + "\"," + fanscount + ",\"" + result.replaceAll("\"", "\\\\\"") + "\"," + time + ")");
		Statement statement = this.conn.createStatement();
		statement.execute(sql);
		statement.close();
	}

	/**
	 * 插入用户数据
	 * @param username
	 * @param nickname
	 * @param location
	 * @param headurl
	 * @param sex
	 * @param fanscount
	 * @param weibocount
	 * @param friendscount
	 * @param favouritescount
	 * @throws SQLException
	 */
	public void insertUserInfo(String username, String nickname, String location, 
			String headurl, String sex, int fanscount, int weibocount, int friendscount, int favouritescount) throws SQLException {
		long time = System.currentTimeMillis()/1000;
		String sql = new String("INSERT INTO `userinfo` (`username`,`nickname`,`location`," +
				"`headurl`,`sex`,`fanscount`,`weibocount`,`friendscount`,`favouritescount`,`lasttime`) VALUES (\"" +
				username + "\",\"" + nickname.replaceAll("\"", "\\\\\"") + "\",\"" +
				location + "\",\"" + headurl + "\",\"" + sex + "\"," + fanscount + "," + weibocount + "," + friendscount + "," +
				favouritescount + "," + time + ")");
		Statement statement = this.conn.createStatement();
		statement.execute(sql);
		statement.close();
	}

	/**
	 * 插入转发用户数据
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

	public void deleteUsernames(String tablename, String username) throws SQLException {

		String sql = new String("DELETE FROM " + tablename + " WHERE `username` = \"" + username + "\"");
		Statement statement = this.conn.createStatement();
		statement.execute(sql);
		statement.close();
	}

	public HashMap<Integer,String> getUsernames(String tablename, int num) throws SQLException {

		HashMap<Integer,String> result = new HashMap<Integer,String>();
		String sql = new String("SELECT `username` FROM " + tablename + " LIMIT " + num);
		Statement statement = this.conn.createStatement();
		ResultSet rs = statement.executeQuery(sql);
		int i = 0;
		while (rs.next()) {
			result.put(i++, rs.getString("username"));
		}
		rs.close();
		statement.close();

		return result;
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

	public HashMap<String, String> getSinaUsers(String tablename) throws SQLException {

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

	/**
	 * 插入结果数据
	 * @throws SQLException
	 */
	public void insertUserWbResult(String username, String nickname, int weibosum, float aveweibobyday,
			float aveweibobyweek, float aveweibobymonth, int lastwbtimediff, float favoritedratio,
			float picratio, float oriratio, float aveorirepcom, float avereposted, float avecommented,
			String wbgradebyrep, String wbgradebycom, String reptop10, String comtop10, String wbtimelinebyy,
			String wbtimelinebym, String wbfenbubyhour, String wbsource) throws SQLException {

		long time = System.currentTimeMillis()/1000;
		String sql = new String("INSERT INTO `userweiboresult` (`username`,`nickname`,`weibosum`," +
				"`aveweibobyday`,`aveweibobyweek`,`aveweibobymonth`," + 
				"`lastwbtimediff`,`favoritedratio`,`picratio`,`oriratio`,`aveorirepcom`,`avereposted`," +
				"`avecommented`,`wbgradebyrep`,`wbgradebycom`,`reptop10`,`comtop10`,`wbtimelinebyy`," +
				"`wbtimelinebym`,`wbfenbubyhour`,`wbsource`,`lasttime`) VALUES (\"" + username + "\",\"" +
				nickname + "\"," + weibosum + "," + aveweibobyday + "," + aveweibobyweek + "," + aveweibobymonth
				+ "," + lastwbtimediff + "," + favoritedratio 
				+ "," + picratio + "," + oriratio + "," + aveorirepcom + "," + avereposted + "," +
				avecommented + ",\"" + wbgradebyrep.replaceAll("\"", "\\\\\"") + "\",\"" +
				wbgradebycom.replaceAll("\"", "\\\\\"") + "\",\"" + reptop10.replaceAll("\"", "\\\\\"")
				+ "\",\"" + comtop10.replaceAll("\"", "\\\\\"") + "\",\"" + wbtimelinebyy.replaceAll("\"", "\\\\\"")
				+ "\",\"" + wbtimelinebym.replaceAll("\"", "\\\\\"") + "\",\"" + wbfenbubyhour.replaceAll("\"", "\\\\\"")
				+ "\",\"" + wbsource.replaceAll("\"", "\\\\\"") + "\"," + time + ")");
		Statement statement = this.conn.createStatement();
		statement.execute(sql);
		statement.close();
	}

	/**
	 * 插入用户的微博数据
	 * @throws SQLException
	 */
	public void insertUserWbInfo(String tablename, String wid, String username, String nickname, long createdat,
			String text, String source, int repcount, int comcount, boolean favorited, boolean truncated,
			String originalpic, String visible, boolean isoriginal) throws SQLException {

		long lasttime = System.currentTimeMillis() / 1000;
		String sql = "INSERT INTO " + tablename + " (`wid`,`username`,`nickname`,`createdat`,`text`," + //
				"`source`,`repcount`,`comcount`,`favorited`,`truncated`,`originalpic`,`visible`," + //
				"`isoriginal`,`lasttime`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		PreparedStatement pstmt = null;
		try {
			pstmt = this.conn.prepareStatement(sql);
			pstmt.setString(1, wid);
			pstmt.setString(2, username);
			pstmt.setString(3, nickname);
			pstmt.setLong(4, createdat);
			pstmt.setString(5, text);
			pstmt.setString(6, source);
			pstmt.setInt(7, repcount);
			pstmt.setInt(8, comcount);
			pstmt.setBoolean(9, favorited);
			pstmt.setBoolean(10, truncated);
			pstmt.setString(11, originalpic);
			pstmt.setString(12, visible);
			pstmt.setBoolean(13, isoriginal);
			pstmt.setLong(14, lasttime);
			pstmt.execute();
		} finally {
			if (pstmt != null) {
				pstmt.close();
			}
		}
	}

	public void createSinaUsersTable(String tablename) throws SQLException {

		String sql = new String("CREATE TABLE "
				+ tablename //
				+ " (`id` int(10) NOT NULL AUTO_INCREMENT,"
				+ "`username` char(10) NOT NULL, PRIMARY KEY (`id`),"
				+ "KEY `username` (`username`)) ENGINE=MyISAM DEFAULT " //
				+ "CHARSET=utf8 AUTO_INCREMENT=1;");
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

	public void createUserWeiboTable(String tablename) throws SQLException {
		
		String sql = new String("CREATE TABLE " + tablename //
				+ " (`id` int(10) NOT NULL AUTO_INCREMENT,`wid` char(20) NOT NULL,"
				+ "`username` char(10) NOT NULL,`nickname` char(50) NOT NULL,`createdat`"
				+ " int(10) NOT NULL,`text` varchar(300) NOT NULL,`source` char(50) "
				+ "NOT NULL,`repcount` int(8) NOT NULL,`comcount` int(8) NOT NULL,"
				+ "`favorited` tinyint(1) NOT NULL,`truncated` tinyint(1) NOT NULL,"
				+ "`originalpic` char(250) NOT NULL,`visible` char(30) NOT NULL,"
				+ "`isoriginal` tinyint(1) NOT NULL,`lasttime` int(10) NOT NULL,"
				+ "PRIMARY KEY (`id`)) ENGINE=MyISAM DEFAULT CHARSET=utf8 AUTO_INCREMENT=1;");
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

	/**
	 * 插入情感数据微博
	 */
	public void insertCorpusWeiboText(String tablename, String type, String text) throws SQLException {

		String sql = new String("INSERT INTO " + tablename + " (`type`,`text`) VALUES (?,?)");
		PreparedStatement pstmt = null;
		try {
			pstmt = this.conn.prepareStatement(sql);
			pstmt.setString(1, type);
			pstmt.setString(2, text);
			pstmt.execute();
		} finally {
			if (pstmt != null) {
				pstmt.close();
			}
		}
	}

	public void insertTest(String tablename, String text) throws SQLException {

		String sql = new String("INSERT INTO " + tablename + " (`text`) VALUES (?)");
		PreparedStatement pstmt = null;
		try {
			pstmt = this.conn.prepareStatement(sql);
			pstmt.setString(1, text);
			pstmt.execute();
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
