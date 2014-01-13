package cc.pp.sina.analysis.userfans.interaction;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.HashMap;

import net.sf.json.JSONArray;
import cc.pp.sina.jdbc.UfiJDBC;

/**
 * Title: 对于没能获取信息的用户处理方式
 * @author wanggang
 * @version 1.1
 * @since 2013-05-27
 */
public class GetNoInfoForUser implements Serializable  {

	/**
	 * 默认序列化版本号
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 测试函数
	 * @param args
	 * @throws SQLException 
	 */
	public static void main(String[] args) throws SQLException {

		GetNoInfoForUser gnifu = new GetNoInfoForUser();
		String username = new String("1862087393");
		HashMap<String,String> result = gnifu.getInfo(username);
		System.out.println(result);

	}

	/**
	 * 获取信息，或者是生成信息
	 * @param username
	 * @return
	 * @throws SQLException
	 */
	public HashMap<String,String> getInfo(String username) throws SQLException {

		HashMap<String,String> result = new HashMap<String,String>();
		UfiJDBC myjdbc = new UfiJDBC("192.168.1.27");
		
		if (myjdbc.mysqlStatus()) {
			HashMap<String,String> emotionratio = new HashMap<String,String>();
			UfiUtils ufiUtils = new UfiUtils();
			emotionratio.put("negative", "0.0%");
			emotionratio.put("positive", "0.0%");
			emotionratio.put("neutral", "0.0%");
			JSONArray json = JSONArray.fromObject(emotionratio);
			myjdbc.insertUserIteractions(username + ufiUtils.getTodayDate(), ufiUtils.getTodayDate(), 0, json.toString());
			result.put("usernameday", username + ufiUtils.getTodayDate());
			result.put("allcount", "0");
			result.put("emotionratio", json.toString());
			myjdbc.sqlClose();
		}

		return result;
	}

}
