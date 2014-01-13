package cc.pp.sina.analysis.userweibo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;

import cc.pp.sina.jdbc.UserWeiboJDBC;

public class WeiboToDbTest {

	/**
	 * @param args
	 * @throws SQLException 
	 * @throws IOException 
	 */
	public static void main(String[] args) throws SQLException, IOException {

		UserWeiboJDBC myJdbc = new UserWeiboJDBC("127.0.0.1", "root", "");
		if (myJdbc.mysqlStatus()) {
			BufferedReader bw = new BufferedReader(new FileReader(new File("testdata/weibo.txt")));
			String str = bw.readLine();
			bw.close();

			myJdbc.insertTest("test", str);
			myJdbc.sqlClose();
		}

	}

}
