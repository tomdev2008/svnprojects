package cc.pp.sina.analysis.user.baseinfo;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.HashMap;

import cc.pp.sina.jdbc.UbiaJDBC;

/**
 * Title: 把结果转移到154数据同步服务器上
 * @author wanggang
 * @version 1.1
 * @since 2013-05-27
 */
public class MoveResult implements Serializable {

	/**
	 * 默认序列化版本号
	 */
	private static final long serialVersionUID = 1L;
	
	private String ip = "";
	
	public MoveResult(String ip) {
		this.ip = ip;
	}

	/**
	 * 测试函数
	 * @param args
	 * @throws SQLException 
	 */
	public static void main(String[] args) throws SQLException {

		MoveResult mr = new MoveResult("192.168.1.27");
		mr.moveResult();
	}
	
	/**
	 * 转移数据
	 * @throws SQLException
	 */
	public void moveResult() throws SQLException {
		
		UbiaUtils ubiaUtils = new UbiaUtils();
		/******************获取数据***********************/
		HashMap<String,String> todaydata = new HashMap<String,String>();
		UbiaJDBC jdbc = new UbiaJDBC(this.ip);
		if (jdbc.mysqlStatus()) {
			todaydata = jdbc.getTodayResult("sinauserinforesult", ubiaUtils.getTodayDate());
			jdbc.sqlClose();
		}
//		System.out.println(todaydata.size());
		/******************插入数据***********************/
		String followers = new String();
		String friends = new String();
		String statuses = new String();
		String followersadd = new String();
		String friendsadd = new String();
		String statusesadd = new String();
		String followersaddratio = new String();
		String friendsaddratio = new String();
		String statusesaddratio = new String();
		String info = new String();
		String usernameday = new String();
		
		UbiaJDBC jdbc1 = new UbiaJDBC("192.168.1.154"); // 转移服务器
		if (jdbc1.mysqlStatus()) {
			for (int i = 0; i < todaydata.size(); i++) {	
				info = todaydata.get(Integer.toString(i));
				usernameday = info.substring(0, info.indexOf(","));
				info = info.substring(info.indexOf(",") + 1);
				followers = info.substring(0, info.indexOf(","));
				info = info.substring(info.indexOf(",") + 1);
				friends = info.substring(0, info.indexOf(","));
				info = info.substring(info.indexOf(",") + 1);
				statuses = info.substring(0, info.indexOf(","));
				info = info.substring(info.indexOf(",") + 1);
				followersadd = info.substring(0, info.indexOf(","));
				info = info.substring(info.indexOf(",") + 1);
				friendsadd = info.substring(0, info.indexOf(","));
				info = info.substring(info.indexOf(",") + 1);
				statusesadd = info.substring(0, info.indexOf(","));
				info = info.substring(info.indexOf(",") + 1);
				followersaddratio = info.substring(0, info.indexOf(","));
				info = info.substring(info.indexOf(",") + 1);
				friendsaddratio = info.substring(0, info.indexOf(","));
				statusesaddratio = info.substring(info.indexOf(",") + 1);
				
				jdbc1.insertTodayResult1("sinauserinforesult", 
						usernameday, usernameday.substring(0,usernameday.length()-8), 
						usernameday.substring(usernameday.length()-8), 
						Integer.parseInt(followers), Integer.parseInt(friends), Integer.parseInt(statuses), 
						Integer.parseInt(followersadd), Integer.parseInt(friendsadd), Integer.parseInt(statusesadd), 
						followersaddratio, friendsaddratio, statusesaddratio);
				
			}
			jdbc1.sqlClose();
		}
	}
	
	/**
	 * 转移数据
	 * @throws SQLException
	 */
	public void moveResultAll() throws SQLException {
		
		/******************获取数据***********************/
		HashMap<String,String> todaydata = new HashMap<String,String>();
		UbiaJDBC jdbc = new UbiaJDBC(this.ip);
		if (jdbc.mysqlStatus()) {
			todaydata = jdbc.getAllResult("sinauserinforesult");
			jdbc.sqlClose();
		}
		System.out.println(todaydata.size());
		/******************插入数据***********************/
		String followers = new String();
		String friends = new String();
		String statuses = new String();
		String followersadd = new String();
		String friendsadd = new String();
		String statusesadd = new String();
		String followersaddratio = new String();
		String friendsaddratio = new String();
		String statusesaddratio = new String();
		String info = new String();
		String usernameday = new String();
		
		UbiaJDBC jdbc1 = new UbiaJDBC("192.168.1.154"); // 转移服务器
		if (jdbc1.mysqlStatus()) {
			for (int i = 0; i < todaydata.size(); i++) {	
				info = todaydata.get(Integer.toString(i));
				usernameday = info.substring(0, info.indexOf(","));
				info = info.substring(info.indexOf(",") + 1);
				followers = info.substring(0, info.indexOf(","));
				info = info.substring(info.indexOf(",") + 1);
				friends = info.substring(0, info.indexOf(","));
				info = info.substring(info.indexOf(",") + 1);
				statuses = info.substring(0, info.indexOf(","));
				info = info.substring(info.indexOf(",") + 1);
				followersadd = info.substring(0, info.indexOf(","));
				info = info.substring(info.indexOf(",") + 1);
				friendsadd = info.substring(0, info.indexOf(","));
				info = info.substring(info.indexOf(",") + 1);
				statusesadd = info.substring(0, info.indexOf(","));
				info = info.substring(info.indexOf(",") + 1);
				followersaddratio = info.substring(0, info.indexOf(","));
				info = info.substring(info.indexOf(",") + 1);
				friendsaddratio = info.substring(0, info.indexOf(","));
				statusesaddratio = info.substring(info.indexOf(",") + 1);
				
				jdbc1.insertTodayResult1("sinauserinforesult", 
						usernameday, usernameday.substring(0,usernameday.length()-8), 
						usernameday.substring(usernameday.length()-8), 
						Integer.parseInt(followers), Integer.parseInt(friends), Integer.parseInt(statuses), 
						Integer.parseInt(followersadd), Integer.parseInt(friendsadd), Integer.parseInt(statusesadd), 
						followersaddratio, friendsaddratio, statusesaddratio);
				
			}
			jdbc1.sqlClose();
		}
	}

}
