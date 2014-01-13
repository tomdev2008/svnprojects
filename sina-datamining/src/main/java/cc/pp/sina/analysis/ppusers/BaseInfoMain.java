package cc.pp.sina.analysis.ppusers;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import cc.pp.sina.jdbc.PPUJDBC;

import com.sina.weibo.api.Users;
import com.sina.weibo.model.User;
import com.sina.weibo.model.WeiboException;

public class BaseInfoMain {

	private String tablename = "";
	private String utablename = "";
	@SuppressWarnings("unused")
	private String ntablename = "";
	private final int MAX = 100;
	private String ip = "";

	public BaseInfoMain(String tablename, String utablename, String ntablename, String ip) {
		this.tablename = tablename;
		this.utablename = utablename;
		this.ntablename = ntablename;
		this.ip = ip;
	}

	/**
	 * @param args
	 * @throws WeiboException 
	 * @throws SQLException 
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws WeiboException, SQLException, InterruptedException {

		BaseInfoMain baseinfo = new BaseInfoMain("sinauserinfo", "ppsinausers", "sinaunusedusers", "192.168.1.189");
		while (true) {
			long time1 = System.currentTimeMillis() / 1000;
			baseinfo.importDataToDB();
			long time2 = System.currentTimeMillis() / 1000;
			long diff = time2 - time1;
			if (diff < 3600) {
				Thread.sleep(1000 * (3600 - diff));
			}
		}
	}

	public void importDataToDB() throws SQLException, WeiboException {

		PPUJDBC myjdbc = new PPUJDBC(ip);
		if (myjdbc.mysqlStatus()) {
			/****************获取token数据*****************/
			String[] tokens = myjdbc.getAccessToken(390);
			for (int i = 0; i < tokens.length; i++) {
				System.out.println(i);
				/****************获取sina用户名*****************/
				List<String> sinausers = myjdbc.getSinaUsers(utablename, MAX);
				/****************采集用户信息并存储***************/
				this.importDataToDbDis(tokens[i], sinausers);
				/****************批量删除用户名******************/
				myjdbc.deleteBatch(utablename, sinausers);
			}
			myjdbc.sqlClose();
		}
	}

	/**
	 * @throws WeiboException 
	 * 把数据导入数据库
	 * @param uids
	 * @throws SQLException
	 * @throws  
	 */
	public void importDataToDbDis(String accesstoken, List<String> uids) throws SQLException, WeiboException {

		PPUJDBC myjdbc = new PPUJDBC(ip);
		if (myjdbc.mysqlStatus()) {
			List<User> users = this.batchBaseinfo(accesstoken, uids);
			for (User user : users) {
				int gender = 2; //m-0-男，f-1-女,2-未知
				int verify = 0; //0-未认证，1-认证
				if ("m".equalsIgnoreCase(user.getGender())) {
					gender = 0;
				} else if ("f".equalsIgnoreCase(user.getGender())) {
					gender = 1;
				}
				if (user.isVerified()) {
					verify = 1;
				}
				if (user.getCity() != 0) {
					try {
						long wbcreatedat = user.getStatus().getCreatedAt().getTime() / 1000;
						myjdbc.insertUsersInfo(tablename, user.getId(), user.getScreenName(), //
								user.getDescription(), gender, user.getProvince(), user.getFollowersCount(),//
								user.getFriendsCount(), user.getStatusesCount(), verify, //
								user.getVerifiedType(), user.getVerifiedReason(), //
								wbcreatedat);
					} catch (RuntimeException e) {
						continue;
					}
				}
			}
			myjdbc.sqlClose();
		}
	}

	/**
	 * 批量获取用户基础信息
	 * @param accesstoken
	 * @param uids：不超过150，定位100
	 * @return
	 * @throws WeiboException
	 */
	public List<User> batchBaseinfo(String accesstoken, List<String> uids) throws WeiboException {

		Users user = new Users();
		user.client.setToken(accesstoken);
		List<User> result = new ArrayList<User>();
		User userinfo = null;
		for (String uid : uids) {
			userinfo = user.showUserById(uid);
			result.add(userinfo);
		}

		return result;
	}

}

