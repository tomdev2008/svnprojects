package cc.pp.sina.require.custom;

import java.sql.SQLException;
import java.util.HashMap;

import net.sf.json.JSONArray;
import cc.pp.sina.algorithms.InsertSort;
import cc.pp.sina.analysis.userfans.FansUtils;
import cc.pp.sina.jdbc.LocalJDBC;

import com.sina.weibo.api.Friendships;
import com.sina.weibo.api.Users;
import com.sina.weibo.model.User;
import com.sina.weibo.model.UserWapper;

public class FansInfo {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {

		FansInfo fansInfo = new FansInfo();
		String accesstoken = "2.00eEQZzBdcZIJC9afb135d07Rbw7NE";
		String uid = "2144684673"; //2342318502、2144684673
		int index = fansInfo.userFansInfo(accesstoken, uid);
		System.out.println(index);

	}

	public int userFansInfo(String accesstoken, String uid) throws Exception {

		/*****************采集用户信息******************/
		Users um = new Users();
		um.client.setToken(accesstoken);
		User user = um.showUserById(uid);
		if (user.getStatusCode().equals("20003")) {
			return -1;
		}
		/****************采集用户粉丝信息****************/
		Friendships fm = new Friendships();
		fm.client.setToken(accesstoken);
		int cursor = 0;
		UserWapper followers = null;
		try {
			followers = fm.getFollowersById(uid, 200, cursor * 200);
		} catch (RuntimeException e) {
			return -1;
		}
		/******************初始化变量******************/
		FansUtils fansUtils = new FansUtils();
		// 2、活跃粉丝数 
		// 5、粉丝的热门标签
		int[] age = new int[5]; // 年龄分析
		String[] top40fans = new String[40]; // top40粉丝，按照粉丝量
		fansUtils.initTopArray(top40fans);
		int addv, fanssum, weibosum, isactivefans = 0, existuids = 0;
		long createdatady;
		String username;

		LocalJDBC myjdbc = new LocalJDBC("127.0.0.1", "root", "");
		if (myjdbc.mysqlStatus()) {
			while ((cursor * 200 < user.getFollowersCount() + 200) && (cursor < 26)) {
				for (User u : followers.getUsers()) {
					/*****************0、粉丝存在数*****************/
					if (u.getName() != null) {
						existuids++;
					} else {
						continue;
					}
					username = u.getId();
					fanssum = u.getFollowersCount();
					weibosum = u.getStatusesCount();
					createdatady = (System.currentTimeMillis() - u.getCreatedAt().getTime()) / (1000 * 86400) + 1;
					if (u.isVerified()) {
						addv = 1;
					} else {
						addv = 0;
					}
					if (weibosum / createdatady > 1) {
						isactivefans = 1;
					} else {
						isactivefans = 0;
					}
					/****************存储粉丝基础信息***************/
					try {
						myjdbc.insertUserBaseInfo("fansinfo", u.getId(), u.getScreenName(), u.getProvince(), //
								u.getCity(), u.getLocation(), u.getDescription(), u.getProfileImageUrl(),//
								u.getGender(), u.getFollowersCount(), u.getFriendsCount(), u.getStatusesCount(), //
								u.getFavouritesCount(), u.getCreatedAt().getTime() / 1000, addv,//
								u.getVerifiedReason(), u.getBiFollowersCount(), isactivefans, u.getWeihao(), "");
					} catch (SQLException e) {
						myjdbc.insertUserBaseInfo("fansinfo", u.getId(), u.getScreenName(), u.getProvince(), //
								u.getCity(), u.getLocation(), "", u.getProfileImageUrl(),//
								u.getGender(), u.getFollowersCount(), u.getFriendsCount(), u.getStatusesCount(), //
								u.getFavouritesCount(), u.getCreatedAt().getTime() / 1000, addv,//
								u.getVerifiedReason(), u.getBiFollowersCount(), isactivefans, u.getWeihao(), "");
					}
					// 注意在lastwbcreatedat一列存储的是粉丝是否活跃信息
					/******************年龄分析******************/
					age[fansUtils.checkAge(weibosum)]++;
					/***********Top40粉丝UID（按粉丝量）***********/
					top40fans = InsertSort.toptable(top40fans, username + "=" + fanssum);
				}
				cursor++;
				followers = fm.getFollowersById(uid, 200, cursor * 200);
			}
			myjdbc.sqlClose();
		}
		/******************年龄分析*****************/
		HashMap<String, String> agedist = this.ageArrange(age, existuids);

		System.out.println(JSONArray.fromObject(agedist));

		/****************粉丝的标签数据***************/
		HashMap<String, HashMap<String, String>> fanstags = UserTags.getFansTags(accesstoken, top40fans);

		System.out.println(JSONArray.fromObject(fanstags));

		/***********粉丝一周内的原创微博关键词************/
		WeiboInfo weiboInfo = new WeiboInfo();
		String texts = "";
		long timestamp = 7 * 86400;
		for (int i = 0; i < top40fans.length; i++) {
			texts = texts
					+ weiboInfo.lastWeiboTexts(top40fans[i].substring(0, top40fans[i].indexOf("=")), accesstoken,
							timestamp);
		}
		KeyWord mykeyword = new KeyWord();
		String keywords = mykeyword.extractKeyword(texts, "", 50);

		System.out.println(keywords);

		return 0;
	}

	public HashMap<String, String> ageArrange(int[] age, int existuids) {

		HashMap<String, String> result = new HashMap<String, String>();
		result.put("others", Float.toString((float) Math.round(((float) age[0] / existuids) * 10000) / 100) + "%");
		result.put("80s", Float.toString((float) Math.round(((float) age[1] / existuids) * 10000) / 100) + "%");
		result.put("90s", Float.toString((float) Math.round(((float) age[2] / existuids) * 10000) / 100) + "%");
		result.put("70s", Float.toString((float) Math.round(((float) age[3] / existuids) * 10000) / 100) + "%");
		result.put("60s", Float.toString((float) Math.round(((float) age[4] / existuids) * 10000) / 100) + "%");
		return result;
	}

}
