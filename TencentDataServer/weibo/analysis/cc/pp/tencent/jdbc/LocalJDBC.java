package cc.pp.tencent.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONArray;
import cc.pp.tencent.analysis.userweibo.spider.UserInfo;

import com.tencent.weibo.dao.impl.Comp;
import com.tencent.weibo.dao.impl.Edu;
import com.tencent.weibo.dao.impl.Tag;

public class LocalJDBC {

	private Connection conn;
	private final String driver = "com.mysql.jdbc.Driver";
	private String url = "";
	private final String user = "root";
	private final String password = "";

	public LocalJDBC() {
		this.url = "jdbc:mysql://127.0.0.1:3306/pp_fenxi?useUnicode=true&characterEncoding=gbk";
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
		String sql = new String(
				"SELECT `access_token`,`token_secret` FROM `member_bind_tencent` ORDER BY `expires` LIMIT " + nums);
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
	 * 获取token数据，并更新
	 * @return
	 * @throws SQLException
	 */
	public String[] getAccessToken() throws SQLException {

		String[] result = this.getAccessTokenone();
		this.updateAccessToken(result[0]);

		return result;
	}

	/**
	 * 获取token数据
	 * @return
	 * @throws SQLException
	 */
	public String[] getAccessTokenone() throws SQLException {

		String[] result = new String[2];
		String sql = new String(
				"SELECT `access_token`,`token_secret` FROM `member_bind_tencent` ORDER BY `expires` LIMIT 1");
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
	 * 更新token
	 * @param token
	 * @throws SQLException
	 */
	public void updateAccessToken(String token) throws SQLException {
		long time = System.currentTimeMillis() / 1000;
		String sql = new String("UPDATE `member_bind_tencent` SET `expires` = " + time + " WHERE `access_token` = \""
				+ token + "\"");
		Statement statement = this.conn.createStatement();
		statement.executeUpdate(sql);
		statement.close();
	}

	/**
	 * 删除token
	 * @param token
	 * @throws SQLException
	 */
	public void deleteAccessToken(String token) throws SQLException {
		String sql = new String("DELETE FROM `member_bind_tencent` WHERE `access_token` = \"" + token + "\"");
		Statement statement = this.conn.createStatement();
		statement.execute(sql);
		statement.close();
	}

	public void insertUsersInfo(String tablename, String username, String nickname, int isvip, int fanscount,
			int friendscount, int exp, int isrealname, int level, int sex) throws SQLException {

		String sql = "INSERT INTO " + tablename + " (`username`,`nickname`,`isvip`,`fanscount`, " + //
				"`friendscount`,`exp`,`isrealname`,`level`,`sex`) VALUES (?,?,?,?,?,?,?,?,?)";
		PreparedStatement pstmt = null;
		try {
			pstmt = this.conn.prepareStatement(sql);
			pstmt.setString(1, username);
			pstmt.setString(2, nickname);
			pstmt.setInt(3, isvip);
			pstmt.setInt(4, fanscount);
			pstmt.setInt(5, friendscount);
			pstmt.setInt(6, exp);
			pstmt.setInt(7, isrealname);
			pstmt.setInt(8, level);
			pstmt.setInt(9, sex);
			pstmt.executeUpdate();
		} finally {
			if (pstmt != null) {
				pstmt.close();
			}
		}
	}

	public void insertUsersInfo(String tablename, String username, String nickname, String description, int isvip,
			int fanscount, int friendscount, int weibocount, int exp, int isrealname, int level, int sex,
			int industrycode, int isent, String province, long lastwbcreated, String verifiedreason)
			throws SQLException {

		String sql = "INSERT INTO " + tablename + " (`username`,`nickname`,`description`,`isvip`," + //
				" `fanscount`,`friendscount`,`weibocount`,`exp`,`isrealname`,`level`,`sex`," + //
				"`industrycode`,`isent`,`province`,`lastwbcreated`,`verifiedreason`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		PreparedStatement pstmt = null;
		try {
			pstmt = this.conn.prepareStatement(sql);
			pstmt.setString(1, username);
			pstmt.setString(2, nickname);
			pstmt.setString(3, description);
			pstmt.setInt(4, isvip);
			pstmt.setInt(5, fanscount);
			pstmt.setInt(6, friendscount);
			pstmt.setInt(7, weibocount);
			pstmt.setInt(8, exp);
			pstmt.setInt(9, isrealname);
			pstmt.setInt(10, level);
			pstmt.setInt(11, sex);
			pstmt.setInt(12, industrycode);
			pstmt.setInt(13, isent);
			if (province.length() > 0) {
				pstmt.setString(14, province);
			} else {
				pstmt.setString(14, "0");
			}

			pstmt.setLong(15, lastwbcreated);
			pstmt.setString(16, verifiedreason);
			pstmt.executeUpdate();
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
	public List<String> getTencentUsers(String tablename, int num) throws SQLException {

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

	public void insertUnusedUsers(String tablename, String username) throws SQLException {

		String sql = new String("INSERT INTO " + tablename + " (`username`) " + "VALUES (\"" + username + "\")");
		Statement statement = this.conn.createStatement();
		statement.execute(sql);
		statement.close();
	}

	/**
	 * 插入腾讯用户的基础信息
	 * @param tablename
	 * @throws SQLException
	 */
	public void inserUserBaseInfo(String tablename, String name, String nick, String openid, //
			int fansnum, int favnum, int idolnum, int tweetnum, int birth_day, int birth_month, //
			int birth_year, String city_code, List<Comp> comp, String country_code, List<Edu> edu, //
			String email, int exp, String head, String homecity_code, String homecountry_code, //
			String homepage, String homeprovince_code, String hometown_code, String https_head, //
			int industry_code, String introduction, int isent, int isrealname, int isvip, //
			int level, String location, int mutual_fans_num, String province_code, long regtime, //
			int send_private_flag, int sex, List<Tag> tag, String verifyinfo) throws SQLException {
		
		long time = System.currentTimeMillis() / 1000;
		String sql = "INSERT INTO " + tablename + " (`name`,`nick`,`openid`,`fansnum`,`favnum`," + //
				"`idolnum`,`tweetnum`,`birth_day`,`birth_month`,`birth_year`,`city_code`,`comp`," + //
				"`country_code`,`edu`,`email`,`exp`,`head`,`homecity_code`,`homecountry_code`," + //
				"`homepage`,`homeprovince_code`,`hometown_code`,`https_head`,`industry_code`," + //
				"`introduction`,`isent`,`isrealname`,`isvip`,`level`,`location`,`mutual_fans_num`," + //
				"`province_code`,`regtime`,`send_private_flag`,`sex`,`tag`,`verifyinfo`,`lasttime`) " + //
				"VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		PreparedStatement pstmt = null;
		try {
			pstmt = this.conn.prepareStatement(sql);
			pstmt.setString(1, name);
			pstmt.setString(2, nick);
			pstmt.setString(3, openid);
			pstmt.setInt(4, fansnum);
			pstmt.setInt(5, favnum);
			pstmt.setInt(6, idolnum);
			pstmt.setInt(7, tweetnum);
			pstmt.setInt(8, birth_day);
			pstmt.setInt(9, birth_month);
			pstmt.setInt(10, birth_year);
			pstmt.setString(11, city_code);
			pstmt.setString(12, JSONArray.fromObject(comp).toString());
			pstmt.setString(13, country_code);
			pstmt.setString(14, JSONArray.fromObject(edu).toString());
			pstmt.setString(15, email);
			pstmt.setInt(16, exp);
			pstmt.setString(17, head);
			pstmt.setString(18, homecity_code);
			pstmt.setString(19, homecountry_code);
			pstmt.setString(20, homepage);
			pstmt.setString(21, homeprovince_code);
			pstmt.setString(22, hometown_code);
			pstmt.setString(23, https_head);
			pstmt.setInt(24, industry_code);
			pstmt.setString(25, introduction);
			pstmt.setInt(26, isent);
			pstmt.setInt(27, isrealname);
			pstmt.setInt(28, isvip);
			pstmt.setInt(29, level);
			pstmt.setString(30, location);
			pstmt.setInt(31, mutual_fans_num);
			pstmt.setString(32, province_code);
			pstmt.setLong(33, regtime);
			pstmt.setInt(34, send_private_flag);
			pstmt.setInt(35, sex);
			pstmt.setString(36, JSONArray.fromObject(tag).toString());
			pstmt.setString(37, verifyinfo);
			pstmt.setLong(38, time);
			pstmt.execute();
		} finally {
			if (pstmt != null) {
				pstmt.close();
			}
		}
	}

	public void insertTempsinauserinfo(String tablename, String username, String nickname, int gender,
			String province, String verifytype) throws SQLException {

		String sql = "INSERT INTO " + tablename
				+ " (`username`,`nickname`,`gender`,`province`,`verifytype`) VALUES (?,?,?,?,?)";
		PreparedStatement pstmt = null;
		try {
			pstmt = this.conn.prepareStatement(sql);
			pstmt.setString(1, username);
			pstmt.setString(2, nickname);
			pstmt.setInt(3, gender);
			pstmt.setString(4, province);
			pstmt.setString(5, verifytype);
			pstmt.execute();
		} finally {
			if (pstmt != null) {
				pstmt.close();
			}
		}
	}

	public void insertReadInfo(String tablename, String type, String cate, String nickname, //
			String domainname, int avereadcount, String priceurl) throws SQLException {

		String sql = "INSERT INTO " + tablename
				+ " (`type`,`cate`,`nickname`,`domainname`,`avereadcount`,`priceurl`) VALUES (?,?,?,?,?,?)";
		PreparedStatement pstmt = null;
		try {
			pstmt = this.conn.prepareStatement(sql);
			pstmt.setString(1, type);
			pstmt.setString(2, cate);
			pstmt.setString(3, nickname);
			pstmt.setString(4, domainname);
			pstmt.setInt(5, avereadcount);
			pstmt.setString(6, priceurl);
			pstmt.execute();
		} finally {
			if (pstmt != null) {
				pstmt.close();
			}
		}
	}

	public List<UserInfo> getWeiboyiTencents(String tablename, String type, //
			int num) throws SQLException {

		List<UserInfo> result = new ArrayList<UserInfo>();
		String sql = "SELECT `type`,`cate`,`nickname`,`domainname`,`priceurl` FROM " //
				+ tablename + " WHERE `type` = \"" + type + "\" LIMIT " + num;
		PreparedStatement pstmt = null;
		try {
			pstmt = this.conn.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				UserInfo temp = new UserInfo();
				temp.setType(rs.getString(1));
				temp.setCate(rs.getString(2));
				temp.setNickname(rs.getString(3));
				temp.setDomainname(rs.getString(4));
				temp.setPriceurl(rs.getString(5));
				result.add(temp);
			}
		} finally {
			if (pstmt != null) {
				pstmt.close();
			}
		}

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
