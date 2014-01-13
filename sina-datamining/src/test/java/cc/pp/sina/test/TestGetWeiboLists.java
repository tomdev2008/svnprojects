package cc.pp.sina.test;

import java.sql.SQLException;

import cc.pp.sina.utils.GetWeiboLists;

public class TestGetWeiboLists {

	/**
	 * @param args
	 * @throws SQLException 
	 */
	public static void main(String[] args) throws SQLException {

		GetWeiboLists getwb = new GetWeiboLists("192.168.1.27");
		getwb.getWbLists();
	}

}
