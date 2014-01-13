package cc.pp.tencent.analysis.username;

import java.io.EOFException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.conn.ConnectTimeoutException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.exc.UnrecognizedPropertyException;

import cc.pp.tencent.jdbc.UsernameJDBC;
import cc.pp.tencent.utils.Nettool;

import com.tencent.weibo.api.FriendsAPI;
import com.tencent.weibo.api.UserAPI;
import com.tencent.weibo.constants.OauthInit;
import com.tencent.weibo.dao.impl.FansInfo;
import com.tencent.weibo.dao.impl.OtherInfo;
import com.tencent.weibo.dao.impl.OtherInfoData;
import com.tencent.weibo.dao.impl.UserFansList;
import com.tencent.weibo.dao.impl.UserIdolList;
import com.tencent.weibo.dao.impl.UserIdolListInfo;
import com.tencent.weibo.oauthv1.OAuthV1;

public class GetFansAndFriends {

	private static int MAX_NUM = 100;

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {

		GetFansAndFriends getFansAndFriends = new GetFansAndFriends();
		int flag;
		while (true) {
			flag = getFansAndFriends.dumpFansAndFriendsInfo();
			if (flag == 2) {
				break;
			}
		}
	}

	/**
	 * 获取用户信息、粉丝信息、关注信息
	 * @throws Exception
	 */
	public int dumpFansAndFriendsInfo() throws Exception {

		//		UsernameJDBC myJdbc = new UsernameJDBC("127.0.0.1", "root", "");
		UsernameJDBC myJdbc = new UsernameJDBC(Nettool.getServerLocalIp());
		if (myJdbc.mysqlStatus()) {
			String[] tokens = myJdbc.getAccessTokenNums(MAX_NUM);
			List<String> uids = myJdbc.getTencentUsers("tencentusers", MAX_NUM);
			if (uids.size() == 0) {
				myJdbc.sqlClose();
				return 2;
			}
			int[] fansfriends = new int[2];
			int i = 0;
			for (String uid : uids) {
				//				System.out.println(i);
				String uidstablename = this.getTablename(myJdbc);
				try {
					fansfriends = this.getUserBaseInfo(myJdbc, tokens[i], uid);
					this.dumpFansToDb(myJdbc, tokens[i], uid, fansfriends[0], uidstablename);
					this.dumpFriendsToDb(myJdbc, tokens[i], uid, fansfriends[1], uidstablename, "tencentfriends");
				} catch (ConnectTimeoutException e) {
					myJdbc.insertUnusedUsers("tencentunuseduids", uid);
					myJdbc.sqlClose();
					return -1;
				}
				i++;
			}
			myJdbc.deleteBatch("tencentusers", uids);
			myJdbc.sqlClose();

			return 0;
		}

		return 0;
	}

