package cc.pp.sina.require.tianma;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;

import cc.pp.sina.jdbc.LocalJDBC;

import com.sina.weibo.api.Users;
import com.sina.weibo.model.User;
import com.sina.weibo.model.WeiboException;

public class GetUserFromWburl {

	/**
	 * @param args
	 * @throws WeiboException 
	 * @throws IOException 
	 * @throws SQLException 
	 */
	public static void main(String[] args) throws WeiboException, IOException, SQLException {

		LocalJDBC myjdbc = new LocalJDBC("127.0.0.1", "root", "");
		if (myjdbc.mysqlStatus()) {
			String accesstoken = "2.003s_buBdcZIJC2bfa65d0adJGsU4B";
			Users user = new Users();
			user.client.setToken(accesstoken);
			BufferedReader br = new BufferedReader(new FileReader(new File("testdata/sinauser.txt")));
			String uid = "";
			while ((uid = br.readLine()) != null) {

				if (uid.contains("e.weibo")) {
					User userinfo = user.showUserById(uid.substring(19, uid.lastIndexOf("/")));
					myjdbc.insertTempsinauserinfo("zsinauserinfo", userinfo.getId(), userinfo.getScreenName(),
							userinfo.getGender(), userinfo.getLocation(), userinfo.getVerifiedType());
				} else {
					User userinfo = user.showUserById(uid.substring(17, uid.lastIndexOf("/")));
					myjdbc.insertTempsinauserinfo("zsinauserinfo", userinfo.getId(), userinfo.getScreenName(),
							userinfo.getGender(), userinfo.getLocation(), userinfo.getVerifiedType());
				}
			}
			br.close();
			myjdbc.sqlClose();
		}

	}

}
