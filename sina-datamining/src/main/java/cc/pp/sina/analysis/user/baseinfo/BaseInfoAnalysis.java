package cc.pp.sina.analysis.user.baseinfo;

import java.io.IOException;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map.Entry;

import org.apache.commons.httpclient.HttpException;

import cc.pp.sina.jdbc.UbiaJDBC;

import com.sina.weibo.json.JSONException;
import com.sina.weibo.model.WeiboException;

/**
 * Title:用户基础信息分析
 * @author wanggang
 * @version 1.1
 * @since 2013-05-27
 */
public class BaseInfoAnalysis implements Serializable {
	
	/**
	 * 默认序列化版本号
	 */
	private static final long serialVersionUID = 1L;
	
	private String ip = "";
	
	public BaseInfoAnalysis(String ip) {
		this.ip = ip;
	}
	
	/**
	 * 测试函数
	 * @param args
	 * @throws JSONException 
	 * @throws WeiboException 
	 * @throws SQLException 
	 * @throws IOException 
	 * @throws HttpException 
	 */
	public static void main(String[] args) throws SQLException, WeiboException, JSONException, HttpException, IOException {

		BaseInfoAnalysis ubia = new BaseInfoAnalysis("192.168.1.27");
		ubia.analysis();
		/*********************转移数据***********************/
		MoveResult mr = new MoveResult("192.168.1.27");
		mr.moveResult();
	}
	
	/**
	 * 分析函数
	 * @throws SQLException
	 * @throws WeiboException
	 * @throws JSONException
	 * @throws IOException 
	 * @throws HttpException 
	 */
	public void analysis() throws SQLException, WeiboException, JSONException, HttpException, IOException {
		
		UbiaJDBC myjdbc = new UbiaJDBC(this.ip);
		if (myjdbc.mysqlStatus())
		{
			/*******************获取T2授权用户*********************/
			GetAddOauthUsers gaau = new GetAddOauthUsers();
			String bid = myjdbc.getMaxBid();
			gaau.insertAddUsersByDay("sina", bid, this.ip);
			HashMap<Integer,String> sinausers = myjdbc.getSinaUsers();
			/***********************获取今天的数据**************************/
			BasedInfoGet big = new BasedInfoGet(this.ip);
			HashMap<String,String> todayinfo = big.getBaseinfo(sinausers); 
			/***********************获取昨天的数据**************************/
			UbiaUtils ubiaUtils = new UbiaUtils();
			String date = ubiaUtils.getYesteydayDate();
			String tablename = "sinauserinforesult";
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
	 * 插入分析结果数据
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
		int tstatuses = 0;
		int yfollowers = 0;
		int yfriends = 0;
		int ystatuses = 0;
		int followersadd = 0;
		int friendsadd = 0;
		int statusesadd = 0;
		String followersaddratio = new String();
		String friendsaddratio = new String();
		String statusesaddratio = new String();
		info = todayinfo;
		tfollowers = Integer.parseInt(info.substring(0, info.indexOf(",")));
		info = info.substring(info.indexOf(",") + 1);
		tfriends = Integer.parseInt(info.substring(0, info.indexOf(",")));
		tstatuses = Integer.parseInt(info.substring(info.indexOf(",") + 1));
		info = yesterdayinfo;
		if (info == null) {
			yfollowers = tfollowers;
			yfriends = tfriends;
			ystatuses = tstatuses;
		} else {
			yfollowers = Integer.parseInt(info.substring(0, info.indexOf(",")));
			info = info.substring(info.indexOf(",") + 1);
			yfriends = Integer.parseInt(info.substring(0, info.indexOf(",")));
			ystatuses = Integer.parseInt(info.substring(info.indexOf(",") + 1));
		}
		followersadd = tfollowers - yfollowers;
		friendsadd = tfriends - yfriends;
		statusesadd = tstatuses - ystatuses;
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
		if (ystatuses == 0) {
			statusesaddratio = "0.0%";
		} else {
			if (statusesadd >= 0) {
				statusesaddratio = Float.toString((float)(Math.round(((float)statusesadd/ystatuses)*100000))/1000) + "%";
			} else {
				statusesaddratio = "-" + Float.toString((float)(Math.round(((float)(Math.abs(statusesadd))/ystatuses)*100000))/1000) + "%";
			}
		}
		myjdbc.insertTodayResult1(tablename, username + date, username, date, tfollowers, tfriends, tstatuses, 
				followersadd, friendsadd, statusesadd, followersaddratio, friendsaddratio, statusesaddratio);
	}

}