	/**
	 * 获取用户基础信息
	 * @param tokens
	 * @param uid
	 * @throws Exception
	 */
	public int[] getUserBaseInfo(UsernameJDBC myJdbc, String token, String uid) throws Exception {

		OAuthV1 oauth = new OAuthV1();
		String accesstoken = token.substring(0, token.indexOf(","));
		String tokensecret = token.substring(token.indexOf(",") + 1);
		OauthInit.oauthInit(oauth, accesstoken, tokensecret);
		UserAPI user = new UserAPI(oauth.getOauthVersion());
		int[] fansAndfriendCount = new int[2];
		ObjectMapper mapper = new ObjectMapper();
		try {
			String userinfo = user.otherInfo(oauth, "json", uid, "");
			OtherInfo otherinfo = mapper.readValue(userinfo, OtherInfo.class);
			/******************插入数据库********************/
			OtherInfoData data = otherinfo.getData();
			try {
				myJdbc.inserUserBaseInfo("tencentuserbaseinfo", data.getName(), data.getNick(), //
						data.getOpenid(), data.getFansnum(), data.getFavnum(), data.getIdolnum(), //
						data.getTweetnum(), data.getBirth_day(), data.getBirth_month(), data.getBirth_year(), //
						data.getCity_code(), data.getComp(), data.getCountry_code(), data.getEdu(), //
						data.getEmail(), data.getExp(), data.getHead(), data.getHomecity_code(), //
						data.getHomecountry_code(), data.getHomepage(), data.getHomeprovince_code(), //
						data.getHometown_code(), data.getHttps_head(), data.getIndustry_code(), //
						data.getIntroduction(), data.getIsent(), data.getIsrealname(), data.getIsvip(), //
						data.getLevel(), data.getLocation(), data.getMutual_fans_num(), //
						data.getProvince_code(), data.getRegtime(), data.getSend_private_flag(), //
						data.getSex(), data.getTag(), data.getVerifyinfo());
			} catch (SQLException e) {
				myJdbc.insertUnusedUsers("tencentunuseduids", uid);
			}
			/***************返回用户粉丝量和关注量***************/
			fansAndfriendCount[0] = otherinfo.getData().getFansnum();
			fansAndfriendCount[1] = otherinfo.getData().getIdolnum();

			return fansAndfriendCount;
		} catch (UnrecognizedPropertyException e) {
			myJdbc.insertUnusedUsers("tencentunuseduids", uid);
			return fansAndfriendCount;
		} catch (JsonMappingException e) {
			myJdbc.insertUnusedUsers("tencentunuseduids", uid);
			return fansAndfriendCount;
		} catch (EOFException e) {
			myJdbc.insertUnusedUsers("tencentunuseduids", uid);
			return fansAndfriendCount;
		} catch (RuntimeException e) {
			myJdbc.insertUnusedUsers("tencentunuseduids", uid);
			return fansAndfriendCount;
		} catch (SocketTimeoutException e) {
			myJdbc.insertUnusedUsers("tencentunuseduids", uid);
			return fansAndfriendCount;
		} catch (JsonParseException e) {
			myJdbc.insertUnusedUsers("tencentunuseduids", uid);
			return fansAndfriendCount;
		} catch (UnknownHostException e) {
			myJdbc.insertUnusedUsers("tencentunuseduids", uid);
			return fansAndfriendCount;
		}
	}

	/**
	 * 获取用户的粉丝信息
	 *    ***最多拉取10000个粉丝信息***
	 * @param myJdbc
	 * @param tokens
	 * @param uid
	 * @return
	 * @throws Exception
	 */
	public int dumpFansToDb(UsernameJDBC myJdbc, String token, String uid, int fansnum, //
			String uidtablename) throws Exception {

		OAuthV1 oauth = new OAuthV1();
		String accesstoken = token.substring(0, token.indexOf(","));
		String tokensecret = token.substring(token.indexOf(",") + 1);
		OauthInit.oauthInit(oauth, accesstoken, tokensecret);
		FriendsAPI fa = new FriendsAPI(oauth.getOauthVersion());
		ObjectMapper mapper = new ObjectMapper();
		try {
			List<String> uids = new ArrayList<String>();
			String fansinfo;
			UserFansList userFansList;
			int pagecount = (fansnum / 30 + 1 > 300) ? 300 : fansnum / 30 + 1;
			for (int page = 1; page < pagecount; page++) {
				fansinfo = fa.userFanslist(oauth, "json", "30", Integer.toString((page - 1) * 30), uid, "", "1", "0");
				userFansList = mapper.readValue(fansinfo, UserFansList.class);
				List<FansInfo> fansinfos = userFansList.getData().getInfo();
				for (FansInfo info : fansinfos) {
					uids.add(info.getName());
				}
			}
			/******************插入数据库********************/
			try {
				myJdbc.insertUidsBatch(uidtablename, uids);
			} catch (SQLException e) {
				myJdbc.insertUnusedUsers("tencentunuseduids", uid);
				return -1;
			}
			return 0;
		} catch (UnrecognizedPropertyException e) {
			myJdbc.insertUnusedUsers("tencentunuseduids", uid);
			return -1;
		} catch (JsonMappingException e) {
			myJdbc.insertUnusedUsers("tencentunuseduids", uid);
			return -1;
		} catch (EOFException e) {
			myJdbc.insertUnusedUsers("tencentunuseduids", uid);
			return -1;
		} catch (RuntimeException e) {
			myJdbc.insertUnusedUsers("tencentunuseduids", uid);
			return -1;
		} catch (SocketTimeoutException e) {
			myJdbc.insertUnusedUsers("tencentunuseduids", uid);
			return -1;
		} catch (JsonParseException e) {
			myJdbc.insertUnusedUsers("tencentunuseduids", uid);
			return -1;
		} catch (UnknownHostException e) {
			myJdbc.insertUnusedUsers("tencentunuseduids", uid);
			return -1;
		}
	}

