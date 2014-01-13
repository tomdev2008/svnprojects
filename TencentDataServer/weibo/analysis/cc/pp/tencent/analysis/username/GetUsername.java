package cc.pp.tencent.analysis.username;

import java.io.Serializable;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map.Entry;

import org.codehaus.jackson.JsonNode;

import cc.pp.tencent.jdbc.UidJDBC;
import cc.pp.tencent.utils.JsonUtils;

import com.tencent.weibo.api.FriendsAPI;
import com.tencent.weibo.oauthv1.OAuthV1;

public class GetUsername implements Serializable {

	/**
	 * 默认的序列化版本号
	 */
	private static final long serialVersionUID = 1L;
	
	public int api = 0;
	private String ip = "";
	
	public GetUsername(String ip) {
		this.ip = ip;
	}

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {

		GetUsername getusername = new GetUsername("192.168.1.171");
		UidJDBC uidjdbc = new UidJDBC("192.168.1.171");
		if (uidjdbc.mysqlStatus()) {
			String tablename = getusername.getTablename();
			HashMap<Integer,String> uids = uidjdbc.getAllUids("tencentusername", 100);
			for (int i = 0; i < uids.size(); i++) {
				getusername.getFansUids(tablename, uids.get(i));
				uidjdbc.deleteUids("tencentusername", uids.get(i));
			}
			uidjdbc.sqlClose();
		}

	}

	/**
	 * 获取粉丝usernames
	 * @throws Exception 
	 */
	public void getFansUids(String tablename, String uid) throws Exception {
		
		UidJDBC uidjdbc = new UidJDBC(this.ip);
		if (uidjdbc.mysqlStatus()) {
//			String[] token = {"2c656d405a1d4587b7b99d143102d002","b36e96d43ed75485258f0fdf0cefe143"};
			String[] token = uidjdbc.getAccessTokenone();
			OAuthV1 oauth = new OAuthV1();
			this.oauthInit(oauth, token[0], token[1]);
			FriendsAPI userfriends = new FriendsAPI(oauth.getOauthVersion());
			int cursor = 0;
			String nextstartpos = "0";
			JsonNode jsondata;
			String baseinfo;
			String username;
			try {
				do {
					baseinfo = userfriends.userFanslist(oauth, "json", "30", nextstartpos, uid, "", "1", "0");
					this.api++;
					cursor++;
					jsondata = JsonUtils.getJsonNode(baseinfo);
					nextstartpos = jsondata.get("data").get("nextstartpos").toString();
					
					for (int i = 0; i < jsondata.get("data").get("info").size(); i++) {		
						username = jsondata.get("data").get("info").get(i).get("name").toString();
						uidjdbc.insertUids(tablename, username.replaceAll("\"", ""));
					}
				} while (cursor*30 < 100000);
			} catch (RuntimeException e) {
				// kong
			}
			
			uidjdbc.sqlClose();
		}
		
	}

	/**
	 * 获取表名
	 * @throws SQLException 
	 */
	public String getTablename() throws SQLException {
		
		String tablename = new String();
		UidJDBC uidjdbc = new UidJDBC(this.ip);
		if (uidjdbc.mysqlStatus()) {
			String lasttablename = uidjdbc.getLastTablename();
			if (lasttablename == null) {
				tablename = "usernames" + this.getTodayDate();
				uidjdbc.createTable(tablename);
				uidjdbc.insertTablename(tablename);
			} else {
				int max = uidjdbc.getMaxId(lasttablename);
				if (max > 10000000) {
					tablename = "usernames" + this.getTodayDate();
					uidjdbc.createTable(tablename);
					uidjdbc.insertTablename(tablename);
				} else {
					tablename = lasttablename;
				}
			}
			uidjdbc.sqlClose();
		}
		
		return tablename;
	}

	/**
	 * 获取今天日期
	 * @return
	 */
	public String getTodayDate() {
		
		long time = System.currentTimeMillis()/1000;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String date = sdf.format(new Date(time*1000l));
		date = date.replaceAll("-", "");
		
		return date;
	}
	
	/**
	 * 转移数据
	 * @throws SQLException
	 */
	public void transUids() throws SQLException {

		UidJDBC uidjdbc = new UidJDBC(this.ip);
		if (uidjdbc.mysqlStatus()) {
			//			for (int i = 1; i < 11; i++) {
			//				uidjdbc.createTable("tencentusername" + i);
			//			}
			HashMap<String,String> alluids = uidjdbc.getUids("tencentusername");
			System.out.println(alluids.size());
			String username = new String();
			int index = 0;
			for (Entry<String,String> temp : alluids.entrySet()) {
				username = temp.getKey();
				index = username.length();
				if (index < 8) {
					index = 1;
				} else if ((index == 15) || (index == 16)) {
					index = 9;
				} else if (index > 16) {
					index = 10;
				} else {
					index = index - 6;
				}
				uidjdbc.insertUids("tencentusername" + index, username);
			}

			uidjdbc.sqlClose();
		}
	}
	
	public void transUids1() throws SQLException {

		UidJDBC uidjdbc = new UidJDBC(this.ip);
		if (uidjdbc.mysqlStatus()) {
			for (int i = 1; i < 11; i++) {
				HashMap<String,String> alluids = uidjdbc.getUids("tencentusername" + i);
				for (Entry<String,String> temp : alluids.entrySet()) {
					uidjdbc.insertUids("tencentusername", temp.getKey());
				}
			}

			uidjdbc.sqlClose();
		}
	}
	
	public void transUids2() throws SQLException {

		UidJDBC uidjdbc = new UidJDBC(this.ip);
		HashMap<Integer,String> alluids = new HashMap<Integer,String>();
		if (uidjdbc.mysqlStatus()) {
			alluids = uidjdbc.getAllUids("tencentusername");
			uidjdbc.sqlClose();
		}
		uidjdbc = new UidJDBC("192.168.1.171");
		if (uidjdbc.mysqlStatus()) {
			uidjdbc.createTable("tencentusername");
			for (int i = 0; i < 900000; i++) {
				uidjdbc.insertUids("tencentusername", alluids.get(i));
			}
			uidjdbc.sqlClose();
		}
		for (int i = 0; i < 7; i++) {
			UidJDBC uidjdbc1 = new UidJDBC("192.168.1." + 173 + i);
			if (uidjdbc1.mysqlStatus()) {
				uidjdbc1.createTable("tencentusername");
				for (int j = 900000*(i+1); j < 900000*(i+2); j++) {
					uidjdbc1.insertUids("tencentusername", alluids.get(j));
				}
				uidjdbc1.sqlClose();
			}
		}
	}
	
	/**
	 * @授权参数初始化
	 * @param oauth
	 */
	public void oauthInit(OAuthV1 oauth, String accesstoken, String tokensecret) 
	{
		/***************皮皮时光机appkey和appsecret***********************/
		oauth.setOauthConsumerKey("11b5a3c188484c3f8654b83d32e19bab");
		oauth.setOauthConsumerSecret("dc5cd31e1ddf556a42a40a1cff7efd5c");
		/**************************************************************/
		//oauth.setOauthCallback("");
		oauth.setOauthToken(accesstoken);
		oauth.setOauthTokenSecret(tokensecret);
	}
	
}
