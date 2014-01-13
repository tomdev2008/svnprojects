package cc.pp.tencent.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import cc.pp.tencent.constant.DataBaseConstant;

/**
 * Title: 用户微博分析的JDBC
 * @author wanggang
 * @version 1.1
 * @since 2013-05-27
 */
public class JDBC implements DataBaseConstant {
	
	public Connection conn;
	private final String driver = DRIVER;
	private String url = new String();
	private String user = USER;
	private String password = PASSWORD;

	/**
	 * @ construct
	 * @param ip
	 */
	public JDBC(String ip) {
		this.url = "jdbc:mysql://" + ip + ":3306/pp_fenxi?useUnicode=true&characterEncoding=utf-8";
	}
	
	public JDBC(String ip, String user, String password) {
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
     * @ 关闭数据库
     * @throws SQLException
     */
	public void sqlClose() throws SQLException {
		this.conn.close();
	}

}

