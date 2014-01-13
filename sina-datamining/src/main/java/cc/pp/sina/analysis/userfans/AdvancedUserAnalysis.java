package cc.pp.sina.analysis.userfans;

import java.io.Serializable;
import java.sql.SQLException;

import net.sf.json.JSONArray;
import cc.pp.sina.algorithms.InsertSort;
import cc.pp.sina.jdbc.UserJDBC;
import cc.pp.sina.result.AdvancedUserResult;

import com.sina.weibo.api.Friendships;
import com.sina.weibo.api.Users;
import com.sina.weibo.model.User;
import com.sina.weibo.model.UserWapper;
import com.sina.weibo.model.WeiboException;

/**
 * Title: 高级版用户分析
 * @author wanggang
 * @version 1.1
 * @since 2013-05-27
 */
public class AdvancedUserAnalysis implements Serializable {

	/**
	 * 默认序列化版本号
	 */
	private static final long serialVersionUID = 1L;
	
	private String ip = "";
	
	public AdvancedUserAnalysis(String ip) {
		this.ip = ip;
	}

	/**
	 * 测试函数
	 * @param args
	 * @throws SQLException 
	 * @throws WeiboException 
	 */
	public static void main(String[] args) throws WeiboException, SQLException {

		AdvancedUserAnalysis ua = new AdvancedUserAnalysis("192.168.1.151");
		String username = new String("2981827167");
		String[] result = ua.analysis(username);
		System.out.println(result[0]);
		System.out.println(result[1]);

	}
	
