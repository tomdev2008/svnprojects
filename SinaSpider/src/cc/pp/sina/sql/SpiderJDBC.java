package cc.pp.sina.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cc.pp.sina.domain.RepInfo;
import cc.pp.sina.domain.RepUser;
import cc.pp.sina.domain.RepWeibo;

public class SpiderJDBC {

	private Connection conn;
	private final String driver = "com.mysql.jdbc.Driver";
	private String url = "";
	private String user = "pp_fenxi";
	private String password = "q#tmuYzC@sqB6!ok@sHd";

	public SpiderJDBC(String ip) {
		this.url = "jdbc:mysql://" + ip + ":3306/pp_fenxi?useUnicode=true&characterEncoding=utf-8";
	}

	public SpiderJDBC(String ip, String user, String password) {
		this.user = user;
		this.password = password;
		this.url = "jdbc:mysql://" + ip + ":3306/pp_fenxi?useUnicode=true&characterEncoding=utf-8";
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
		long time = System.currentTimeMillis() / 1000;
		while (Integer.parseInt(result[1]) < time) {
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

		long time = System.currentTimeMillis() / 1000;
		String sql = new String("UPDATE `member_bind_sina` SET `lasttime` = " + time + " WHERE `oauth_token` = \""
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
	public void deleteAccessToken(String token) throws SQLException {

		String sql = new String("DELETE FROM `member_bind_sina` WHERE `oauth_token` = \"" + token + "\"");
		Statement statement = this.conn.createStatement();
		statement.execute(sql);
		statement.close();
	}

	public void insertWeiboInfo(String tablename, String username, String oriNickname, String oriUsername,
			String oriWeiboText, String oriZanCount, String oriRepCount, String oriComcount, String weiboText,
			String zanCount, String repcount, String comCount, String createAt, String source) throws SQLException {

		long time = System.currentTimeMillis() / 1000;
		String sql = "INSERT INTO " + tablename + " (`username`,`oriNickname`,`oriUsername`,"
				+ "`oriWeiboText`,`oriZanCount`,`oriRepCount`,`oriComcount`,`weiboText`,"
				+ "`zanCount`,`repcount`,`comCount`,`createAt`,`source`,`lasttime`)"
				+ " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

		PreparedStatement pstmt = null;
		try {
			pstmt = this.conn.prepareStatement(sql);
			pstmt.setString(1, username);
			pstmt.setString(2, oriNickname);
			pstmt.setString(3, oriUsername);
			pstmt.setString(4, oriWeiboText);
			pstmt.setInt(5, Integer.parseInt(oriZanCount));
			pstmt.setInt(6, Integer.parseInt(oriRepCount));
			pstmt.setInt(7, Integer.parseInt(oriComcount));
			pstmt.setString(8, weiboText);
			pstmt.setInt(9, Integer.parseInt(zanCount));
			pstmt.setInt(10, Integer.parseInt(repcount));
			pstmt.setInt(11, Integer.parseInt(comCount));
			pstmt.setString(12, createAt);
			pstmt.setString(13, source);
			pstmt.setLong(14, time);
			pstmt.executeUpdate();
		} finally {
			if (pstmt != null) {
				pstmt.close();
			}
		}
	}

	/**
	 * 插入微博数据
	 * @param tablename
	 * @param username
	 * @param nickname
	 * @param weibotext
	 * @param createat
	 * @param source
	 * @throws SQLException
	 */
	public void insertRepWeiboInfo(String tablename, String domainname, String nickname, String weibotext,
			String createat, String source) throws SQLException {

		long time = System.currentTimeMillis() / 1000;
		String sql = "INSERT INTO " + tablename + " (`domainname`,`nickname`,`weibotext`,"
				+ "`createat`,`source`,`lasttime`) VALUES (?,?,?,?,?,?)";
		PreparedStatement pstmt = null;
		try {
			pstmt = this.conn.prepareStatement(sql);
			pstmt.setString(1, domainname);
			pstmt.setString(2, nickname);
			pstmt.setString(3, weibotext);
			pstmt.setString(4, createat);
			pstmt.setString(5, source);
			pstmt.setLong(6, time);
			pstmt.execute();
		} finally {
			if (pstmt != null) {
				pstmt.close();
			}
		}
	}

	public List<RepWeibo> getRepWeiboInfo(String tablename, int nums) throws SQLException {

		List<RepWeibo> result = new ArrayList<RepWeibo>();
		String sql = "SELECT `domainname`,`nickname`,`weibotext`,`createat`,`source` FROM " //
				+ tablename + " LIMIT " + nums;
		PreparedStatement pstmt = null;
		try {
			pstmt = this.conn.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				RepWeibo temp = new RepWeibo();
				temp.setDomainname(rs.getString(1));
				temp.setNickname(rs.getString(2));
				temp.setWeibotext(rs.getString(3));
				temp.setCreateat(rs.getString(4));
				temp.setSource(rs.getString(5));
				result.add(temp);
			}
			return result;
		} finally {
			if (pstmt != null) {
				pstmt.close();
			}
		}
	}

	public HashMap<String, RepWeibo> getRepWeiboInfo(String tablename) throws SQLException {

		HashMap<String, RepWeibo> result = new HashMap<String, RepWeibo>();
		String sql = "SELECT `domainname`,`nickname`,`weibotext`,`createat`,`source` FROM " + tablename;
		PreparedStatement pstmt = null;
		try {
			pstmt = this.conn.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				RepWeibo temp = new RepWeibo();
				temp.setDomainname(rs.getString(1));
				temp.setNickname(rs.getString(2));
				temp.setWeibotext(rs.getString(3));
				temp.setCreateat(rs.getString(4));
				temp.setSource(rs.getString(5));
				result.put(rs.getString(2), temp);
			}
			return result;
		} finally {
			if (pstmt != null) {
				pstmt.close();
			}
		}
	}

	public void insertRepInfo(String tablename, String username, String nickname, String description, String gender,
			int province, int fanscount, int friendscount, int weibocount, boolean verify, int verifytype,
			String verifiedreason, long lastwbcreated, String weibotext, String createat, String source)
			throws SQLException {

		String sql = "INSERT INTO " + tablename + " (`username`,`nickname`,`description`," + //
				"`gender`,`province`,`fanscount`,`friendscount`,`weibocount`,`verify`,`verifytype`," + //
				"`verifiedreason`,`lastwbcreated`,`weibotext`,`createat`,`source`) VALUES " + //
				"(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		PreparedStatement pstmt = null;
		try {
			pstmt = this.conn.prepareStatement(sql);
			pstmt.setString(1, username);
			pstmt.setString(2, nickname);
			pstmt.setString(3, description);
			pstmt.setString(4, gender);
			pstmt.setInt(5, province);
			pstmt.setInt(6, fanscount);
			pstmt.setInt(7, friendscount);
			pstmt.setInt(8, weibocount);
			pstmt.setBoolean(9, verify);
			pstmt.setInt(10, verifytype);
			pstmt.setString(11, verifiedreason);
			pstmt.setLong(12, lastwbcreated);
			pstmt.setString(13, weibotext);
			pstmt.setString(14, createat);
			pstmt.setString(15, source);
			pstmt.executeUpdate();
		} finally {
			if (pstmt != null) {
				pstmt.close();
			}
		}
	}

	public HashMap<String, RepUser> getRepUserInfos(String tablename) throws SQLException {

		HashMap<String, RepUser> result = new HashMap<String, RepUser>();
		String sql = "SELECT `username`,`nickname`,`description`,`gender`,`province`,`fanscount`,"
				+ "`friendscount`,`weibocount`,`verify`,`verifytype`,`verifiedreason`,`lastwbcreated`" + " FROM "
				+ tablename;
		PreparedStatement pstmt = null;
		try {
			pstmt = this.conn.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				RepUser repuser = new RepUser();
				repuser.setUsername(rs.getString(1));
				repuser.setNickname(rs.getString(2));
				repuser.setDescription(rs.getString(3));
				repuser.setGender(rs.getString(4));
				repuser.setProvince(rs.getInt(5));
				repuser.setFanscount(rs.getInt(6));
				repuser.setFriendscount(rs.getInt(7));
				repuser.setWeibocount(rs.getInt(8));
				repuser.setVerify(rs.getBoolean(9));
				repuser.setVerifytype(rs.getInt(10));
				repuser.setVerifiedreason(rs.getString(11));
				repuser.setLastwbcreated(rs.getLong(12));
				result.put(rs.getString(2), repuser);
			}
			return result;
		} finally {
			if (pstmt != null) {
				pstmt.close();
			}
		}
	}

