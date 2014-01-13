package cc.pp.sina.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import cc.pp.sina.constant.DataBaseConstant;

/**
 * Title: 用户与粉丝互动数据分析的JDBC
 * @author wanggang
 * @version 1.1
 * @since 2013-05-27
 */
public class UfiJDBC implements DataBaseConstant {

	private Connection conn;
	private final String driver = DRIVER;
	private String url = new String();
	private String user = USER;
	private String password = PASSWORD;

	/**
	 * @ construct
	 * @param ip
	 */
	public UfiJDBC(String ip) {
		this.url = "jdbc:mysql://" + ip + ":3306/pp_fenxi?useUnicode=true&characterEncoding=utf-8";
	}

	public UfiJDBC(String ip, String user, String password) {
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
	
	/**
	 * 获取最大bid
	 * @return
	 * @throws SQLException 
	 */
	public String getMaxBid() throws SQLException {
		
		String bid = new String();
		String sql = new String("SELECT MAX(`bid`) `bid` FROM `sinausers`");
		Statement statement = this.conn.createStatement();
		ResultSet rs = statement.executeQuery(sql);
		while (rs.next()) {
			bid = rs.getString("bid");
		}
		rs.close();
		statement.close();
		
		return bid;
	}
	
	/**
	 * 获取t2所有授权用户的信息
	 * @throws SQLException 
	 */
	public HashMap<String,String> getT2Users() throws SQLException {
		
		HashMap<String,String> result = new HashMap<String,String>();
		String sql = new String("SELECT `username`,`accesstoken`,`expires` FROM `sinausers`");
		Statement statement = this.conn.createStatement();
		ResultSet rs = statement.executeQuery(sql);
		String expires = new String();
		long t = 0;
		while (rs.next()) {
			expires = rs.getString("expires");
			t = Long.parseLong(expires);
			if (t < System.currentTimeMillis()/1000) {
				result.put(rs.getString("username"), "no");
			} else {
				result.put(rs.getString("username"), rs.getString("accesstoken"));
			}
		}
		
		return result;
	}
 
	/**
	 * 获取新浪用户
	 * @return
	 * @throws SQLException
	 */
	public HashMap<Integer,String> getSinaUsers() throws SQLException {
		HashMap<Integer,String> result = new HashMap<Integer,String>();
		String sql = new String("SELECT `username` FROM `sinausers`");
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
	 * 插入新浪用户
	 * @param username
	 * @throws SQLException
	 */
	public void insertSinaUsers(String username) throws SQLException {
		String sql = new String("INSERT INTO `sinausers` (`username`) VALUES (\"" + username + "\")");
		Statement statement = this.conn.createStatement();
		statement.execute(sql);
		statement.close();
	}

	/**
	 * 获取新浪基础用户信息
	 * @return
	 * @throws SQLException
	 */
	public HashMap<String,String> getSinaUserInfo() throws SQLException {
		HashMap<String,String> result = new HashMap<String,String>();
		String sql = new String("SELECT * FROM `sinauserinfo`");
		Statement statement = this.conn.createStatement();
		ResultSet rs = statement.executeQuery(sql);
		String info = new String();
		while (rs.next()) {
			info = rs.getString("followerscount") + "," + rs.getString("friendscount") 
					+ "," + rs.getString("statusescount");
			result.put(rs.getString("username"), info);
		}
		rs.close();
		statement.close();

		return result;
	}

	/**
	 * 插入基础用户信息
	 * @param username
	 * @param followerscount
	 * @param friendscount
	 * @param statusescount
	 * @throws SQLException
	 */
	public void insertUserinfo(String username, int followerscount, int friendscount, 
			int statusescount) throws SQLException {
		String sql = new String("INSERT INTO `sinauserinfo`(`username`,`followerscount`,`friendscount`," +
				"`statusescount`) VALUES (\"" + username + "\"," + followerscount + "," + friendscount
				+ "," + statusescount + ")");
		Statement statement = this.conn.createStatement();
		statement.execute(sql);
		statement.close();
	}

	/**
	 * tablename[0]--昨天，tablename[1]--今天
	 * @return
	 * @throws SQLException
	 */
	public String[] getTablename() throws SQLException {
		long time = System.currentTimeMillis()/1000;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String day = sdf.format(new Date(time*1000l));
		day = day.replaceAll("-", "");
		String date = new String();
		if (day.subSequence(6,7).equals("0")) {
			date = day.substring(7);
		} else {
			date = day.substring(6);
		}
		String[] tablename = new String[2];
		String table = this.selectTablename();
		if (table == null) {
			tablename[0] = "sinauserinforesult" + day;
		} else {
			tablename[0] = table;
		}
		if (Integer.parseInt(date) % 5 == 0) {
			tablename[1] = "sinauserinforesult" + day;
			this.createTables(tablename[1]);
			this.insertTablename(tablename[1], time);
		} else {
			tablename[1] = tablename[0];
		}	

		return tablename;
	}

	/**
	 * 获取今天和昨天的数据表名
	 * @return
	 * @throws SQLException
	 */
	public String[] getTables() throws SQLException {
		long time = System.currentTimeMillis()/1000;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String day = sdf.format(new Date(time*1000l));
		day = day.replaceAll("-", "");
		String date = new String();
		if (day.subSequence(6,7).equals("0")) {
			date = day.substring(7);
		} else {
			date = day.substring(6);
		}
		String[] tablename = new String[2];
		String table = this.selectTablename();
		tablename[1] = table;
		if (Integer.parseInt(date) % 5 == 0) {
			tablename[0] = "1";  //需要换表
		} else {
			tablename[0] = "0";
		}	

		return tablename;
	}

	/**
	 * 起始时，创建表格
	 * @return
	 * @throws SQLException
	 */
	public String[] firstCreate() throws SQLException {

		String result[] = new String[2];
		long time = System.currentTimeMillis()/1000;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String day = sdf.format(new Date(time*1000l));
		day = day.replaceAll("-", "");
		@SuppressWarnings("unused")
		String date = new String();
		if (day.subSequence(6,7).equals("0")) {
			date = day.substring(7);
		} else {
			date = day.substring(6);
		}
		String tablename = "sinauserinforesult" + day;
		this.createTables(tablename);
		this.insertTablename(tablename, time);
		result[0] = day;
		result[1] = tablename;

		return result;
	}

	/**
	 * 清空数据表
	 * @param tablename
	 * @throws SQLException 
	 */
	public void truncateTables(String tablename) throws SQLException {
		String sql = new String("TRUNCATE TABLE " + tablename);
		Statement statement = this.conn.createStatement();
		statement.execute(sql);
		statement.close();
	}

	/**
	 * 获取昨天的数据
	 * @param usernameday: 如12345678920130513
	 * @return
	 * @throws SQLException 
	 */
	public HashMap<String,String> getYesterdayResult(String tablename, String date) throws SQLException {
		HashMap<String,String> result = new HashMap<String,String>();
		String sql = new String("SELECT `usernameday`,`followerscount`,`friendscount`,`statusescount` FROM " + tablename
				+ " WHERE `date` = \"" + date + "\"");
		Statement statement = this.conn.createStatement();
		ResultSet rs = statement.executeQuery(sql);
		String username = new String();
		String info = new String();
		while (rs.next()) {
			username = rs.getString("usernameday").substring(0, rs.getString("usernameday").length() - 8);
			info = rs.getString("followerscount") + "," + rs.getString("friendscount") + "," + rs.getString("statusescount");
			result.put(username, info);
		}
		rs.close();
		statement.close();

		return result;
	}

	/**
	 * 获取今天的数据分析结果
	 * @param tablename
	 * @param date
	 * @return
	 * @throws SQLException
	 */
	public HashMap<Integer,String> getTodayResult(String tablename, String date) throws SQLException {
		HashMap<Integer,String> result = new HashMap<Integer,String>();
		String sql = new String("SELECT * FROM " + tablename + " WHERE `date` = \"" + date + "\"");
		Statement statement = this.conn.createStatement();
		ResultSet rs = statement.executeQuery(sql);
		String info = new String();
		int i = 0;
		while (rs.next()) {
			info = rs.getString("usernameday") + "," + rs.getString("followerscount") + "," + 
					rs.getString("friendscount") + "," + rs.getString("statusescount") + "," + 
					rs.getString("followersadd") + "," + rs.getString("friendsadd") + "," + 
					rs.getString("statusesadd") + "," + rs.getString("followersaddratio") + "," + 
					rs.getString("friendsaddratio") + "," + rs.getString("statusesaddratio");
			result.put(i++, info);
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
	public void createTables(String tablename) throws SQLException {
		String sql = new String("CREATE TABLE " + tablename + " (" +
				"`id` int(10) NOT NULL AUTO_INCREMENT," +
				"`usernameday` varchar(20) NOT NULL," +
				"`date` VARCHAR(10) NOT NULL," +
				"`followerscount` int(10) NOT NULL," +
				"`friendscount` int(10) NOT NULL," +
				"`statusescount` int(10) NOT NULL," +
				"`followersadd` int(10) NOT NULL," +
				"`friendsadd` int(10) NOT NULL," +
				"`statusesadd` int(10) NOT NULL," +
				"`followersaddratio` varchar(10) NOT NULL," +
				"`friendsaddratio` varchar(10) NOT NULL," +
				"`statusesaddratio` varchar(10) NOT NULL," +
				"`lasttime` varchar(10) NOT NULL, PRIMARY KEY (`id`)," +
				"UNIQUE KEY `usernameday` (`usernameday`)" +
				") ENGINE=MyISAM DEFAULT CHARSET=utf8 AUTO_INCREMENT=1;");
		Statement statement = this.conn.createStatement();
		statement.executeUpdate(sql);
		statement.close();
	}

	/**
	 * 插入表格名称
	 * @param tablename
	 * @param time
	 * @throws SQLException
	 */
	public void insertTablename(String tablename, long time) throws SQLException {
		String sql = new String("INSERT INTO `sinauserinfotablename` (`tablename`,`lasttime`) VALUES (\""
				+ tablename + "\"," + time + ")");
		Statement statement = this.conn.createStatement();
		statement.execute(sql);
		statement.close();
	}

	/**
	 * 获取表格名
	 * @return
	 * @throws SQLException
	 */
	public String selectTablename() throws SQLException {
		String result = new String();
		String sql = new String("SELECT `tablename` FROM `sinauserinfotablename` ORDER BY `lasttime` DESC LIMIT 1");
		Statement statement = this.conn.createStatement();
		ResultSet rs = statement.executeQuery(sql);
		if (rs.next()) {
			result = rs.getString("tablename");
		} else {
			result = null;
		}
		rs.close();
		statement.close();

		return result;
	}

	/**
	 * 插入今天的数据分析结果
	 * @param tablename
	 * @param usernameday
	 * @param date
	 * @param followerscount
	 * @param friendscount
	 * @param statusescount
	 * @param followersadd
	 * @param friendsadd
	 * @param statusesadd
	 * @param followersaddratio
	 * @param friendsaddratio
	 * @param statusesaddratio
	 * @throws SQLException
	 */
	public void insertTodayResult(String tablename, String usernameday, String date, int followerscount,
			int friendscount, int statusescount, int followersadd, int friendsadd, int statusesadd, 
			String followersaddratio, String friendsaddratio, String statusesaddratio) throws SQLException {
		long time = System.currentTimeMillis()/1000;
		String sql = new String("INSERT INTO " + tablename + " (`usernameday`,`date`,`followerscount`," +
				"`friendscount`,`statusescount`,`followersadd`,`friendsadd`,`statusesadd`,`followersaddratio`," +
				"`friendsaddratio`,`statusesaddratio`,`lasttime`) VALUES (\"" + usernameday + "\",\"" + date +
				"\"," + followerscount + "," + friendscount + "," + statusescount + "," + followersadd + "," +
				friendsadd + "," + statusesadd + ",\"" + followersaddratio + "\",\"" + friendsaddratio + "\",\"" +
				statusesaddratio + "\"," + time + ")");
		Statement statement = this.conn.createStatement();
		statement.execute(sql);
		statement.close();
	}

	/**
	 * 获取用户与粉丝的交互数据
	 * @return
	 * @throws SQLException
	 */
	public HashMap<Integer,String> getIteractions(String date) throws SQLException {

		HashMap<Integer,String> result = new HashMap<Integer,String>();
		String sql = new String("SELECT * FROM `sinainteractions` WHERE `date` = " + date);
		Statement statement = this.conn.createStatement();
		ResultSet rs = statement.executeQuery(sql);
		int i = 0;
		String info = new String();
		while (rs.next()) {
			info = rs.getString("usernameday") + "=" + rs.getString("allcount") + "=" + rs.getString("emotionratio");
			result.put(i++, info);
		}
		rs.close();
		statement.close();

		return result;
	}

	public HashMap<Integer,String> getAllIteractions() throws SQLException {

		HashMap<Integer,String> result = new HashMap<Integer,String>();
		String sql = new String("SELECT * FROM `sinainteractions`");
		Statement statement = this.conn.createStatement();
		ResultSet rs = statement.executeQuery(sql);
		int i = 0;
		String info = new String();
		while (rs.next()) {
			info = rs.getString("usernameday") + "=" + rs.getString("allcount") + "=" + rs.getString("emotionratio");
			result.put(i++, info);
		}
		rs.close();
		statement.close();

		return result;
	}

	/**
	 * 插入用户与粉丝的交互数据
	 * @param usernameday
	 * @param allcount
	 * @param emotionratio
	 * @throws SQLException
	 */
	public void insertUserIteractions(String usernameday, String date, int allcount, String emotionratio) throws SQLException {
		long time = System.currentTimeMillis()/1000;
		String sql = new String("INSERT INTO `sinainteractions` (`usernameday`,`date`,`allcount`,`emotionratio`," +
				"`lasttime`) VALUES (\"" + usernameday + "\",\"" + date + "\"," + allcount + ",\"" +
				emotionratio.replaceAll("\"", "\\\\\"") + "\"," + time + ")");
		Statement statement = this.conn.createStatement();
		statement.execute(sql);
		statement.close();
	}

	public void insertUserIteractions1(String usernameday, String username, String date, int allcount, String emotionratio) throws SQLException {
		long time = System.currentTimeMillis()/1000;
		String sql = new String("INSERT INTO `sinainteractions` (`usernameday`,`username`,`date`,`allcount`,`emotionratio`," +
				"`lasttime`) VALUES (\"" + usernameday + "\",\"" + username + "\",\"" + date + "\"," + allcount + ",\"" +
				emotionratio.replaceAll("\"", "\\\\\"") + "\"," + time + ")");
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
