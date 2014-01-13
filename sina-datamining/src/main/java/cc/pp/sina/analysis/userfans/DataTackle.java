package cc.pp.sina.analysis.userfans;

import java.sql.SQLException;
import java.util.HashMap;

import cc.pp.sina.jdbc.UserJDBC;

public class DataTackle {

	/**
	 * @param args
	 * @throws SQLException 
	 */
	public static void main(String[] args) throws SQLException {

		DataTackle dt = new DataTackle();
		HashMap<Integer, String> uids = dt.getUids();
		dt.updateUids(uids);
	}
	
	public HashMap<Integer,String> getUids() throws SQLException {
		
		UserJDBC mysql = new UserJDBC("192.168.1.154");
		if (mysql.mysqlStatus()) {
			HashMap<Integer,String> result = mysql.getUsers();
			mysql.sqlClose();
			return result;
		} else {
			return null;
		}
		
	}
	
	public void updateUids(HashMap<Integer,String> uids) throws SQLException {
		
		UserJDBC mysql = new UserJDBC("192.168.1.27");
		if (mysql.mysqlStatus()) {
			for (int i = 0; i < uids.size(); i++) {
				mysql.updateUser(uids.get(i));
			}
			mysql.sqlClose();
		}		
	}

}
