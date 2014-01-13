package cc.pp.weirenwu.core;

import java.sql.SQLException;
import java.util.List;

import cc.pp.spider.sql.LocalJDBC;

public class TablesUnion {

	public static void main(String[] args) throws SQLException {

		LocalJDBC myjdbc = new LocalJDBC("127.0.0.1", "root", "root");
		myjdbc.mysqlStatus();

		List<BozhuInfo> bozhuInfos = myjdbc.selectBozhuInfos("weirenwu_zhuanfa");
		for (BozhuInfo bozhuInfo : bozhuInfos) {
			myjdbc.updateBozhuInfos("weirenwu_all", "zhuanfaprice", bozhuInfo.getCanliprice(),
					bozhuInfo.getUsername());
		}
		System.out.println("OK");

		myjdbc.sqlClose();
	}

}
