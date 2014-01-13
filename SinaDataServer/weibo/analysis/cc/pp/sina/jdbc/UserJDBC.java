package cc.pp.sina.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

import cc.pp.sina.constant.DataBaseConstant;

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

	public void updateAccessToken(String token) throws SQLException {
		long time = System.currentTimeMillis()/1000;
		String sql = new String("UPDATE `member_bind_sina` SET `lasttime` = " + time + " WHERE `oauth_token` = \"" + token + "\"");
		Statement statement = this.conn.createStatement();
		statement.executeUpdate(sql);
		statement.close();
	}

	public void deleteAccessToken(String token) throws SQLException {
		String sql = new String("DELETE FROM `member_bind_sina` WHERE `oauth_token` = \"" + token + "\"");
		Statement statement = this.conn.createStatement();
		statement.execute(sql);
		statement.close();
	}

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
	 * 获取新浪未进行粉丝分析的用户
	 * @return
	 * @throws SQLException 
	 */
	public HashMap<String,String> getUnAnalyzedUsers() throws SQLException {

		HashMap<String,String> result = new HashMap<String,String>();
		String sql = new String("SELECT `bid`,`username` FROM `sinausers` WHERE `isfansanalyzed` = 0");
		Statement statement = this.conn.createStatement();
		ResultSet rs = statement.executeQuery(sql);
		while (rs.next()) {
			if ("OK".equals(result.get(rs.getString("username")))) {
				this.deleteSinaUser(rs.getString("bid"));
			} else {
				result.put(rs.getString("username"), "OK");
			}
		}
		rs.close();
		statement.close();		

		return result;
	}
	
	public void deleteSinaUser(String bid) throws SQLException {
		
		String sql = new String("DELETE FROM `sinausers` WHERE `bid` = " + bid);
		Statement statement = this.conn.createStatement();
		statement.execute(sql);
		statement.close();
	}

	public HashMap<Integer,String> getUsers() throws SQLException {

		HashMap<Integer,String> result = new HashMap<Integer,String>();
		String sql = new String("SELECT `username` FROM `sinafansresult`");
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

	public void updateUser(String username) throws SQLException {

		String sql = new String("UPDATE `sinausers` SET `isfansanalyzed` = 1 WHERE `username` = " + username);
		Statement statement = this.conn.createStatement();
		statement.executeUpdate(sql);
		statement.close();
	}
	

	/**
	 * 更新新浪某个用户数据，粉丝分析完以后更新isfansanalyzed
	 * @param token
	 * @throws SQLException
	 */
	public void updateSinaUser(String username) throws SQLException {

		String sql = new String("UPDATE `sinausers` SET `isfansanalyzed` = 1 WHERE `username` = " + username);
		Statement statement = this.conn.createStatement();
		statement.executeUpdate(sql);
		statement.close();
	}

	/**
	 * 获取某个用户的历史粉丝数据
	 * @param username
	 * @return
	 * @throws SQLException
	 */
	public HashMap<String,String> getFansAddInfo(String username) throws SQLException {

		String sql = new String("SELECT `date`,`followerscount` FROM `sinauserinforesult` WHERE `username` = \""
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
	 * 获取最新用户的粉丝数信息
	 * @param username
	 * @return
	 * @throws SQLException
	 */
	public String getLastFansCount(String username) throws SQLException {

		String sql = new String("SELECT `followerscount` FROM `sinauserinforesult` WHERE `username` = " + username
				+ " ORDER BY `lasttime` DESC LIMIT 1");
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

	/**
	 * 插入结果数据
	 * @param username
	 * @param result
	 * @throws SQLException
	 */
	public void insertFansResult(String username, String result) throws SQLException {

		long time = System.currentTimeMillis()/1000;
		String sql = new String("INSERT INTO `sinafansresult` (`username`,`fansresult`,`lasttime`) " +
				"VALUES (" + username + ",\"" + result.replaceAll("\"", "\\\\\"") + "\"," + time + ")");
		Statement statement = this.conn.createStatement();
		statement.execute(sql);
		statement.close();
	}

	/**
	 * 获取结果数据
	 * @param tablename
	 * @return
	 * @throws SQLException
	 */
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

	/**
	 * 更新结果数据
	 * @param username
	 * @param fansresult
	 * @throws SQLException
	 */
	public void updateResult(String username, String fansresult) throws SQLException {

		long time = System.currentTimeMillis() / 1000;
		String sql = new String("UPDATE `sinafansresult` SET `fansresult` = \"" + fansresult.replaceAll("\"", "\\\\\"")
				+ "\",`lasttime` = " + time + " WHERE `username` = " + username);
		Statement statement = this.conn.createStatement();
		statement.executeUpdate(sql);
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
