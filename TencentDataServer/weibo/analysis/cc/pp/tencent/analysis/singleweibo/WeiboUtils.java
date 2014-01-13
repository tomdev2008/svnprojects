package cc.pp.tencent.analysis.singleweibo;

import java.sql.SQLException;

import cc.pp.tencent.jdbc.WeiboJDBC;

/**
 * Title: 微博分析工具类
 * @author wanggang
 * @version 1.1
 * @since 2013-05-27
 */
public class WeiboUtils {
	
	/**
	 * @获取accesstoken
	 * @return
	 * @throws SQLException
	 */
	public static String[] getAccessToken(String ip) throws SQLException {

		WeiboJDBC jdbc = new WeiboJDBC(ip);
		String[] token = null;
		if (jdbc.mysqlStatus()) {
			token = jdbc.getAccessToken();
			jdbc.sqlClose();
		}

		return token;
	}

	/**
	 * @获取wid
	 * @param url
	 * @return
	 */
	public static String getWid(String url) {
		//http://t.qq.com/p/t/197562129592354
		String wid = url.substring(20);
		
		return wid;
	}

}
