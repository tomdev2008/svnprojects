package cc.pp.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class TokenJDBC implements DatabaseConstant {

	private final ParamsDB db = new ParamsDB();
	private Connection conn;
	private final String driver;
	private final String url;
	private final String user;
	private final String password;
	private final String ip;

	public TokenJDBC() {
		this.ip = db.getProps("token_ip");
		this.url = "jdbc:mysql://" + ip + ":3306/pp_fenxi?useUnicode=true&characterEncoding=utf-8";
		this.driver = db.getProps("db_driver");
		this.user = db.getProps("db_user");
		this.password = db.getProps("db_password");
	}

	public TokenJDBC(String ip, String user, String password) {
		this.ip = ip;
		this.url = "jdbc:mysql://" + ip + ":3306/pp_fenxi?useUnicode=true&characterEncoding=utf-8";
		this.driver = db.getProps("db_driver");
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
	public String getAccessToken(String tablename) throws SQLException {
		String[] result = this.getAccessTokenone(tablename);
		long time = System.currentTimeMillis()/1000;
		while (Long.parseLong(result[1]) < time)
		{
			this.deleteAccessToken(tablename, result[0]);
			result = this.getAccessTokenone(tablename);
		}
		this.updateAccessToken(tablename, result[0]);

		return result[0];
	}

	/**
	 * 获取单个token
	 * @return
	 * @throws SQLException
	 */
	public String[] getAccessTokenone(String tablename) throws SQLException {
		String[] result = new String[2];
		String sql = new String("SELECT `oauth_token`,`expires_in` FROM " + tablename + " ORDER BY `lasttime` LIMIT 1");
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
	public void updateAccessToken(String tablename, String token) throws SQLException {
		long time = System.currentTimeMillis()/1000;
		String sql = new String("UPDATE " + tablename + " SET `lasttime` = " + time + " WHERE `oauth_token` = \""
				+ token + "\"");
		Statement statement = this.conn.createStatement();
		statement.executeUpdate(sql);
		statement.close();
	}

	/**
	 * 删除无效token
	 * @param token
	 * @throws SQLException
	 */
	public void deleteAccessToken(String tablename, String token) throws SQLException {
		String sql = new String("DELETE FROM " + tablename + " WHERE `oauth_token` = \"" + token + "\"");
		Statement statement = this.conn.createStatement();
		statement.execute(sql);
		statement.close();
	}

	/**
	 * 删除表中所有过期数据
	 * @param tablename
	 * @throws SQLException
	 */
	public void deleteExpireTokens(String tablename) throws SQLException {
		long time = System.currentTimeMillis() / 1000;
		String sql = "DELETE FROM " + tablename + " WHERE `expires_in` < " + time;
		Statement statement = this.conn.createStatement();
		statement.execute(sql);
		statement.close();
	}

	/**
	 * 获取所有token
	 * @return
	 * @throws SQLException
	 */
	public List<String> getAllTokens(String type) throws SQLException {
		List<String> result = new ArrayList<String>();
		String sql = new String("SELECT `oauth_token` FROM " + "member_bind_" + type);
		if ("tencent".equalsIgnoreCase(type)) {
			sql = new String("SELECT `access_token`,`token_secret` FROM " + "member_bind_" + type);
		}
		Statement statement = this.conn.createStatement();
		ResultSet rs = statement.executeQuery(sql);
		if ("tencent".equalsIgnoreCase(type)) {
			while (rs.next()) {
				result.add(rs.getString("access_token") + "," + rs.getString("token_secret"));
			}
		} else {
			while (rs.next()) {
				result.add(rs.getString("oauth_token"));
			}
		}
		rs.close();
		statement.close();

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
