package cc.pp.tencent.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

public class UidJDBC extends JDBC {

	public UidJDBC(String ip) {
		super(ip);
	}

	public UidJDBC(String ip, String user, String password) {
		super(ip, user, password);
	}

	/**
	 * 创建十张表格
	 * @param tablename
	 * @throws SQLException
	 */
	public void createTable(String tablename) throws SQLException {

		String sql = new String("CREATE TABLE " + tablename + " (" +
				"`id` int(10) NOT NULL AUTO_INCREMENT," +
				"`username` char(50) NOT NULL, PRIMARY KEY (`id`)" +
				") ENGINE=MyISAM DEFAULT CHARSET=utf8 AUTO_INCREMENT=1;");
		Statement statement = this.conn.createStatement();
		statement.executeUpdate(sql);
		statement.close();
	}

	public void createTablenames(String tablename) throws SQLException {

		String sql = new String("CREATE TABLE " + tablename + " (" +
				"`id` int(10) NOT NULL AUTO_INCREMENT," +
				"`tablename` char(30) NOT NULL, PRIMARY KEY (`id`)" +
				") ENGINE=MyISAM DEFAULT CHARSET=utf8 AUTO_INCREMENT=1;");
		Statement statement = this.conn.createStatement();
		statement.executeUpdate(sql);
		statement.close();
	}
	
	public void insertTablename(String tablename) throws SQLException {
		
		String sql = new String("INSERT INTO `tablenames` (`tablename`) VALUES (\"" + tablename + "\")");
		Statement statement = this.conn.createStatement();
		statement.execute(sql);
		statement.close();
	}
	
	public String getLastTablename() throws SQLException {
		
		String result = new String();
		String sql = new String("SELECT `tablename` FROM `tablenames` ORDER BY `id` DESC LIMIT 1");
		Statement statement = this.conn.createStatement();
		ResultSet rs = statement.executeQuery(sql);
		if (rs.next()) {
			result = rs.getString("tablename");
			rs.close();
			statement.close();
			return result;
		} else {
			rs.close();
			statement.close();
			return null;
		}
		
	}
	
	public int getMaxId(String tablename) throws SQLException {

		int max = 0;
		String sql = new String("SELECT MAX(`id`) `id` FROM " + tablename);
		Statement statement = this.conn.createStatement();
		ResultSet rs = statement.executeQuery(sql);
		if (rs.next()) {
			max = rs.getInt("id");
		}
		rs.close();
		statement.close();

		return max;
	}
	
	public HashMap<String,String> getUids(String tablename) throws SQLException {

		HashMap<String,String> result = new HashMap<String,String>();
		String sql = new String("SELECT `username` FROM " + tablename);
		Statement statement = this.conn.createStatement();
		ResultSet rs = statement.executeQuery(sql);
		while (rs.next()) {
			result.put(rs.getString("username"), "OK");
		}
		rs.close();
		statement.close();

		return result;
	}

	public HashMap<Integer,String> getAllUids(String tablename) throws SQLException {

		HashMap<Integer,String> result = new HashMap<Integer,String>();
		String sql = new String("SELECT `username` FROM " + tablename);
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
	
	public HashMap<Integer,String> getAllUids(String tablename, int num) throws SQLException {

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

	public void insertUids(String tablename, String username) throws SQLException {

		String sql = new String("INSERT INTO " + tablename + " (`username`) VALUES (\"" + username + "\")");
		Statement statement = this.conn.createStatement();
		statement.execute(sql);
		statement.close();
	}

	public void insertUnusedUids(String tablename, String username) throws SQLException {

		String sql = new String("INSERT INTO " + tablename + " (`username`) VALUES (\"" + username + "\")");
		Statement statement = this.conn.createStatement();
		statement.execute(sql);
		statement.close();
	}

	public void deleteUids(String tablename, String username) throws SQLException {

		String sql = new String("DELETE FROM " + tablename + " WHERE `username` = \"" + username + "\"");
		Statement statement = this.conn.createStatement();
		statement.execute(sql);
		statement.close();		
	}

}
