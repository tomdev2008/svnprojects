package cc.pp.sina.analysis.ppusers;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import cc.pp.sina.jdbc.LocalJDBC;

import com.sina.weibo.api.Users;
import com.sina.weibo.model.User;
import com.sina.weibo.model.WeiboException;

public class BaseInfo {

	private String tablename = "";
	private String utablename = "";
	@SuppressWarnings("unused")
	private String ntablename = "";
	private final int MAX = 100;

	public BaseInfo(String tablename, String utablename, String ntablename) {
		this.tablename = tablename;
		this.utablename = utablename;
		this.ntablename = ntablename;
	}

	/**
	 * @param args
	 * @throws WeiboException 
	 * @throws SQLException 
	 */
	public static void main(String[] args) throws WeiboException, SQLException {

		BaseInfo baseinfo = new BaseInfo("sinauserinfo", "sinausers", "sinaunusedusers");
		baseinfo.importDataToDB();

	}

	/**
	 * 执行一小时内API的操作
	 * @throws SQLException
	 * @throws WeiboException
	 */
	public void importDataToDB() throws SQLException, WeiboException {

		LocalJDBC myjdbc = new LocalJDBC("127.0.0.1", "root", "");
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
				myjdbc.deleteBatch(utablename, sinausers); // 注意要添加token索引，否则很慢
			}
			myjdbc.sqlClose();
		}
	}

	/**
	 * 把数据导入数据库
	 * @param accesstoken
	 * @param uids
	 * @throws SQLException
	 * @throws WeiboException
	 */
	public void importDataToDbDis(String accesstoken, List<String> uids) throws SQLException, WeiboException {

		LocalJDBC myjdbc = new LocalJDBC("127.0.0.1", "root", "");
		if (myjdbc.mysqlStatus()) {
			List<User> users = this.batchBaseinfo(accesstoken, uids);
			for (User user : users) {
				int gender = 2;  //m-0-男，f-1-女,2-未知
				int verify = 0;  //0-未认证，1-认证
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
