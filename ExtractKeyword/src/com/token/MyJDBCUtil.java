package com.token;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.mysql.jdbc.Statement;

public class MyJDBCUtil {

	public static List<String> GetTokens() {
		
		String databaseName = "pp_fenxi";
		String url = "jdbc:mysql://192.168.1.153:3306/" + databaseName;
		String user = "pp_fenxi"; 
		String password = "q#tmuYzC@sqB6!ok@sHd";
		String driver = "com.mysql.jdbc.Driver";
		List<String> tokens = new ArrayList<String>();
		
		try {
			Class.forName(driver);
			java.sql.Connection conn = DriverManager.getConnection(url, user, password);
			Statement statement = (Statement) conn.createStatement();
			String sql = "select * from mytokens";
			ResultSet rs = statement.executeQuery(sql);
			while (rs.next()) {
				tokens.add(rs.getString("token"));
			}
			conn.close();
			return tokens;
		} catch(ClassNotFoundException e) {
			e.printStackTrace();
		} catch(SQLException e) {
			e.printStackTrace();
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static boolean isToken(List<String> tokens, String token) {
		Iterator<String> iterator = tokens.iterator();
		while (iterator.hasNext()) {
			String tk = iterator.next();
			if(token.equals(tk)) return true;
		}
		return false;
	}
	
}
