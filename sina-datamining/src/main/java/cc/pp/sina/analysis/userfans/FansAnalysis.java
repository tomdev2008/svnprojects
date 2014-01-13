package cc.pp.sina.analysis.userfans;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import net.sf.json.JSONArray;
import cc.pp.sina.algorithms.InsertSort;
import cc.pp.sina.algorithms.PPSort;
import cc.pp.sina.jdbc.UserJDBC;
import cc.pp.sina.result.FansResult;

import com.sina.weibo.api.Friendships;
import com.sina.weibo.api.Tags;
import com.sina.weibo.api.Users;
import com.sina.weibo.model.TagWapper;
import com.sina.weibo.model.User;
import com.sina.weibo.model.UserWapper;
import com.sina.weibo.model.WeiboException;

public class FansAnalysis implements Serializable {

	/**
	 * 默认序列化版本号
	 */
	private static final long serialVersionUID = 1L;

	private String ip = "";

	/**
	 * 构造函数
	 * @param ip
	 */
	public FansAnalysis(String ip) {
		this.ip = ip;
	}

	/**
	 * 测试函数
	 * @param args
	 * @throws SQLException 
	 * @throws WeiboException 
	 */
	public static void main(String[] args) throws WeiboException, SQLException {

		FansAnalysis ua = new FansAnalysis("127.0.0.1");
		String uid = "1644395354";
		int index = ua.analysis(uid);
		System.out.println(index);
	}

	/**
	 * 分析函数
	 * @param uid
	 * @return
	 * @throws WeiboException
	 * @throws SQLException
	 */
	public int analysis(String uid) throws WeiboException, SQLException {

		JSONArray jsonresult = null;
		UserJDBC usermysql = new UserJDBC(this.ip);
		int apicount = 0;
		if (usermysql.mysqlStatus()) 
		{
			FansUtils fansUtils = new FansUtils();
			FansResult result = new FansResult();
			/********获取accesstoken和用户username*********/
			String accesstoken = usermysql.getAccessToken();
			/*****************采集用户信息******************/
			Users um = new Users();
			um.client.setToken(accesstoken);
			User user = um.showUserById(uid);
			apicount++;
			if (user.getStatusCode().equals("20003")) {
				return -1;
			}
			/****************采集用户粉丝信息****************/
			Friendships fm = new Friendships();
			fm.client.setToken(accesstoken);
			int cursor = 0;
			UserWapper followers = null;
			try {
				followers = fm.getFollowersById(uid, 200, cursor*200);
			} catch (RuntimeException e) {
				return -1;
			}
			apicount++;
			/******************初始化变量******************/
			int existuids = 0;
			// 1、粉丝总数
			int userfanssum = user.getFollowersCount();
			result.setFanssum(Integer.toString(userfanssum));
			// 2、活跃粉丝数 
			// 3、粉丝使用的终端设备来源分布
			int[] verifiedtype = new int[14];  // 4、认证分布
			// 5、粉丝的热门标签
			int[] gradebyfans = new int[5];  // 6、粉丝等级分布（按粉丝量）
			int[] age = new int[5]; // 年龄分析
			int[] province = new int[101]; // 区域分析
			int[] gender = new int[2];            // 性别分析
			int[] quality = new int[2];           // 水军分析
			int[] verifiedratio = new int[2];     // 认证比例
			String[] top40fans = new String[40];  // top40粉丝，按照粉丝量
			fansUtils.initTopArray(top40fans);
			String[] top10uids = new String[10];   // 存放待分析的用户名，用户分析这些用户的发博时间线
			fansUtils.initTopArray(top10uids);
			int city, fanssum, weibosum, verifytype;
			String sex, username;
			boolean addv;

			while ((cursor*200 < user.getFollowersCount() + 200) && (cursor < 26))
			{
				for(User u : followers.getUsers())
				{
					/*****************0、粉丝存在数*****************/
					if (u.getName() != null) {
						existuids++;
					} else {
						continue;
					}
					username = u.getId();
					city = u.getProvince();
					sex = u.getGender();
					fanssum = u.getFollowersCount();
					weibosum = u.getStatusesCount();
					addv = u.isVerified();
					verifytype = u.getVerifiedType();
					/******************1、年龄分析******************/
					age[fansUtils.checkAge(weibosum)]++;
					/****************存放待分析的用户名****************/
					top10uids = InsertSort.toptable(top10uids, username + "=" + weibosum);
					/******************2、区域分析******************/
					province[fansUtils.checkLocation(city)]++;
					/******************3、性别分析******************/
					gender[fansUtils.checkGender(sex)]++;
					/***********4、Top40粉丝UID（按粉丝量）************/
					top40fans = InsertSort.toptable(top40fans, username + "=" + fanssum);
					/******************5、认证比例*****************/
					verifiedratio[fansUtils.checkVerified(addv)]++;
					/******************6、水军分析*****************/
					quality[fansUtils.checkQuality(addv, fanssum, weibosum)]++;
					/************4、用户等级分析（按粉丝量）*************/
					gradebyfans[fansUtils.checkFansGrade(fanssum)]++;
					/******************15、认证分布*****************/
					verifiedtype[fansUtils.checkVerifiedType(verifytype)]++;
				}
				cursor++;
				followers = fm.getFollowersById(uid, 200, cursor*200);
				apicount++;
			}
			/**************0、分析粉丝微博时间线和终端设备分布**************/
			result.setActivetimeline(this.getSwbAnalysis(top10uids, result));
			/**************1、年龄分析（按微博量）**************/
			fansUtils.ageArrange(result, age, existuids);
			/******************2、区域分析******************/
			fansUtils.locationArrange(result, province, existuids);
			/******************3、性别分析******************/
			fansUtils.genderArrange(result, gender, existuids);
			/***********4、Top40粉丝UID（按粉丝量）************/
			fansUtils.topFansArrange(result, top40fans);
			/******************5、水军分析******************/
			fansUtils.qualityArrange(result, quality, existuids);
			/******************6、认证比例******************/
			result.setAddVRatio(Float.toString((float)Math.round(((float)verifiedratio[1]/existuids)*10000)/100) + "%");
			/****************7、粉丝增长趋势******************/
			result.setFansaddtimeline(usermysql.getFansAddInfo(uid));
			/************4、用户等级分析（按粉丝量）*************/
			fansUtils.fansGradeArrange(result, gradebyfans, existuids);
			/******************15、认证分布******************/
			fansUtils.verifiedTypeArrange(result, verifiedtype, existuids);
			/****************活跃粉丝数******************/
			fansUtils.activeCountArrange(result, quality, userfanssum, existuids);
			/****************用户标签信息******************/
			result.setHottags(this.getFansTags(accesstoken, top40fans));
			/******************数据转换与存储****************/
			jsonresult = JSONArray.fromObject(result);
			/**
			 * 数据存储到154服务器上
			 */
			//			UserJDBC mysql = new UserJDBC(154);
			//			if (mysql.mysqlStatus()) {
			//				mysql.insertFansResult(uid, jsonresult.toString());
			//				mysql.sqlClose();
			//			}
			System.out.println(jsonresult);

			usermysql.sqlClose();
		}		
		
		return apicount;
	}

