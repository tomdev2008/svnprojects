package cc.pp.sina.spider;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map.Entry;

import cc.pp.sina.domain.RepWeibo;
import cc.pp.sina.sql.SpiderJDBC;

public class test {

	/**
	 * @param args
	 * @throws SQLException 
	 */
	public static void main(String[] args) throws SQLException {

		//		int[] ips = { 26, 27, 28, 29, 30, 31, 32, 50, 34, 51, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45 };
		//		int[] ips = { 27, 28, 29, 30, 31 };
		//		SpiderJDBC myJDBC = new SpiderJDBC("192.168.1.26");
		//		if (myJDBC.mysqlStatus()) {
		//			for (int i = 0; i < ips.length; i++) {
		//				SpiderJDBC spiderJDBC = new SpiderJDBC("192.168.1." + ips[i]);
		//				if (spiderJDBC.mysqlStatus()) {
		//					List<RepWeibo> weiboinfos = spiderJDBC.getRepWeiboInfo("singleweiboinfo");
		//					for (RepWeibo temp : weiboinfos) {
		//						myJDBC.insertRepWeiboInfo("singleweiboinfo", temp.getDomainname(), temp.getNickname(), //
		//								temp.getWeibotext(), temp.getCreateat(), temp.getSource());
		//					}
		//					spiderJDBC.sqlClose();
		//				}
		//			}
		//			myJDBC.sqlClose();
		//		}

		SpiderJDBC spiderJDBC = new SpiderJDBC("127.0.0.1", "root", "");
		if (spiderJDBC.mysqlStatus()) {
			HashMap<String, RepWeibo> weiboinfos = spiderJDBC.getRepWeiboInfo("singleweiboinfo2");
			for (Entry<String, RepWeibo> infos : weiboinfos.entrySet()) {
				RepWeibo temp = infos.getValue();
				spiderJDBC.insertRepWeiboInfo("singleweiboinfo", temp.getDomainname(), temp.getNickname(), //
						temp.getWeibotext(), temp.getCreateat(), temp.getSource());
			}
			spiderJDBC.sqlClose();
		}

	}

}
