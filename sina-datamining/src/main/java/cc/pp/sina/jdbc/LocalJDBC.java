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

import cc.pp.e.bozhu.Bozhu;
import cc.pp.sina.constant.DataBaseConstant;

public class LocalJDBC implements DataBaseConstant {

	private Connection conn;
	private final String driver = DRIVER;
	private String url = new String();
	private String user = USER;
	private String password = PASSWORD;

	public LocalJDBC(String ip) {
		this.url = "jdbc:mysql://" + ip + ":3306/pp_fenxi?useUnicode=true&characterEncoding=utf-8";
	}

	public LocalJDBC(String ip, String user, String password) {
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

	public List<String> getAllTokens() throws SQLException {

		List<String> result = new ArrayList<String>();
		String sql = new String("SELECT `oauth_token` FROM `member_bind_sina`");
		Statement statement = this.conn.createStatement();
		ResultSet rs = statement.executeQuery(sql);
		if (rs.next()) {
			result.add(rs.getString("oauth_token"));
		}
		rs.close();
		statement.close();

		return result;
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

	public void createWbUsersInfoTable(String tablename) throws SQLException {

		String sql = "CREATE TABLE " + tablename + " (`id` int(10) NOT NULL AUTO_INCREMENT,"
				+ "`username` char(20) NOT NULL,`nickname` char(50) NOT NULL,"
				+ "`description` char(250) NOT NULL,`userurl` char(250) NOT NULL,"
				+ "`gender` char(5) NOT NULL,`fanscount` int(10) NOT NULL,"
				+ "`weibocount` int(10) NOT NULL,`province` int(2) NOT NULL,"
				+ "`repostcount` int(10) NOT NULL,`commentcount` int(10) NOT NULL,"
				+ "`isoriginal` char(30) NOT NULL,`addv` int(2) NOT NULL,"
				+ "`verifytype` int(10) NOT NULL,`verify` char(10) NOT NULL,"
				+ "`createdtime` char(50) NOT NULL,PRIMARY KEY (`id`)) "
				+ "ENGINE=MyISAM DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;";
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

	public void insertWbUsersInfo(String username, String nickname, String description, String userurl, String gender,
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
			pstmt.setString(5, gender);
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

	public void insertWbUsersInfo(String tablename, String username, String nickname, String description,
			String userurl, String gender, int fanscount, int weibocount, int province, int repostcount,
			int commentcount, String isoriginal, int addv, int verifytype, String verify, String createtime)
			throws SQLException {

		String sql = "INSERT INTO " + tablename + " (`username`,`nickname`,`description`,`userurl`,`gender`,"
				+ "`fanscount`,`weibocount`,`province`,`repostcount`,`commentcount`,`isoriginal`,`addv`,"
				+ "`verifytype`,`verify`,`createdtime`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		PreparedStatement pstmt = null;
		try {
			pstmt = this.conn.prepareStatement(sql);
			pstmt.setString(1, username);
			pstmt.setString(2, nickname);
			pstmt.setString(3, description.replaceAll("x", ""));
			pstmt.setString(4, userurl);
			pstmt.setString(5, gender);
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

	public void insertUsersInfo(String tablename, String username, String nickname, String description, int gender,
			int province, int fanscount, int friendscount, int weibocount, int verify, int verifytype,
			String verifiedreason, long lastwbcreated) throws SQLException {

		String sql = "INSERT INTO " + tablename + " (`username`,`nickname`,`description`," + //
				"`gender`,`province`,`fanscount`,`friendscount`,`weibocount`,`verify`,`verifytype`," + //
				"`verifiedreason`,`lastwbcreated`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
		PreparedStatement pstmt = null;
		try {
			pstmt = this.conn.prepareStatement(sql);
			pstmt.setString(1, username);
			pstmt.setString(2, nickname);
			//			pstmt.setString(3, description.replaceAll("x", ""));
			pstmt.setString(3, "0");
			pstmt.setInt(4, gender);
			pstmt.setInt(5, province);
			pstmt.setInt(6, fanscount);
			pstmt.setInt(7, friendscount);
			pstmt.setInt(8, weibocount);
			pstmt.setInt(9, verify);
			pstmt.setInt(10, verifytype);
			pstmt.setString(11, verifiedreason);
			pstmt.setLong(12, lastwbcreated);
			pstmt.executeUpdate();
		} finally {
			if (pstmt != null) {
				pstmt.close();
			}
		}
	}

	public void insertUserBaseInfo(String tablename, String username, String nickname, int province, int city,
			String location, String description, String headurl, String gender, int fanscount, int friendscount,
			int weiboscount, int favouritescount, long createdat, int verified, String verifiedreason, int bifanscount,
			long lastwbcreatedat, String weihao, String tags) throws SQLException {

		long lasttime = System.currentTimeMillis() / 1000;
		String sql = "INSERT INTO " + tablename + " (`username`,`nickname`,`province`,`city`," + //
				"`location`,`description`,`headurl`,`gender`,`fanscount`,`friendscount`," + //
				"`weiboscount`,`favouritescount`,`createdat`,`verified`,`verifiedreason`," + //
				"`bifanscount`,`lastwbcreatedat`,`weihao`,`tags`,`lasttime`) VALUES " + //
				"(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		PreparedStatement pstmt = null;
		try {
			pstmt = this.conn.prepareStatement(sql);
			pstmt.setString(1, username);
			pstmt.setString(2, nickname);
			pstmt.setInt(3, province);
			pstmt.setInt(4, city);
			pstmt.setString(5, location);
			pstmt.setString(6, description);
			pstmt.setString(7, headurl);
			pstmt.setString(8, gender);
			pstmt.setInt(9, fanscount);
			pstmt.setInt(10, friendscount);
			pstmt.setInt(11, weiboscount);
			pstmt.setInt(12, favouritescount);
			pstmt.setLong(13, createdat);
			pstmt.setInt(14, verified);
			pstmt.setString(15, verifiedreason);
			pstmt.setInt(16, bifanscount);
			pstmt.setLong(17, lastwbcreatedat);
			pstmt.setString(18, weihao);
			pstmt.setString(19, tags);
			pstmt.setLong(20, lasttime);
			pstmt.execute();
		} finally {
			if (pstmt != null) {
				pstmt.close();
			}
		}
	}

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

	public List<Bozhu> getUserResult(String tablename, int low, int high) throws SQLException {

		List<Bozhu> result = new ArrayList<Bozhu>();
		String sql = new String("SELECT * FROM " + tablename + " WHERE `id` >= " //
				+ low + " AND `id` <= " + high);
		Statement statement = this.conn.createStatement();
		ResultSet rs = statement.executeQuery(sql);
		while (rs.next()) {
			Bozhu temp = new Bozhu(rs.getString("username"), "sina");
			temp.setUsername(rs.getString("username"));
			temp.setNickname(rs.getString("nickname"));
			temp.setDescription(rs.getString("description"));
			//			temp.setUsertags(rs.getString("usertags"));
			temp.setFannum(rs.getInt("fanscount"));
			temp.setWbnum(rs.getInt("weibocount"));
			temp.setWb_avg_daily((float) Math.round(rs.getFloat("averagewbs") * 10000) / 10000);
			temp.setActive(rs.getInt("activation"));
			temp.setAct_fan(rs.getInt("activecount"));
			temp.setVrate((float) Math.round(rs.getFloat("addvratio") * 10000) / 10000);
			temp.setAct_fan_rate((float) Math.round(rs.getFloat("activeratio") * 10000) / 10000);
			temp.setMalerate((float) Math.round(rs.getFloat("maleratio") * 10000) / 10000);
			temp.setExsit_fan_rate((float) Math.round(rs.getFloat("fansexistedratio") * 10000) / 10000);
			temp.setVerify(rs.getInt("verify"));
			temp.setFan_fans(rs.getLong("allfanscount"));
			temp.setAct_fan_fans(rs.getLong("allactivefanscount"));
			//			temp.setTop5provinces(rs.getString("top5provinces"));
			temp.setOrig_wb_rate((float) Math.round(rs.getFloat("oriratio") * 10000) / 10000);
			temp.setOrig_wb_avg_repost((float) Math.round(rs.getFloat("aveorirepcom") * 10000) / 10000);
			temp.setWb_avg_repost((float) Math.round(rs.getFloat("averepcom") * 10000) / 10000);
			//			temp.setWbsource(rs.getString("wbsource"));
			temp.setWb_avg_repost_lastweek((float) Math.round(rs.getFloat("averepsbyweek") * 10000) / 10000);
			temp.setWb_avg_repost_lastmonth((float) Math.round(rs.getFloat("averepsbymonth") * 10000) / 10000);
			temp.setWb_avg_valid_repost_lastweek((float) Math.round(rs.getFloat("validrepcombyweek") * 10000) / 10000);
			temp.setWb_avg_valid_repost_lastmonth((float) Math.round(rs.getFloat("validrepcombymonth") * 10000) / 10000);
			temp.setRt_user_avg_quality((float) Math.round(rs.getFloat("avereposterquality") * 10000) / 10000);
			temp.setAvg_valid_fan_cover_last100(rs.getLong("aveexposionsum"));
			
			float active = (float) Math.pow(Math.log10(rs.getFloat("activecount") + 1), 3);
			float addv = 0.0f;
			if (rs.getFloat("addvratio") != 0) {
				addv = (float) (Math.log(rs.getFloat("addvratio"))/Math.log(2)*2);
			}
			float repwbs = (float) ((Math.log(rs.getFloat("aveorirepcom") + 1) + 
					Math.log(rs.getFloat("averepcom") + 1))/Math.log(2) + 
					Math.log(rs.getFloat("averepsbymonth") + 1));	
			float inf = (active - addv + repwbs) / 2 - 10;
			if (inf < 0) {
				inf = (float) (Math.random() * 10);
			}
			if (inf > 100) {
				inf = 100;
			}
			temp.setInfluence(Math.round(inf));
			result.add(temp);
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

	public List<HashMap<String, String>> getUsersNickname(String tablename, int nums) throws SQLException {

		List<HashMap<String, String>> result = new ArrayList<HashMap<String, String>>();
		String sql = "SELECT `domainname`,`nickname` FROM " + tablename + " LIMIT " + nums;
		PreparedStatement pstmt = this.conn.prepareStatement(sql);
		ResultSet rs = pstmt.executeQuery();
		while (rs.next()) {
			HashMap<String, String> temp = new HashMap<String, String>();
			if (rs.getString(2).length() < 2) {
				temp.put("name", "domainname");
				temp.put("data", rs.getString(1));
			} else {
				temp.put("name", "nickname");
				temp.put("data", rs.getString(2));
			}
			result.add(temp);
		}
		rs.close();
		pstmt.close();

		return result;
	}

	/**
	 * 插入用户名
	 * @param tablename
	 * @param uids
	 * @throws SQLException
	 */
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

	public void insertUsersInfo(String tablename, String username, String nickname, int usedcount, String description,
			String gender, int province, int fanscount, int friendscount, int weibocount, boolean verify,
			int verifytype, String verifiedreason, long lastwbcreated) throws SQLException {

		String sql = "INSERT INTO " + tablename + " (`username`,`nickname`,`usedcount`,`description`," + //
				"`gender`,`province`,`fanscount`,`friendscount`,`weibocount`,`verify`,`verifytype`," + //
				"`verifiedreason`,`lastwbcreated`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";
		PreparedStatement pstmt = null;
		try {
			pstmt = this.conn.prepareStatement(sql);
			pstmt.setString(1, username);
			pstmt.setString(2, nickname);
			pstmt.setInt(3, usedcount);
			//			pstmt.setString(3, description.replaceAll("x", ""));
			pstmt.setString(4, "0");
			pstmt.setString(5, gender);
			pstmt.setInt(6, province);
			pstmt.setInt(7, fanscount);
			pstmt.setInt(8, friendscount);
			pstmt.setInt(9, weibocount);
			pstmt.setBoolean(10, verify);
			pstmt.setInt(11, verifytype);
			pstmt.setString(12, verifiedreason);
			pstmt.setLong(13, lastwbcreated);
			pstmt.executeUpdate();
		} finally {
			if (pstmt != null) {
				pstmt.close();
			}
		}
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

	public HashMap<String, String> getFriendUids(String tablename, int lower, int higher) throws SQLException {

		HashMap<String, String> result = new HashMap<String, String>();
		String sql = "SELECT `username`,`frienduids` FROM " + tablename + " WHERE `id` >= " //
				+ lower + " AND `id` < " + higher;
		PreparedStatement pstmt = null;
		try {
			pstmt = this.conn.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				result.put(rs.getString(1), rs.getString(2));
			}
			return result;
		} finally {
			if (pstmt != null) {
				pstmt.close();
			}
		}
	}

	public int getMaxFriends(String tablename) throws SQLException {

		int result = 0;
		String sql = "SELECT Max(`id`) FROM " + tablename;
		PreparedStatement pstmt = null;
		try {
			pstmt = this.conn.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				result = rs.getInt(1);
			}
		} finally {
			if (pstmt != null) {
				pstmt.close();
			}
		}
		return result;
	}

	public void insertFriendUids(String tablename, String username, int friendscount, String frienduids)
			throws SQLException {

		String sql = "INSERT INTO " + tablename + " (`username`,`friendscount`,`frienduids`) VALUES (?,?,?)";
		PreparedStatement pstmt = null;
		try {
			pstmt = this.conn.prepareStatement(sql);
			pstmt.setString(1, username);
			pstmt.setInt(2, friendscount);
			pstmt.setString(3, frienduids);
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

	public void insertTempsinauserinfo(String tablename, String username, String nickname, String gender,
			String province,
			int verifytype) throws SQLException {

		String sql = "INSERT INTO " + tablename
				+ " (`username`,`nickname`,`gender`,`province`,`verifytype`) VALUES (?,?,?,?,?)";
		PreparedStatement pstmt = null;
		try {
			pstmt = this.conn.prepareStatement(sql);
			pstmt.setString(1, username);
			pstmt.setString(2, nickname);
			pstmt.setString(3, gender);
			pstmt.setString(4, province);
			pstmt.setInt(5, verifytype);
			pstmt.execute();
		} finally {
			if (pstmt != null) {
				pstmt.close();
			}
		}
	}

	public void createUsersTable(String tablename) throws SQLException {

		String sql = "CREATE TABLE " + tablename + " (`id` int(10) NOT NULL AUTO_INCREMENT,"
				+ "`username` char(10) NOT NULL,PRIMARY KEY (`id`)) "
				+ "ENGINE=MyISAM DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;";
		Statement statement = this.conn.createStatement();
		statement.executeUpdate(sql);
		statement.close();
	}

	public void createFriendsTable(String tablename) throws SQLException {

		String sql = "CREATE TABLE " + tablename + " (`id` int(10) NOT NULL AUTO_INCREMENT,"
				+ "`username` char(10) NOT NULL,`friendscount` int(5) NOT NULL,"
				+ "`frienduids` mediumtext NOT NULL,PRIMARY KEY (`id`),"
				+ "KEY `username` (`username`),FULLTEXT KEY `username_2` (`username`)"
				+ ") ENGINE=MyISAM DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;";
		Statement statement = this.conn.createStatement();
		statement.executeUpdate(sql);
		statement.close();
	}

	public void createSinauserTable(String tablename) throws SQLException {

		String sql = "CREATE TABLE " + tablename + " (`id` int(10) NOT NULL AUTO_INCREMENT,"
				+ "`username` char(10) NOT NULL,`nickname` char(30) NOT NULL,"
				+ "`usedcount` int(7) NOT NULL,`description` varchar(500) NOT NULL,"
				+ "`gender` char(2) NOT NULL,`province` int(5) NOT NULL,"
				+ "`fanscount` int(10) NOT NULL,`friendscount` int(8) NOT NULL,"
				+ "`weibocount` int(8) NOT NULL,`verify` tinyint(1) NOT NULL,"
				+ "`verifytype` int(3) NOT NULL,`verifiedreason` varchar(500) NOT NULL,"
				+ "`lastwbcreated` int(10) NOT NULL,PRIMARY KEY (`id`)"
				+ ") ENGINE=MyISAM DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;";
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
