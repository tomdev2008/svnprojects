package cc.pp.weixin.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Title: 爬虫JDBC类
 * @author wanggang
 * @version 1.1
 * @since 2013-05-27
 */
public class LocalJDBC {

	private Connection conn;
	private final String driver = "com.mysql.jdbc.Driver";
	private String url = new String();
	private final String user = "root";
	private final String password = "";

	/**
	 * @ construct
	 * @param ip
	 */
	public LocalJDBC(String databasename) {
		this.url = "jdbc:mysql://127.0.0.1:3306/" + databasename + "?useUnicode=true&characterEncoding=gbk";
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
	 * 插入微信主信息
	 * @param tablename
	 * @param name
	 * @param wid
	 * @param description
	 * @throws SQLException
	 */
	public void insertWeixinUserInfo(String tablename, String name, String cate, String wid, String description)
			throws SQLException {

		String sql = "INSERT INTO " + tablename + " (`name`,`cate`,`wid`,`description`) VALUES (?,?,?,?)";
		PreparedStatement pstmt = null;
		try {
			pstmt = this.conn.prepareStatement(sql);
			pstmt.setString(1, name);
			pstmt.setString(2, cate);
			pstmt.setString(3, wid);
			if (description == null) {
				pstmt.setString(4, "0");
			} else {
				pstmt.setString(4, description);
			}
			pstmt.executeUpdate();
		} finally {
			if (pstmt != null) {
				pstmt.close();
			}
		}
	}

	public void insertWeixinUserInfo(String tablename, String name, String cate, String wid, String description,
			String fanscount) throws SQLException {

		String sql = "INSERT INTO " + tablename + " (`name`,`cate`,`wid`,`description`,`fanscount`) VALUES (?,?,?,?,?)";
		PreparedStatement pstmt = null;
		try {
			pstmt = this.conn.prepareStatement(sql);
			pstmt.setString(1, name);
			pstmt.setString(2, cate);
			pstmt.setString(3, wid);
			if (description == null) {
				pstmt.setString(4, "0");
			} else {
				pstmt.setString(4, description);
			}
			pstmt.setString(5, fanscount);
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