	/**
	 * 获取粉丝的24小时的活跃时间线
	 * @param top10uids
	 * @return
	 * @throws SQLException 
	 * @throws WeiboException 
	 */
	public HashMap<String,String> getSwbAnalysis(String[] top10uids, FansResult results) 
			throws WeiboException, SQLException {

		String[] uids = InsertSort.trans(top10uids);
		SWeiboAnalysis swa = new SWeiboAnalysis(this.ip);
		int[] wbfenbubyhour = swa.analysis(uids); // 24小时内的微博发布分布
		/********************微博来源分布**********************/
		results.getSource().put("sina",
				Float.toString((float) Math.round(((float) swa.wbsource[0] / swa.sum) * 10000) / 100) + "%");
		results.getSource().put("pc",
				Float.toString((float) Math.round(((float) swa.wbsource[1] / swa.sum) * 10000) / 100) + "%");
		results.getSource().put("android",
				Float.toString((float) Math.round(((float) swa.wbsource[2] / swa.sum) * 10000) / 100) + "%");
		results.getSource().put("iphone",
				Float.toString((float) Math.round(((float) swa.wbsource[3] / swa.sum) * 10000) / 100) + "%");
		results.getSource().put("ipad",
				Float.toString((float) Math.round(((float) swa.wbsource[4] / swa.sum) * 10000) / 100) + "%");
		results.getSource().put("others",
				Float.toString((float) Math.round(((float) swa.wbsource[5] / swa.sum) * 10000) / 100) + "%");

		int sum = 0;
		for (int i = 0; i < wbfenbubyhour.length; i++) {
			sum += wbfenbubyhour[i];
		}
		HashMap<String,String> result = new HashMap<String,String>();
		for (int i = 0; i < wbfenbubyhour.length; i++) {
			result.put(Integer.toString(i), Float.toString((float)Math.round(((float)wbfenbubyhour[i]/sum)*10000)/100) + "%");
		}

		return result;
	}
	
	/**
	 * 获取用户标签信息
	 * @param accesstoken
	 * @param top40fans
	 * @return
	 * @throws WeiboException
	 */
	public HashMap<String,HashMap<String,String>> getFansTags(String accesstoken, String[] top40fans) throws WeiboException {

		HashMap<String, Integer> tagsandsum = new HashMap<String, Integer>();
		String uids1 = "", uids2 = "";
		for (int i = 0; i < top40fans.length; i++) {
			if (top40fans[i] != null) {
				if (i < 20)
					uids1 = uids1 + top40fans[i] + ",";
				else
					uids2 = uids2 + top40fans[i] + ",";
			} else {
				break;
			}
		}
		uids1 = uids1.substring(0, uids1.length() - 1);
		Tags tm = new Tags();
		tm.client.setToken(accesstoken);
		if (uids1.length() > 2) {
			this.transTags(tm, tagsandsum, uids1);
		}
		if (top40fans[20] != null) {
			uids2 = uids2.substring(0, uids2.length() - 1);
			this.transTags(tm, tagsandsum, uids2);
		}
		HashMap<String, HashMap<String, String>> result = PPSort.sortedToDoubleMap(tagsandsum, "tag", "weight", 20);

		return result;
	}

	/**
	 * 采集用户标签信息
	 * @param tm
	 * @param tagsandsum
	 * @param uids
	 * @throws WeiboException
	 */
	public void transTags(Tags tm, HashMap<String, Integer> tagsandsum, String uids) throws WeiboException {

		List<TagWapper> tags = tm.getTagsBatch(uids);
		String value, weight;
		for (int j = 0; j < tags.size(); j++) {
			for (int i = 0; i < tags.get(j).getTags().size(); i++) {
				value = tags.get(j).getTags().get(i).getValue();
				weight = tags.get(j).getTags().get(i).getWeight();
				if (tagsandsum.get(value) == null) {
					tagsandsum.put(value, Integer.parseInt(weight));
				} else {
					tagsandsum.put(value, tagsandsum.get(value) + Integer.parseInt(weight));
				}
			}
		}
	}
}
