package cc.pp.tencent.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import cc.pp.tencent.constant.DataBaseConstant;

/**
 * Title: 话题分析的JDBC
 * @author wanggang
 * @version 1.1
 * @since 2013-05-27
 */
public class TopicJDBC implements DataBaseConstant {

	private Connection conn;
	private final String driver = DRIVER;
	private String url = new String();
	private String user = USER;
	private String password = PASSWORD;
	
	/**
	 * @construct
	 * @param ip
	 */
	public TopicJDBC(String ip) {
		this.url = "jdbc:mysql://" + ip + ":3306/pp_fenxi?useUnicode=true&characterEncoding=utf-8";
	}

	public TopicJDBC(String ip, String user, String password) {
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
		String sql = new String("SELECT `access_token`,`token_secret` FROM `member_bind_tencent` ORDER BY `expires` LIMIT " + nums);
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
	 * 更新token对应的时间
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
	 * 删除无效的token
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
		String sql = new String("INSERT INTO `TencentWeiboAnalysisResult` (`username`,`fanscount`,`result`,`lasttime`) VALUES (\"" +
				username + "\"," + fanscount + ",\"" + result.replaceAll("\"", "\\\\\"") + "\"," + time + ")");
		Statement statement = this.conn.createStatement();
		statement.execute(sql);
		statement.close();
	}

	/**
	 * 查询微博信息
	 * @param rewid
	 * @return
	 * @throws SQLException
	 */
	public boolean queryWeiboInfo(String rewid) throws SQLException {
		boolean status = false;
		String sql = new String("SELECT `rewid` FROM `tencentweiboinfo` WHERE `rewid` = " + rewid);
		Statement statement = this.conn.createStatement();
		ResultSet rs = statement.executeQuery(sql);
		if (rs.next()) {
			status = true;
		}
		rs.close();
		statement.close();

		return status;
	}

	/**
	 * 插入微博信息
	 * @param rcount
	 * @param ccount
	 * @param rewid
	 * @param name
	 * @param self
	 * @param reposttime
	 * @param wbtype
	 * @param viptype
	 * @param wbstatus
	 * @param addv
	 * @param emotiontype
	 * @throws SQLException
	 */
	public void insertWeiboInfo(int rcount, int ccount, String rewid, String name, int self,
			long reposttime, int wbtype, int viptype, int wbstatus, int addv, int emotiontype) throws SQLException {
		String sql = new String("INSERT INTO `tencentweiboinfo` (`rcount`,`ccount`,`rewid`,`name`,`self`," +
				"`reposttime`,`wbtype`,`viptype`,`wbstatus`,`addv`,`emotiontype`) VALUES (" + rcount + ","
				+ ccount + "," + rewid + ",\"" + name + "\"," + self + "," + reposttime + "," + wbtype 
				+ "," + viptype + "," + wbstatus + "," + addv + "," + emotiontype + ")");
		//		System.out.println(sql);
		Statement statement = this.conn.createStatement();
		statement.execute(sql);
		statement.close();
	}

	/**
	 * 插入结果信息
	 * @param username
	 * @param wid
	 * @param result
	 * @throws SQLException
	 */
	public void insertResult(String username, String wid, String result) throws SQLException {
		long time = System.currentTimeMillis()/1000;
		result = result.substring(1);
		result = result.substring(0, result.length() - 1);
		String sql = new String("INSERT INTO `TencentWeiboAnalysisResult` (`username`,`wid`,`result`,`lasttime`) VALUES (\"" +
				username + "\"," + wid + ",\"" + result.replaceAll("\"", "\\\\\"") + "\"," + time + ")");
		Statement statement = this.conn.createStatement();
		statement.execute(sql);
		statement.close();
	}
	
	public void insertWbUsersInfo(String username, String nickname, String description, String userurl, int gender,
			int fanscount, int weibocount, int province, int repostcount, int commentcount, String isoriginal,
			int addv, int verifytype, String verify, String createtime) throws SQLException {

		String sql = "INSERT INTO `wbusersinfo` (`username`,`nickname`,`description`,`userurl`,`gender`,"
				+ "`fanscount`,`weibocount`,`province`,`repostcount`,`commentcount`,`isoriginal`,`addv`,"
				+ "`verifytype`,`verify`,`createdtime`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		PreparedStatement pstmt = null;
		try {
			pstmt = this.conn.prepareStatement(sql);
			pstmt.setString(1, username);
			pstmt.setString(2, nickname);
			pstmt.setString(3, description.replaceAll("x", ""));
			pstmt.setString(4, userurl);
			pstmt.setInt(5, gender);
			pstmt.setInt(6, fanscount);
			pstmt.setInt(7, weibocount);
			pstmt.setInt(8, province);
			pstmt.setInt(9, repostcount);
			pstmt.setInt(10, commentcount);
			pstmt.setString(11, isoriginal);
			pstmt.setInt(12, addv);
			pstmt.setInt(13, verifytype);
			pstmt.setString(14, verify);
			pstmt.setString(15, createtime);
			pstmt.executeUpdate();
		} finally {
			if (pstmt != null) {
				pstmt.close();
			}
		}
	}
	
	public void updateWbUsersInfo(String username, int gender, int fanscount, int weibocount)
			throws SQLException {

		String sql = new String("UPDATE `wbusersinfo` SET `gender` = " + gender + ",`fanscount` = " + fanscount
				+ ",`weibocount` = " + weibocount + " WHERE `username` = \"" + username + "\"");
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
