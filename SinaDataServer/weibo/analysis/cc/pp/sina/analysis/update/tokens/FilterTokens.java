package cc.pp.sina.analysis.update.tokens;

import java.sql.SQLException;

import cc.pp.sina.jdbc.WeiboJDBC;

public class FilterTokens {

	/**
	 * @param args
	 * @throws SQLException 
	 */
	public static void main(String[] args) throws SQLException {

		WeiboJDBC weibojdbc = new WeiboJDBC("192.168.1.27");
		if (weibojdbc.mysqlStatus()) {
			for (int i = 0; i < 100; i++) {
				System.out.println(i);
				weibojdbc.getAccessToken();
			}
			weibojdbc.sqlClose();
		}
	}

}
