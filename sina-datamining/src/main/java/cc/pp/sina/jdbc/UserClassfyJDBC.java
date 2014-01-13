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

import cc.pp.sina.analysis.userclassfy.dao.UserAllParams;
import cc.pp.sina.constant.DataBaseConstant;

/**
 * Title: 用户分类的JDBC
 * @author wanggang
 * @version 1.1
 * @since 2013-05-27
 */
public class UserClassfyJDBC implements DataBaseConstant {

	private Connection conn;
	private final String driver = DRIVER;
	private String url = new String();
	private String user = USER;
	private String password = PASSWORD;

	/**
	 * @ construct
	 * @param ip
	 */
	public UserClassfyJDBC(String ip) {
		this.url = "jdbc:mysql://" + ip + ":3306/pp_fenxi?useUnicode=true&characterEncoding=utf-8";
	}

	public UserClassfyJDBC(String ip, String user, String password) {
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
		while (Integer.parseInt(result[1]) < time)
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
		String sql = new String("SELECT `oauth_token`,`expires_in` FROM `member_bind_sina` LIMIT 1");
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
	 * 更新token的时间
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

	public HashMap<Integer,String> getSinaUsers(String tablename) throws SQLException {

		HashMap<Integer,String> result = new HashMap<Integer,String>();
		String sql = new String("SELECT `username` FROM " + tablename);
		Statement statement = this.conn.createStatement();
		ResultSet rs = statement.executeQuery(sql);
		int i = 0;
		String info = new String();
		while (rs.next()) {
			info = rs.getString("username");
			result.put(i++, info);
		}
		rs.close();
		statement.close();

		return result;
	}

	public HashMap<String,String> getOriginalUsers(String tablename) throws SQLException {

		HashMap<String,String> result = new HashMap<String,String>();
		String sql = new String("SELECT `username`,`nickname` FROM " + tablename);
		Statement statement = this.conn.createStatement();
		ResultSet rs = statement.executeQuery(sql);
		while (rs.next()) {
			result.put(rs.getString("nickname"), rs.getString("username"));
		}
		rs.close();
		statement.close();

		return result;
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

	public void deleteUsernames(String tablename, String username) throws SQLException {

		String sql = new String("DELETE FROM " + tablename + " WHERE `username` = \"" + username + "\"");
		Statement statement = this.conn.createStatement();
		statement.execute(sql);
		statement.close();
	}

	public void insertSinaUsers(String tablename, String username, String nickname) throws SQLException {

		String sql = new String("INSERT INTO " + tablename + " (`username`,`nickname`,`lasttime`) " +
				"VALUES (\"" + username + "\",\"" + nickname + "\"," + System.currentTimeMillis()/1000 + ")");
		Statement statement = this.conn.createStatement();
		statement.execute(sql);
		statement.close();
	}
	
	public void insertSinaUsers(String tablename, String username) throws SQLException {

		String sql = new String("INSERT INTO " + tablename + " (`username`) VALUES (" + username + ")");
		Statement statement = this.conn.createStatement();
		statement.execute(sql);
		statement.close();
	}

	public void insertUnusedUsers(String tablename, String username) throws SQLException {

		String sql = new String("INSERT INTO " + tablename + " (`username`) " +
				"VALUES (\"" + username + "\")");
		Statement statement = this.conn.createStatement();
		statement.execute(sql);
		statement.close();
	}

	/**
	 * 插入微博主参数数据
	 */
	public void insertUsersParams(String tablename, String username, String nickname, String description,
			int fanscount, int weibocount, float averagewbs, int influence, int activation, int activecount,
			float addvratio, float activeratio, float maleratio, float fansexistedratio, int verify, long allfanscount,
			long allactivefanscount, String top5provinces, float oriratio, float aveorirepcom, float averepcom,
			String wbsource, float averepsbyweek, float averepsbymonth, float avereposterquality, long aveexposionsum,
			float validrepcombyweek, float validrepcombymonth, String usertags) throws SQLException {

		long time = System.currentTimeMillis() / 1000;
		String sql = "INSERT INTO " + tablename + " (`username`,`nickname`,`description`,`fanscount`,`weibocount`,"
				+ "`averagewbs`,`influence`,`activation`,`activecount`,`addvratio`,`activeratio`,"
				+ "`maleratio`,`fansexistedratio`,`verify`,`allfanscount`,`allactivefanscount`,"
				+ "`top5provinces`,`oriratio`,`aveorirepcom`,`averepcom`,`wbsource`,`averepsbyweek`,"
				+ "`averepsbymonth`,`avereposterquality`,`aveexposionsum`,`validrepcombyweek`,"
				+ "`validrepcombymonth`,`usertags`,`lasttime`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		PreparedStatement pstmt = null;
		try {
			pstmt = this.conn.prepareStatement(sql);
			pstmt.setString(1, username);
			pstmt.setString(2, nickname);
			pstmt.setString(3, description);
			pstmt.setInt(4, fanscount);
			pstmt.setInt(5, weibocount);
			pstmt.setFloat(6, averagewbs);
			pstmt.setInt(7, influence);
			pstmt.setInt(8, activation);
			pstmt.setInt(9, activecount);
			pstmt.setFloat(10, addvratio);
			pstmt.setFloat(11, activeratio);
			pstmt.setFloat(12, maleratio);
			pstmt.setFloat(13, fansexistedratio);
			pstmt.setInt(14, verify);
			pstmt.setLong(15, allfanscount);
			pstmt.setLong(16, allactivefanscount);
			pstmt.setString(17, top5provinces);
			pstmt.setFloat(18, oriratio);
			pstmt.setFloat(19, aveorirepcom);
			pstmt.setFloat(20, averepcom);
			pstmt.setString(21, wbsource);
			pstmt.setFloat(22, averepsbyweek);
			pstmt.setFloat(23, averepsbymonth);
			pstmt.setFloat(24, avereposterquality);
			pstmt.setLong(25, aveexposionsum);
			pstmt.setFloat(26, validrepcombyweek);
			pstmt.setFloat(27, validrepcombymonth);
			pstmt.setString(28, usertags);
			pstmt.setLong(29, time);
			pstmt.executeUpdate();
		} finally {
			if (pstmt != null) {
				pstmt.close();
			}
		}

	}

	public HashMap<String,String> getUsersParams(String tablename) throws SQLException {

		HashMap<String,String> result = new HashMap<String,String>();
		String sql = new String("SELECT * FROM " + tablename);
		Statement statement = this.conn.createStatement();
		ResultSet rs = statement.executeQuery(sql);
		String info = new String();
		while (rs.next()) {
			info = rs.getString("fanscount") + "=" + rs.getString("weibocount") + "=" + rs.getString("averagewbs") + "="
					+ rs.getString("influence") + "=" + rs.getString("activation") + "=" + rs.getString("activecount") + "="
					+ rs.getString("addvratio") + "=" + rs.getString("headratio") + "=" + rs.getString("activeratio") + "="
					+ rs.getString("maleratio") + "=" + rs.getString("createdtime") + "=" + rs.getString("fansexistedratio") + "="
					+ rs.getString("description");
			result.put(rs.getString("nickname"), info);
		}
		rs.close();
		statement.close();

		return result;
	}
	
	public HashMap<String, String> getResult(String tablename) throws SQLException {

		HashMap<String, String> result = new HashMap<String, String>();
		String sql = new String("SELECT * FROM " + tablename);
		Statement statement = this.conn.createStatement();
		ResultSet rs = statement.executeQuery(sql);
		String info = new String();
		while (rs.next()) {
			info = rs.getString("nickname") + "&=" + rs.getString("description") + "&=" + rs.getString("usertags")
					+ "&=" + rs.getString("fanscount") + "&=" + rs.getString("weibocount") + "&="
					+ rs.getString("averagewbs") + "&=" + rs.getString("influence") + "&=" + rs.getString("activation")
					+ "&=" + rs.getString("activecount") + "&=" + rs.getString("addvratio") + "&="
					+ rs.getString("activeratio") + "&=" + rs.getString("maleratio") + "&="
					+ rs.getString("fansexistedratio") + "&=" + rs.getString("verify") + "&="
					+ rs.getString("allfanscount") + "&=" + rs.getString("allactivefanscount") + "&="
					+ rs.getString("top5provinces") + "&=" + rs.getString("oriratio") + "&="
					+ rs.getString("aveorirepcom") + "&=" + rs.getString("averepcom") + "&=" + rs.getString("wbsource")
					+ "&=" + rs.getString("averepsbyweek") + "&=" + rs.getString("averepsbymonth") + "&="
					+ rs.getString("validrepcombyweek") + "&=" + rs.getString("validrepcombymonth") + "&="
					+ rs.getString("avereposterquality") + "&=" + rs.getString("aveexposionsum");
			result.put(rs.getString("username"), info);
		}
		rs.close();
		statement.close();

		return result;
	}

	public List<UserAllParams> getUserResult(String tablename) throws SQLException {

		List<UserAllParams> result = new ArrayList<UserAllParams>();
		String sql = new String("SELECT * FROM " + tablename);
		Statement statement = this.conn.createStatement();
		ResultSet rs = statement.executeQuery(sql);
		while (rs.next()) {
			UserAllParams temp = new UserAllParams();
			temp.setUsername(rs.getString("username"));
			temp.setNickname(rs.getString("nickname"));
			temp.setDescription(rs.getString("description"));
			temp.setUsertags(rs.getString("usertags"));
			temp.setFanscount(rs.getInt("fanscount"));
			temp.setWeibocount(rs.getInt("weibocount"));
			temp.setAveragewbs(rs.getInt("averagewbs"));
			temp.setInfluence(rs.getInt("influence"));
			temp.setActivation(rs.getInt("activation"));
			temp.setActivecount(rs.getInt("activecount"));
			temp.setAddvratio(rs.getFloat("addvratio"));
			temp.setActiveratio(rs.getFloat("activeratio"));
			temp.setMaleratio(rs.getFloat("maleratio"));
			temp.setFansexistedratio(rs.getFloat("fansexistedratio"));
			temp.setVerify(rs.getInt("verify"));
			temp.setAllfanscount(rs.getLong("allfanscount"));
			temp.setAllactivefanscount(rs.getLong("allactivefanscount"));
			temp.setTop5provinces(rs.getString("top5provinces"));
			temp.setOriratio(rs.getFloat("oriratio"));
			temp.setAveorirepcom(rs.getFloat("aveorirepcom"));
			temp.setAverepcom(rs.getFloat("averepcom"));
			temp.setWbsource(rs.getString("wbsource"));
			temp.setAverepcombyweek(rs.getFloat("averepsbyweek"));
			temp.setAverepcombymonth(rs.getFloat("averepsbymonth"));
			temp.setValidrepcombyweek(rs.getFloat("validrepcombyweek"));
			temp.setValidrepcombymonth(rs.getFloat("validrepcombymonth"));
			temp.setAvereposterquality(rs.getFloat("avereposterquality"));
			temp.setAveexposionsum(rs.getLong("aveexposionsum"));
			result.add(temp);
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
