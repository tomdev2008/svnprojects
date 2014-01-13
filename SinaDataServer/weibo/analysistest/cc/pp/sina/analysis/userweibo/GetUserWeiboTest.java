package cc.pp.sina.analysis.userweibo;

import java.sql.SQLException;
import java.util.List;

import cc.pp.sina.jdbc.UserWeiboJDBC;

import com.sina.weibo.model.WeiboException;

public class GetUserWeiboTest {

	/**
	 * @param args
	 * @throws SQLException 
	 * @throws WeiboException 
	 * @throws WeiboException 
	 */
	public static void main(String[] args) throws SQLException, WeiboException {

		/****************本地环境******************/
		GetUserWeibos1 getUserWeibos = new GetUserWeibos1();
		UserWeiboJDBC myJdbc = new UserWeiboJDBC("127.0.0.1", "root", "");
		if (myJdbc.mysqlStatus()) {
			int num = 100;
			myJdbc.createUserWeiboTable("sinauserweiboinfo");
			List<String> sinausers = myJdbc.getSinaUsers("sinausers", num);
			String[] accesstoken = myJdbc.getAccessToken(num);
			for (int i = 0; i < sinausers.size(); i++) {
				getUserWeibos.dumpWeibosToDB(myJdbc, sinausers.get(i), accesstoken[i]);
			}
			myJdbc.deleteBatch("sinausers", sinausers);
			myJdbc.sqlClose();
		}

	}

}
