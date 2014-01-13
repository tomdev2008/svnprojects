package cc.pp.sina.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

import cc.pp.sina.constant.DataBaseConstant;

/**
 * Title: 单条微博分析的JDBC
 * @author wanggang
 * @version 1.1
 * @since 2013-05-27
 */
public class WeiboJDBC implements DataBaseConstant {

	private Connection conn;
	private final String driver = DRIVER;
	private String url = new String();
	private String user = USER;
	private String password = PASSWORD;
	
	/**
	 * @ construct
	 * @param ip
	 */
	public WeiboJDBC(String ip) {
		this.url = "jdbc:mysql://" + ip + ":3306/pp_fenxi?useUnicode=true&characterEncoding=utf-8";
	}

	public WeiboJDBC(String ip, String user, String password) {
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
	
	public void deleteToken() throws SQLException {

		long time = System.currentTimeMillis() / 1000;
		String sql = new String("DELETE FROM `member_bind_sina` WHERE `expires_in` < " + time);
		Statement statement = this.conn.createStatement();
		statement.execute(sql);
		statement.close();
	}

	/**
	 * 插入待分析微博列表
	 * @param tablename
	 * @param uid
	 * @param url
	 * @throws SQLException
	 */
	public void insertWbLists(String tablename, String uid, String url) throws SQLException {

		String sql = "INSERT INTO " + tablename + " (`uid`,`url`) VALUES (?,?)";
		PreparedStatement pstmt = null;
		try {
			pstmt = this.conn.prepareStatement(sql);
			pstmt.setString(1, uid);
			pstmt.setString(2, url);
			pstmt.executeUpdate();
		} finally {
			if (pstmt != null) {
				pstmt.close();
			}
		}
	}
	
	/**
	 * 获取数据库中的待分析微博列表
	 * @param tablename
	 * @return
	 * @throws SQLException 
	 */
	public HashMap<Integer,String> getWbListsByDB(String tablename) throws SQLException {
		
		HashMap<Integer,String> wblists = new HashMap<Integer,String>();
		String sql = "SELECT `uid`,`url` FROM " + tablename; 
		Statement statement = this.conn.createStatement();
		ResultSet rs = statement.executeQuery(sql);
		int i = 0;
		while (rs.next()) {
			wblists.put(i++, rs.getString("uid") + "=" + rs.getString("url"));
		}
		rs.close();
		statement.close();
		
		return wblists;
	}
	
	/**
	 * 插入问题微博
	 * @param type
	 * @param uid
	 * @param url
	 * @throws SQLException
	 */
	public void insertIssueWbs(String type, String uid, String url) throws SQLException {
		
		String sql = "INSERT INTO `issueweibolists` (`type`,`uid`,`url`) VALUES (?,?,?)";
		PreparedStatement pstmt = null;
		try {
			pstmt = this.conn.prepareStatement(sql);
			pstmt.setString(1, type);
			pstmt.setString(2, uid);
			pstmt.setString(3, url);
			pstmt.executeUpdate();
		} finally {
			if (pstmt != null) {
				pstmt.close();
			}
		}
	}

	public void truncateTables(String tablename) throws SQLException {
		String sql = new String("TRUNCATE TABLE " + tablename);
		Statement statement = this.conn.createStatement();
		statement.execute(sql);
		statement.close();
	}

	/**
	 * 插入结果数据
	 * @param username
	 * @param wid
	 * @param result
	 * @throws SQLException
	 */
	public void insertResult(String username, String wid, String result) throws SQLException {
		long time = System.currentTimeMillis()/1000;
		result = result.substring(1);
		result = result.substring(0, result.length() - 1);
		String sql = new String("INSERT INTO `SinaWeiboAnalysisResult` (`username`,`wid`,`result`,`lasttime`) VALUES (\"" +
	                 username + "\"," + wid + ",\"" + result.replaceAll("\"", "\\\\\"") + "\"," + time + ")");
		Statement statement = this.conn.createStatement();
		statement.execute(sql);
		statement.close();
	}

	/**
	 * 插入结果数据
	 * @param wid
	 * @param url
	 * @param result
	 * @throws SQLException
	 */
	public void insertWeiboResult(String wid, String url, String result) throws SQLException {
		
		long time = System.currentTimeMillis()/1000;
		result = result.substring(1);
		result = result.substring(0, result.length() - 1);
		String sql = "INSERT INTO `sinaweiboresult` (`wid`,`url`,`weiboresult`,`lasttime`) " //
				+ "VALUES (?, ?, ?, ?)";
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, wid);
			pstmt.setString(2, url);
			pstmt.setString(3, result);
			pstmt.setLong(4, time);
			pstmt.executeUpdate();
		} finally {
			if (pstmt != null) {
				pstmt.close();
			}
		}
	}
	
    /**
     * @ 关闭数据库
     * @throws SQLException
     */
	public void sqlClose() throws SQLException {
		this.conn.close();
	}

}