	public List<RepUser> getRepUserInfos(String tablename, int nums) throws SQLException {

		List<RepUser> result = new ArrayList<RepUser>();
		String sql = "SELECT `username`,`nickname`,`description`,`gender`,`province`,`fanscount`,"
				+ "`friendscount`,`weibocount`,`verify`,`verifytype`,`verifiedreason`,`lastwbcreated`" + " FROM "
				+ tablename + " LIMIT " + nums;
		PreparedStatement pstmt = null;
		try {
			pstmt = this.conn.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				RepUser repuser = new RepUser();
				repuser.setUsername(rs.getString(1));
				repuser.setNickname(rs.getString(2));
				repuser.setDescription(rs.getString(3));
				repuser.setGender(rs.getString(4));
				repuser.setProvince(rs.getInt(5));
				repuser.setFanscount(rs.getInt(6));
				repuser.setFriendscount(rs.getInt(7));
				repuser.setWeibocount(rs.getInt(8));
				repuser.setVerify(rs.getBoolean(9));
				repuser.setVerifytype(rs.getInt(10));
				repuser.setVerifiedreason(rs.getString(11));
				repuser.setLastwbcreated(rs.getLong(12));
				result.add(repuser);
			}
			return result;
		} finally {
			if (pstmt != null) {
				pstmt.close();
			}
		}
	}

	public List<RepInfo> getRepInfos(String tablename, int nums) throws SQLException {

		List<RepInfo> result = new ArrayList<RepInfo>();
		String sql = "SELECT `username`,`nickname`,`description`,`gender`,`province`,`fanscount`,"
				+ "`friendscount`,`weibocount`,`verify`,`verifytype`,`verifiedreason`,`lastwbcreated`,"
				+ "`weibotext`,`createat`,`source` FROM " + tablename + " LIMIT " + nums;
		PreparedStatement pstmt = null;
		try {
			pstmt = this.conn.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				RepInfo repinfo = new RepInfo();
				repinfo.setUsername(rs.getString(1));
				repinfo.setNickname(rs.getString(2));
				repinfo.setDescription(rs.getString(3));
				repinfo.setGender(rs.getString(4));
				repinfo.setProvince(rs.getInt(5));
				repinfo.setFanscount(rs.getInt(6));
				repinfo.setFriendscount(rs.getInt(7));
				repinfo.setWeibocount(rs.getInt(8));
				repinfo.setVerify(rs.getBoolean(9));
				repinfo.setVerifytype(rs.getInt(10));
				repinfo.setVerifiedreason(rs.getString(11));
				repinfo.setLastwbcreated(rs.getLong(12));
				repinfo.setWeibotext(rs.getString(13));
				repinfo.setCreateat(rs.getString(14));
				repinfo.setSource(rs.getString(15));
				result.add(repinfo);
			}
			return result;
		} finally {
			if (pstmt != null) {
				pstmt.close();
			}
		}
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
			result.put(rs.getString(1), "OK");
		}
		rs.close();
		pstmt.close();

		return result;
	}

	public void insertSinaUser(String tablename, String username) throws SQLException {

		String sql = "INSERT INTO " + tablename + " (`username`) VALUES (?)";
		PreparedStatement pstmt = null;
		try {
			pstmt = this.conn.prepareStatement(sql);
			pstmt.setString(1, username);
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

	/**
	 * @ 关闭数据库
	 * @throws SQLException
	 */
	public void sqlClose() throws SQLException {
		this.conn.close();
	}

}

