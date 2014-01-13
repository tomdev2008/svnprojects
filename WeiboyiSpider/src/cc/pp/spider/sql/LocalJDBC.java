package cc.pp.spider.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import cc.pp.spider.common.Params;
import cc.pp.spider.domain.UserInfo;
import cc.pp.weirenwu.core.BozhuInfo;

public class LocalJDBC {

	private final Params params = new Params();
	private Connection conn;
	private final String driver = "com.mysql.jdbc.Driver";
	private String url;
	private String user;
	private String password;

	/**
	 * 线上环境
	 */
	public LocalJDBC() {
		try {
			this.url = "jdbc:mysql://" + params.getDbProps("ip")
					+ ":3306/pp_fenxi?useUnicode=true&characterEncoding=utf-8";
			this.user = params.getDbProps("db_user");
			this.password = params.getDbProps("db_password");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 本地环境
	 */
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

	public void createUserinfoTable(String tablename) throws SQLException {

		String sql = "CREATE TABLE " + tablename + " (`id` int(10) NOT NULL AUTO_INCREMENT,"
				+ "`type` char(10) NOT NULL,`cate` char(10) NOT NULL,"
				+ "`nickname` char(50) NOT NULL,`priceurl` char(250) NOT NULL,"
				+ "`domainname` char(50) NOT NULL,PRIMARY KEY (`id`)"
				+ ") ENGINE=MyISAM DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;";
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

	public void insertUserInfo(String tablename, String type, String cate, String nickname, String priceurl,
			String domainname) throws SQLException {

		String sql = "INSERT INTO " + tablename
				+ " (`type`,`cate`,`nickname`,`priceurl`,`domainname`) VALUES (?,?,?,?,?)";
		PreparedStatement pstmt = null;
		try {
			pstmt = this.conn.prepareStatement(sql);
			pstmt.setString(1, type);
			pstmt.setString(2, cate);
			pstmt.setString(3, nickname);
			pstmt.setString(4, priceurl);
			pstmt.setString(5, domainname);
			pstmt.execute();
		} finally {
			if (pstmt != null) {
				pstmt.close();
			}
		}
	}

	public List<UserInfo> getWeiboyi(String tablename, String type, //
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

	public void updatePrices(String tablename, String cate, String domainname, float hardrepprice, //
			float hardoriprice, float softrepprice, float softoriprice) throws SQLException {

		String sql = "UPDATE " + tablename + " SET `cate`=?,`hardrepprice`=?,`hardoriprice`=?," + //
				"`softrepprice`=?,`softoriprice`=? WHERE `domainname`=?";
		PreparedStatement pstmt = null;
		try {
			pstmt = this.conn.prepareStatement(sql);
			pstmt.setString(1, cate);
			pstmt.setFloat(2, hardrepprice);
			pstmt.setFloat(3, hardoriprice);
			pstmt.setFloat(4, softrepprice);
			pstmt.setFloat(5, softoriprice);
			pstmt.setString(6, domainname);
			pstmt.executeUpdate();
		} finally {
			if (pstmt != null) {
				pstmt.close();
			}
		}
	}

	public String[] getCateAndAvereadcount(String tablename, String domainname) throws SQLException {

		String[] result = new String[2];
		String sql = "SELECT `cate`,`avereadcount` FROM " + tablename + //
				" WHERE `domainname` = \"" + domainname + "\"";
		PreparedStatement pstmt = null;
		try {
			pstmt = this.conn.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				result[0] = rs.getString(1);
				result[1] = Integer.toString(rs.getInt(2));
			}
		} finally {
			if (pstmt != null) {
				pstmt.close();
			}
		}

		return result;
	}

	public void inserWeiRenWu(String tablename, BozhuInfo bozhuInfo) throws SQLException {

		String sql = "INSERT INTO " + tablename
				+ " (`username`,`nickname`,`influence`,`cate`,`canliprice`,`fanscount`,`jiedanratio`) " + "VALUES (\""
				+ bozhuInfo.getUsername() + "\",\"" + bozhuInfo.getNickname() + "\",\"" + bozhuInfo.getInfluence()
				+ "\",\"" + bozhuInfo.getCate()
				+ "\",\"" + bozhuInfo.getCanliprice() + "\",\"" + bozhuInfo.getFanscount() + "\",\""
				+ bozhuInfo.getJiedanratio() + "\")";
		Statement statement = this.conn.createStatement();
		statement.executeUpdate(sql);
		statement.close();
	}

	public List<BozhuInfo> selectBozhuInfos(String tablename) throws SQLException {

		List<BozhuInfo> result = new ArrayList<BozhuInfo>();
		String sql = "SELECT * FROM " + tablename;
		Statement statement = this.conn.createStatement();
		ResultSet rs = statement.executeQuery(sql);
		while (rs.next()) {
			BozhuInfo temp = new BozhuInfo();
			temp.setUsername(rs.getString("username"));
			temp.setNickname(rs.getString("nickname"));
			temp.setInfluence(rs.getString("influence"));
			temp.setCate(rs.getString("cate"));
			temp.setCanliprice(rs.getString("canliprice"));
			temp.setFanscount(rs.getString("fanscount"));
			temp.setJiedanratio(rs.getString("jiedanratio"));
			result.add(temp);
		}
		rs.close();
		statement.close();

		return result;
	}

	public void updateBozhuInfos(String tablename, String key, String value, String username) throws SQLException {

		String sql = "UPDATE " + tablename + " SET " + key + " = \"" + value + "\" WHERE `username` = \"" + username
				+ "\"";
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
