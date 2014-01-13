package cc.pp.sina.analysis.userweibo;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.HashMap;

import cc.pp.sina.jdbc.UserWeiboJDBC;

import com.sina.weibo.model.WeiboException;

public class UserWeibo implements Serializable {
	
	/**
	 * 默认的序列化版本号
	 */
	private static final long serialVersionUID = 1L;
	
	private String ip = "";

	public UserWeibo(String ip) {
		this.ip = ip;
	}

	/**
	 * @param args
	 * @throws WeiboException 
	 * @throws SQLException 
	 */
	public static void main(String[] args) throws SQLException, WeiboException {

		UserWeibo uweibo = new UserWeibo("192.168.1.184");
		uweibo.userWeibo();

	}
	
	public void userWeibo() throws SQLException, WeiboException {
		
		UWBAnalysis uwa = new UWBAnalysis(ip);
		UserWeiboJDBC usermysql = new UserWeiboJDBC(ip);
		if (usermysql.mysqlStatus()) {
			int i = 0;
			while (i++ < 700) {
				HashMap<Integer,String> username = usermysql.getUsernames("sinausers", 1);
				usermysql.deleteUsernames("sinausers", username.get(0));
				try {
					uwa.analysis(username.get(0));
				} catch (RuntimeException e) {
					//
				}
			}
			usermysql.sqlClose();
		}
	}

}