	/**
	 * 获取用户的关注信息
	 *    ***最多拉取10000个关注信息***
	 * @param myJdbc
	 * @param tokens
	 * @param uid
	 * @param fansnum
	 * @return
	 * @throws Exception
	 */
	public int dumpFriendsToDb(UsernameJDBC myJdbc, String token, String uid, int idolnum, //
			String uidtablename, String friendtablename) throws Exception {

		OAuthV1 oauth = new OAuthV1();
		String accesstoken = token.substring(0, token.indexOf(","));
		String tokensecret = token.substring(token.indexOf(",") + 1);
		OauthInit.oauthInit(oauth, accesstoken, tokensecret);
		FriendsAPI fa = new FriendsAPI(oauth.getOauthVersion());
		ObjectMapper mapper = new ObjectMapper();
		try {
			List<String> uids = new ArrayList<String>();
			String friendsinfo;
			UserIdolList userIdolList;
			int pagecount = (idolnum / 30 + 1 > 300) ? 300 : idolnum / 30 + 1;
			for (int page = 1; page < pagecount; page++) {
				friendsinfo = fa.userIdollist(oauth, "json", "30", //
						Integer.toString((page - 1) * 30), uid, "", "0", "1");
				userIdolList = mapper.readValue(friendsinfo, UserIdolList.class);
				List<UserIdolListInfo> idolinfos = userIdolList.getData().getInfo();
				for (UserIdolListInfo info : idolinfos) {
					uids.add(info.getName());
				}
			}
			/******************插入数据库********************/
			try {
				myJdbc.insertUidsBatch(uidtablename, uids); // 插入关注的uid到tencentuids
				/**
				 * 出现‘/tmp/MLJ5ciYa’异常字符
				 */
				myJdbc.insertFriendUids(friendtablename, uid, uids); // 插入关注的uid到tencentfriends
			} catch (SQLException e) {
				myJdbc.insertUnusedUsers("tencentunuseduids", uid);
				return -1;
			}
			return 0;
		} catch (UnrecognizedPropertyException e) {
			myJdbc.insertUnusedUsers("tencentunuseduids", uid);
			return -1;
		} catch (JsonMappingException e) {
			myJdbc.insertUnusedUsers("tencentunuseduids", uid);
			return -1;
		} catch (EOFException e) {
			myJdbc.insertUnusedUsers("tencentunuseduids", uid);
			return -1;
		} catch (RuntimeException e) {
			myJdbc.insertUnusedUsers("tencentunuseduids", uid);
			return -1;
		} catch (SocketTimeoutException e) {
			myJdbc.insertUnusedUsers("tencentunuseduids", uid);
			return -1;
		} catch (JsonParseException e) {
			myJdbc.insertUnusedUsers("tencentunuseduids", uid);
			return -1;
		} catch (UnknownHostException e) {
			myJdbc.insertUnusedUsers("tencentunuseduids", uid);
			return -1;
		}
	}

	/**
	 * 获取表名
	 * @throws SQLException 
	 */
	public String getTablename(UsernameJDBC myJdbc) throws SQLException {

		String tablename = new String();
		String lasttablename = myJdbc.getLastTablename("tencenttablenames");
		if (lasttablename == null) {
			//			tablename = "tencentuids" + this.getTodayDate();
			tablename = "tencentuids" + System.currentTimeMillis() / 1000;
			myJdbc.createUidsTable(tablename);
			myJdbc.insertTablename(tablename);
		} else {
			int max = myJdbc.getMaxId(lasttablename);
			if (max > 10000000) {
				//				tablename = "tencentuids" + this.getTodayDate();
				tablename = "tencentuids" + System.currentTimeMillis() / 1000;
				myJdbc.createUidsTable(tablename);
				myJdbc.insertTablename(tablename);
			} else {
				tablename = lasttablename;
			}
		}

		return tablename;
	}

	/**
	 * 获取今天日期
	 * @return
	 */
	public String getTodayDate() {

		long time = System.currentTimeMillis() / 1000;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String date = sdf.format(new Date(time * 1000l));
		date = date.replaceAll("-", "");

		return date;
	}

}