	/**
	 * 分析函数
	 * @param uid
	 * @return
	 * @throws WeiboException
	 * @throws SQLException
	 */
	public String[] analysis(String uid) throws WeiboException, SQLException {
		
		String[] tworesult = new String[2];
		JSONArray jsonresult = null;
		UserJDBC usermysql = new UserJDBC(ip);
		int apicount = 0;
		if (usermysql.mysqlStatus()) 
		{
			AdvancedUserResult result = new AdvancedUserResult();
			/********获取accesstoken和用户username*********/
			String accesstoken = usermysql.getAccessToken();
			/*****************采集用户信息******************/
			Users um = new Users();
			um.client.setToken(accesstoken);
			User user = um.showUserById(uid);
			apicount++;
			if (user.getStatusCode().equals("20003")) {
				tworesult[0] = "20003";
				tworesult[0] = "0";
				return tworesult;
			}
			System.out.println(user);
			System.out.println(user.getFollowersCount());
			result.setFansCount(Long.toString(user.getFollowersCount()));
			int userfanssum = user.getFollowersCount();
			int userweibosum = user.getStatusesCount();
			long usercreateday = user.getCreatedAt().getTime()/1000;
			/******************存储用户数据******************/
			usermysql.insertUserInfo(user.getId(), user.getName(), user.getLocation(), 
					user.getProfileImageUrl(), user.getGender(), user.getFollowersCount(),
					user.getStatusesCount(), user.getFriendsCount(), 
					user.getFavouritesCount());
			/****************采集用户粉丝信息****************/
			Friendships fm = new Friendships();
			fm.client.setToken(accesstoken);
			int cursor = 0;
			UserWapper followers = fm.getFollowersById(uid, 200, cursor*200);
			apicount++;
			/******************00、用户粉丝量******************/
			int existuids = 0;
			int[] province = new int[101];
			int[] ishead = new int[2];
			int[] gender = new int[2];
			int[] fansclassfans = new int[5];
			int[] fansclasswb = new int[5];
			int[] quality = new int[2];
			int[] fansclasstime = new int[6];
			int averagewb = 0;
			int[] verifiedratio = new int[2];
			int[] verifiedtype = new int[14];
			int[] fansclassactive = new int[5];
			int flag = 0;
			String[] top50maskusers = new String[50];
			for (int i = 0; i < 50; i++) {
				top50maskusers[i] = "0";
			}
			String[] top50fans = new String[50];
			for (int i = 0; i < 50; i++) {
				top50fans[i] = "0=0";
			}
			String[] top50wb = new String[50];
			for (int i = 0; i < 50; i++) {
				top50wb[i] = "0=0";
			}
			String[] top50time = new String[50];
			for (int i = 0; i < 50; i++) {
				top50time[i] = "0=0";
			}
			String[] top50active = new String[50];
			for (int i = 0; i < 50; i++) {
				top50active[i] = "0=0";
			}
			String[] top50avgwb = new String[50];
			for (int i = 0; i < 50; i++) {
				top50avgwb[i] = "0=0";
			}
			
			String username;
			int city;
			String headurl;
			String sex;
			int fanssum;
			int weibosum;
			long createday;
			boolean addv;
			int verifytype;

            /****************循环计算******************/
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
					headurl = u.getProfileImageUrl().substring(u.getProfileImageUrl().length() - 1);
					sex = u.getGender();
					fanssum = u.getFollowersCount();
					weibosum = u.getStatusesCount();
					createday = u.getCreatedAt().getTime()/1000;
					addv = u.isVerified();
					verifytype = u.getVerifiedType();
					/******************存储粉丝数据******************/
					usermysql.insertUserInfo(u.getId(), u.getName(), u.getLocation(), u.getProfileImageUrl(), 
							u.getGender(), u.getFollowersCount(), u.getStatusesCount(), u.getFriendsCount(), u.getFavouritesCount());
					/******************1、区域分析******************/
					if (city == 400) {
						province[0]++;
					} else {
						province[city]++;
					}
					/******************2、头像分析******************/
					if (headurl.equals("0")) {
						ishead[0]++;
					} else {
						ishead[1]++;
					}
					/******************3、性别分析******************/
					if (sex.equals("m")) {
						gender[0]++; //0----m
					} else {
						gender[1]++; //1----f
					}
					/************4、用户等级分析（按粉丝量）*************/
					if (fanssum < 100) {
						fansclassfans[0]++;  //0-----"X<100"
					} else if (fanssum < 1000) {
						fansclassfans[1]++;  //1-----"100<X<1000"
					} else if (fanssum < 10000) {
						fansclassfans[2]++;  //2-----"1000<X<1w"
					} else if (fanssum < 100000) {
						fansclassfans[3]++;  //3-----"1w<X<10w"
					} else {
						fansclassfans[4]++;  //4-----"X>10w"
					}
					/************5、用户等级分析（按微博量）*************/
					if (weibosum < 100) {
						fansclasswb[0]++;   //0----"X<100"
					} else if (weibosum < 1000) {
						fansclasswb[1]++;   //1----"100<X<1000"
					} else if (weibosum < 5000) {
						fansclasswb[2]++;   //2----"1000<X<5000"
					} else if (weibosum < 10000) {
						fansclasswb[3]++;   //3----"5000<X<1w"
					} else { 
						fansclasswb[4]++;   //4----"X>1w"
					}
					/***********6、Top50粉丝UID（按粉丝量）************/
					top50fans = InsertSort.toptable(top50fans, username + "=" + fanssum);
					/***********7、Top50粉丝UID（按微博量）************/
					top50wb = InsertSort.toptable(top50wb, username + "=" + weibosum);
					/******************8、水军分析******************/
					/***************9、Top50水军UID****************/
					/******************10、认证比例******************/
					if (addv == false) {
						if (fanssum < 50 || weibosum < 20) {
							quality[0]++;
						} else if ((fanssum*3 < weibosum) && (fanssum < 150)) {
							quality[0]++;
						} else {
							quality[1]++;
						}
						verifiedratio[0]++;
						if ((fanssum < 10 || weibosum < 10) && (flag < 50)) {
							top50maskusers[flag++] = username;
						}
					} else {
						quality[1]++;
						verifiedratio[1]++;
					}
					/***********11、用户等级分析（按创建时间）*************/
					int timediff = 1;
					if (System.currentTimeMillis()/1000 - createday > 86400) {
						timediff = (int) ((System.currentTimeMillis()/1000 - createday)/86400);
					}
					if (timediff < 30 * 1) {
						fansclasstime[0]++;   //0----"X<1个月"
					} else if (timediff < 30 * 6) {
						fansclasstime[1]++;   //1----"1个月<X<半年"
					} else if (timediff < 30 * 12) {
						fansclasstime[2]++;   //2----"半年<X<1年"
					} else if (timediff < 30 * 18) {
						fansclasstime[3]++;   //3----"1年<X<1.5年"
					} else if (timediff < 30 * 24) {
						fansclasstime[4]++;   //4----"1.5年X<2年"
					} else {
						fansclasstime[5]++;   //5----"X>2年"
					}
					/**********12、Top50粉丝UID（按创建时间）************/
					top50time = InsertSort.toptable(top50time, username + "=" + timediff);
					/******************平均每天发博量******************/
					averagewb = weibosum / timediff;
					/*********14、Top50粉丝UID（按平均发博量）***********/
					top50avgwb = InsertSort.toptable(top50avgwb, username + "=" + averagewb);
					/******************15、认证分布******************/
					if ((verifytype < 9)&&(verifytype >= 0)) {
						verifiedtype[verifytype]++;
					} else if (verifytype == 200) {
						verifiedtype[9]++;
					} else if (verifytype == 220) {
						verifiedtype[10]++;
					} else if (verifytype == 400) {
						verifiedtype[11]++;
					} else if (verifytype == -1) {
						verifiedtype[12]++;
					} else {
						verifiedtype[13]++;
					}
					/************16、用户等级分析（按活跃度）*************/
					float actvalue = (float) (1 - 0.4/(1+(float)fanssum/timediff) - 0.6/(1+averagewb));
					int fansactive = Math.round(Math.abs(actvalue) * 10);
					if (fansactive <= 2) {
						fansclassactive[0]++;   //0----"X<=2"
					} else if (fansactive <= 4) {
						fansclassactive[1]++;   //0----"2<X<=4"
					} else if (fansactive <= 6) {
						fansclassactive[2]++;   //0----"4<X<=6"
					} else if (fansactive <= 8) {
						fansclassactive[3]++;   //0----"6<X<=8"
					} else {
						fansclassactive[4]++;   //0----"X>8"
					}
					/************17、用户等级分析（按活跃度）*************/
					top50active = InsertSort.toptable(top50active, username + "=" + fansactive);					
				}
				cursor++;
				followers = fm.getFollowersById(uid, 200, cursor*200);
				apicount++;
			}
			/*****************0、粉丝存在比例*****************/
			if (user.getFollowersCount() > 5000) {
				result.setExistuids(Float.toString((float)Math.round(((float)existuids/5000)*10000)/100) + "%");
			} else {
				result.setExistuids(Float.toString((float)Math.round(((float)existuids/user.getFollowersCount())*10000)/100) + "%");
			}
			/******************1、区域分析******************/
			if (province[0] != 0) {
				float temp = (float)province[0] / existuids;
				result.getProvince().put("400", Float.toString((float)Math.round(temp*10000)/100) + "%");
			}
			for (int j = 1; j < province.length; j++) {
				if (province[j] != 0) {
					float temp1 = (float)province[j] / existuids;
					result.getProvince().put(Integer.toString(j), Float.toString((float)Math.round(temp1*10000)/100) + "%");
				}
			}
			/******************2、头像分析******************/
			float temp = (float)ishead[0] / existuids;
			result.getIsHead().put("no", Float.toString((float)Math.round(temp*10000)/100) + "%");
			result.getIsHead().put("yes", Float.toString((float)Math.round((1-temp)*10000)/100) + "%");
			/******************3、性别分析******************/
			temp = (float)gender[0] / existuids;
			result.getGender().put("m", Float.toString((float)Math.round(temp*10000)/100) + "%");
			result.getGender().put("f", Float.toString((float)Math.round((1-temp)*10000)/100) + "%");
			/************4、用户等级分析（按粉丝量）*************/
			result.getGradeByFans().put("<100", Float.toString((float)Math.round(((float)fansclassfans[0]/existuids)*10000)/100) + "%");
			result.getGradeByFans().put("100~1000", Float.toString((float)Math.round(((float)fansclassfans[1]/existuids)*10000)/100) + "%");
			result.getGradeByFans().put("1000~1w", Float.toString((float)Math.round(((float)fansclassfans[2]/existuids)*10000)/100) + "%");
			result.getGradeByFans().put("1w~10w", Float.toString((float)Math.round(((float)fansclassfans[3]/existuids)*10000)/100) + "%");
			result.getGradeByFans().put(">10w", Float.toString((float)Math.round(((float)fansclassfans[4]/existuids)*10000)/100) + "%");
			/************5、用户等级分析（按微博量）*************/
			result.getGradeByWb().put("<100", Float.toString((float)Math.round(((float)fansclasswb[0]/existuids)*10000)/100) + "%");
			result.getGradeByWb().put("100~1000", Float.toString((float)Math.round(((float)fansclasswb[1]/existuids)*10000)/100) + "%");
			result.getGradeByWb().put("1000~5000", Float.toString((float)Math.round(((float)fansclasswb[2]/existuids)*10000)/100) + "%");
			result.getGradeByWb().put("5000~1w", Float.toString((float)Math.round(((float)fansclasswb[3]/existuids)*10000)/100) + "%");
			result.getGradeByWb().put(">1w", Float.toString((float)Math.round(((float)fansclasswb[4]/existuids)*10000)/100) + "%");
			/***********6、Top50粉丝UID（按粉丝量）************/
			for (int n = 0; n < 50; n++) {
				if (top50fans[n].length() > 5) {
					result.getTopsByFans().put(Integer.toString(n), top50fans[n].substring(0, top50fans[n].indexOf("=")));
				} else {
					break;
				}
			}
			/***********7、Top50粉丝UID（按微博量）************/
			for (int n = 0; n < 50; n++) {
				if (top50wb[n].length() > 5) {
					result.getTopsByWb().put(Integer.toString(n), top50wb[n].substring(0, top50wb[n].indexOf("=")));
				} else {
					break;
				}
			}
			/******************8、水军分析******************/
			temp = (float)quality[0] / existuids;
			result.getQuality().put("mask", Float.toString((float)Math.round(temp*10000)/100) + "%");
			result.getQuality().put("real", Float.toString((float)Math.round((1 - temp)*10000)/100) + "%");
			/***************9、Top50水军UID****************/
			for (int n = 0; n < 50; n++) {
				if (top50maskusers[n].length() > 5) {
					result.getTopsMaskers().put(Integer.toString(n), top50maskusers[n]);
				} else {
					break;
				}
			}
			/******************10、认证比例******************/
			temp = (float)verifiedratio[1] / existuids;
			result.getVerifiedRatio().put("1", Float.toString((float)Math.round(temp*10000)/100) + "%");
			result.getVerifiedRatio().put("0", Float.toString((float)Math.round((1-temp)*10000)/100) + "%");
			/***********11、用户等级分析（按创建时间）*************/
			result.getGradeByTime().put("<1month", Float.toString((float)Math.round(((float)fansclasstime[0]/existuids)*10000)/100) + "%");
			result.getGradeByTime().put("1~6months", Float.toString((float)Math.round(((float)fansclasstime[1]/existuids)*10000)/100) + "%");
			result.getGradeByTime().put("6~12months", Float.toString((float)Math.round(((float)fansclasstime[2]/existuids)*10000)/100) + "%");
			result.getGradeByTime().put("12~18months", Float.toString((float)Math.round(((float)fansclasstime[3]/existuids)*10000)/100) + "%");
			result.getGradeByTime().put("18~24months", Float.toString((float)Math.round(((float)fansclasstime[4]/existuids)*10000)/100) + "%");
			result.getGradeByTime().put(">24months", Float.toString((float)Math.round(((float)fansclasstime[5]/existuids)*10000)/100) + "%");
			/**********12、Top50粉丝UID（按创建时间）************/
			for (int n = 0; n < 50; n++) {
				if (top50time[n].length() > 5) {
					result.getTopsByTime().put(Integer.toString(n), top50time[n].substring(0, top50time[n].indexOf("=")));
				}
			}
			/*************13、该用户的平均每天发博量**************/
			int usertimediff = 1;
			if (System.currentTimeMillis()/1000 - usercreateday > 86400) {
				usertimediff = (int) ((System.currentTimeMillis()/1000 - usercreateday)/86400);
			}
			float avgweibo = (float) userweibosum/usertimediff;
			result.setAverageWeibos(Float.toString((float)Math.round(avgweibo*100)/100));
			/*********14、Top50粉丝UID（按平均发博量）***********/
			for (int n = 0; n < 50; n++) {
				if (top50avgwb[n].length() > 5) {
					result.getTopsByAvgwb().put(Integer.toString(n), top50avgwb[n].substring(0, top50avgwb[n].indexOf("=")));
				}
			}
			/******************15、认证分布******************/
			if (verifiedtype[13] != 0) {
				result.getVerifiedType().put("other", Float.toString((float)Math.round(((float)verifiedtype[13]/existuids)*10000)/100) + "%");
			}
			if (verifiedtype[12] != 0) {
				result.getVerifiedType().put("-1", Float.toString((float)Math.round(((float)verifiedtype[12]/existuids)*10000)/100) + "%");
			}
			if (verifiedtype[11] != 0) {
				result.getVerifiedType().put("400", Float.toString((float)Math.round(((float)verifiedtype[11]/existuids)*10000)/100) + "%");
			}
			if (verifiedtype[10] != 0) {
				result.getVerifiedType().put("220", Float.toString((float)Math.round(((float)verifiedtype[10]/existuids)*10000)/100) + "%");
			}
			if (verifiedtype[9] != 0) {
				result.getVerifiedType().put("200", Float.toString((float)Math.round(((float)verifiedtype[9]/existuids)*10000)/100) + "%");
			}
			for (int n7 = 0; n7 < 9; n7++) {
				if (verifiedtype[n7] != 0) {
					result.getVerifiedType().put(Integer.toString(n7), Float.toString((float)Math.round(((float)verifiedtype[n7]/existuids)*10000)/100) + "%");
				}
			}
			/************16、用户等级分析（按活跃度）*************/
			result.getGradeByActive().put("<2", Float.toString((float)Math.round(((float)fansclassactive[0]/existuids)*10000)/100) + "%");
			result.getGradeByActive().put("2~4", Float.toString((float)Math.round(((float)fansclassactive[1]/existuids)*10000)/100) + "%");
			result.getGradeByActive().put("4~6", Float.toString((float)Math.round(((float)fansclassactive[2]/existuids)*10000)/100) + "%");
			result.getGradeByActive().put("6~8", Float.toString((float)Math.round(((float)fansclassactive[3]/existuids)*10000)/100) + "%");
			result.getGradeByActive().put(">8", Float.toString((float)Math.round(((float)fansclassactive[4]/existuids)*10000)/100) + "%");
			/************17、用户等级分析（按活跃度）*************/
			for (int n = 0; n < 50; n++) {
				if (top50active[n].length() > 5) {
					result.getTopsByActive().put(Integer.toString(n), top50active[n].substring(0, top50active[n].indexOf("=")));
				}
			}
			/***************18、该用户的活跃度*****************/
			float active = 0.00f;
			float alpha = 0.4f;
			float beta = 0.6f;
			active = 1 - alpha/(1+(float)userfanssum/usertimediff) - beta/(1+(float)userweibosum/usertimediff);
			result.setActivation(Integer.toString(Math.round(Math.abs(active)*10)));
			/**************19、该用户的微博创建时间****;*********/
			result.setCreateLong(Integer.toString(usertimediff));
			/*****************20、该用户的影响力***************/
			float inf = 0.00f;
//			inf = (float)(1 - 1/(1+Math.log10(existuids + 1)));
//			result.setInfluence(Integer.toString(Math.round(inf*100)));
			float ifollowers = 0.0f;
			float iflowersclass = 0.0f;
			if (user.getFollowersCount() > 5000) {
				ifollowers = ((float)quality[0] / 5000)*user.getFollowersCount();
				iflowersclass = (float)(fansclassfans[2]+fansclassfans[3]+fansclassfans[4])/5000;
			} else {
				ifollowers = quality[1];
				iflowersclass = (float)(fansclassfans[2]+fansclassfans[3]+fansclassfans[4])/user.getFollowersCount();
			}
			inf = (float) (0.8*(Math.log(ifollowers)/Math.log(1.5)) + 0.1*Math.abs(Math.log(iflowersclass)/Math.log(2)) + 0.1*Math.abs(Math.log(temp)/Math.log(2)));
			if (inf > 100) {
				inf = 10;
			}
			result.setInfluence(Integer.toString(Math.round(inf*100/30)));
			/*******************数据转换与存储*****************/
			jsonresult = JSONArray.fromObject(result);
			usermysql.insertResult(uid, user.getFollowersCount(), jsonresult.toString());
			
			usermysql.sqlClose();
		}
		tworesult[0] = jsonresult.toString();
		tworesult[1] = Integer.toString(apicount);
		
		return tworesult;
	}

}

