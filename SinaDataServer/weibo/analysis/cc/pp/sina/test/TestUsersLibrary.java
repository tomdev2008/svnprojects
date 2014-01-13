package cc.pp.sina.test;

import java.sql.SQLException;
import java.util.HashMap;

import cc.pp.sina.analysis.userclassfy.UsersLibrary;
import cc.pp.sina.jdbc.UserClassfyJDBC;

import com.sina.weibo.model.WeiboException;

public class TestUsersLibrary {

	/**
	 * @param args
	 * @throws SQLException 
	 * @throws WeiboException 
	 */
	public static void main(String[] args) throws WeiboException, SQLException {

		UsersLibrary ui = new UsersLibrary();
		UserClassfyJDBC ucjdbc = new UserClassfyJDBC("192.168.1.189");
		long time = System.currentTimeMillis() / 1000;
		if (ucjdbc.mysqlStatus()) {
			int i = 0;
			while (i++ < 100) {
				System.out.println(i);
				HashMap<Integer, String> username = ucjdbc.getUsernames("sinausers", 1);
				ucjdbc.deleteUsernames("sinausers", username.get(0));
				try {
					ui.userAllParams(ucjdbc, "sinalibrary", username.get(0));
				} catch (SQLException e) {
					continue;
				}
			}
			ucjdbc.sqlClose();
		}
		System.out.println(System.currentTimeMillis() / 1000 - time);
	}

}
