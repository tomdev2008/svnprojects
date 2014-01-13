package cc.pp.tencent.analysis.user.baseinfo;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map.Entry;

import cc.pp.tencent.jdbc.UbiaJDBC;

/**
 * Title:用户基础信息分析
 * @author wanggang
 * @version 1.1
 * @since 2013-05-27
 */
public class BaseInfoAnalysis implements Serializable {
	
	/**
	 * 默认的序列化版本号
	 */
	private static final long serialVersionUID = 1L;
	
	private String ip = "";
	
	/**
	 * 构造函数
	 * @param ip
	 */
	public BaseInfoAnalysis(String ip) {
		this.ip = ip;
	}
	
	/**
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		BaseInfoAnalysis ubia = new BaseInfoAnalysis("192.168.1.27");
		ubia.analysis();

	}
	
	/**
	 * 分析函数
	 * @throws Exception 
	 * @throws SQLException
	 * @throws WeiboException
	 * @throws JSONException
	 */
	public void analysis() throws Exception {
		
		UbiaJDBC myjdbc = new UbiaJDBC(this.ip);
		if (myjdbc.mysqlStatus())
		{
			/*******************获取T2授权用户*********************/
			GetAddOauthUsers gaau = new GetAddOauthUsers();
			String bid = myjdbc.getMaxBid();
			gaau.insertAddUsersByDay("tencent", bid, this.ip);
			HashMap<Integer,String> tencentusers = myjdbc.getTencentUsers();
			/***********************获取今天的数据**************************/
			BasedInfoGet big = new BasedInfoGet(this.ip);
			HashMap<String,String> todayinfo = big.getBaseinfo(tencentusers); 
			/***********************获取昨天的数据**************************/
			UbiaUtils ubiaUtils = new UbiaUtils();
			String date = ubiaUtils.getYesteydayDate();
			String tablename = "tencentuserinforesult";
			HashMap<String,String> yesterdayinfo = myjdbc.getYesterdayResult(tablename, date);
			/***********************分析今天的数据**************************/
			HashMap<Integer,String> nouserinfo = new HashMap<Integer,String>();
			HashMap<Integer,String> adduserinfo = new HashMap<Integer,String>();
			int k = 0;
			String username = new String();
			for (Entry<String,String> yesterday : yesterdayinfo.entrySet()) {
				username = yesterday.getKey();
				if (todayinfo.get(username) == null) {  // 数据没获取到，加入重新获取的列表
					nouserinfo.put(k++, username);
				} else {
					this.insertData(todayinfo.get(username), yesterdayinfo.get(username), 
							myjdbc, ubiaUtils.getTodayDate(), username, tablename);		
				}
			}
			k = 0;
			for (Entry<String,String> today : todayinfo.entrySet()) {
				username = today.getKey();
				if (yesterdayinfo.get(username) == null) {  // 新加入的用户
					adduserinfo.put(k++, username);
				} 
			}
			
			// 处理为获取到数据的用户
			BasedInfoGet basedinfoget = new BasedInfoGet(this.ip);
			if (nouserinfo.size() > 0) {
				todayinfo = basedinfoget.getBaseinfo(nouserinfo);
				if (todayinfo.size() > 0) {
					for (Entry<String,String> today : todayinfo.entrySet()) {
						username = today.getKey();
						this.insertData(todayinfo.get(username), yesterdayinfo.get(username), 
								myjdbc, ubiaUtils.getTodayDate(), username, tablename);		
					}
				}
			}
			
			// 处理新加入的用户
			if (adduserinfo.size() > 0) {
				big.insertInitinfo(adduserinfo);
			}
			
			myjdbc.sqlClose();
		}
		
	}
	
	/**
	 * 插入结果数据
	 * @param todayinfo
	 * @param yesterdayinfo
	 * @param myjdbc
	 * @param date
	 * @param username
	 * @param tablename
	 * @throws SQLException
	 */
	public void insertData(String todayinfo, String yesterdayinfo, UbiaJDBC myjdbc, String date, 
			String username, String tablename) throws SQLException {
		
		String info = new String();
		int tfollowers = 0;
		int tfriends = 0;
		int yfollowers = 0;
		int yfriends = 0;
		int followersadd = 0;
		int friendsadd = 0;
		String followersaddratio = new String();
		String friendsaddratio = new String();
		info = todayinfo;
		tfollowers = Integer.parseInt(info.substring(0, info.indexOf(",")));
		tfriends = Integer.parseInt(info.substring(info.indexOf(",") + 1));
		info = yesterdayinfo;
		if (info == null) {
			yfollowers = tfollowers;
			yfriends = tfriends;
		} else {
			yfollowers = Integer.parseInt(info.substring(0, info.indexOf(",")));
			yfriends = Integer.parseInt(info.substring(info.indexOf(",") + 1));
		}
		followersadd = tfollowers - yfollowers;
		friendsadd = tfriends - yfriends;
		if (yfollowers == 0) {
			followersaddratio = "0.0%";
		} else {
			if (followersadd >= 0) {
				followersaddratio = Float.toString((float)(Math.round(((float)followersadd/yfollowers)*100000))/1000) + "%";
			} else {
				followersaddratio = "-" + Float.toString((float)(Math.round(((float)(Math.abs(followersadd))/yfollowers)*100000))/1000) + "%";
			}
		}
		if (yfriends == 0) {
			friendsaddratio = "0.0%";
		} else {
			if (friendsadd >= 0) {
				friendsaddratio = Float.toString((float)(Math.round(((float)friendsadd/yfriends)*100000))/1000) + "%";	
			} else {
				friendsaddratio = "-" + Float.toString((float)(Math.round(((float)(Math.abs(friendsadd))/yfriends)*100000))/1000) + "%";
			}
		}
		myjdbc.insertTodayResult1(tablename, username + date, username, date, tfollowers, tfriends, 
				followersadd, friendsadd, followersaddratio, friendsaddratio);
	}

}

