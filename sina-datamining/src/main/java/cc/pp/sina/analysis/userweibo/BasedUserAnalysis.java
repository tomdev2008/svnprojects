package cc.pp.sina.analysis.userweibo;

import java.io.Serializable;
import java.sql.SQLException;

import net.sf.json.JSONArray;
import cc.pp.sina.jdbc.UserJDBC;
import cc.pp.sina.result.BasedUserResult;

import com.sina.weibo.api.Friendships;
import com.sina.weibo.api.Users;
import com.sina.weibo.model.User;
import com.sina.weibo.model.UserWapper;
import com.sina.weibo.model.WeiboException;

/**
 * Title: 基础版用户分析
 * @author wanggang
 * @version 1.1
 * @since 2013-05-27
 */
public class BasedUserAnalysis implements Serializable {

	/**
	 * 默认序列化版本号
	 */
	private static final long serialVersionUID = 1L;
	
	private String ip = "";

	/**
	 * 构造函数
	 * @param ip
	 */
	public BasedUserAnalysis(String ip) {
		this.ip = ip;
	}

	/**
	 * 测试函数
	 * @param args
	 * @throws SQLException 
	 * @throws WeiboException 
	 */
	public static void main(String[] args) throws WeiboException, SQLException {

		BasedUserAnalysis ua = new BasedUserAnalysis("192.168.1.151");
		String uid = new String("2116921732");
		long time = System.currentTimeMillis();
		String[] result = ua.analysis(uid);
		System.out.println((System.currentTimeMillis() - time)/1000);
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
			BasedUserResult result = new BasedUserResult();
			/********获取accesstoken和用户username*********/
			String accesstoken = usermysql.getAccessToken();
			/*****************采集用户信息******************/
			Users um = new Users();
			um.client.setToken(accesstoken);
			User user = um.showUserById(uid);
			apicount++;
			if (user.getStatusCode().equals("20003")) {
				tworesult[0] = "20003";
				tworesult[1] = "0";
				return tworesult;
			}
			result.setFanscount(Long.toString(user.getFollowersCount()));
			int userfanssum = user.getFollowersCount();
			int userweibosum = user.getStatusesCount();
			long usercreateday = user.getCreatedAt().getTime()/1000;	
			System.out.println(userfanssum);
			/****************采集用户粉丝信息****************/
			Friendships fm = new Friendships();
			fm.client.setToken(accesstoken);
			int cursor = 0;
			UserWapper followers = fm.getFollowersById(uid, 200, cursor*200);
			System.out.println(followers.getUsers().toString());
			apicount++;
			/******************00、用户粉丝量******************/
			int existuids = 0;
			int[] province = new int[101];
			int[] ishead = new int[2];
			int[] gender = new int[2];
			int[] fansclassforfans = new int[5];
			int[] quality = new int[2];
			int[] verifiedratio = new int[2];
			int[] verifiedtype = new int[14];
			
			int city;
			String headurl;
			String sex;
			int fanssum;
			int weibosum;
			@SuppressWarnings("unused")
			long createday;
			boolean addv;
			int verifytype;

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
					//33、1、m、210、74、1332737132、false、-1
					city = u.getProvince();
					headurl = u.getProfileImageUrl().substring(u.getProfileImageUrl().length() - 1);
					sex = u.getGender();
					fanssum = u.getFollowersCount();
					weibosum = u.getStatusesCount();
					createday = u.getCreatedAt().getTime()/1000;
					addv = u.isVerified();
					verifytype = u.getVerifiedType();
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
						fansclassforfans[0]++;  //0-----"X<100"
					} else if (fanssum < 1000) {
						fansclassforfans[1]++;  //1-----"100<X<1000"
					} else if (fanssum < 10000) {
						fansclassforfans[2]++;  //2-----"1000<X<1w"
					} else if (fanssum < 100000) {
						fansclassforfans[3]++;  //3-----"1w<X<10w"
					} else {
						fansclassforfans[4]++;  //4-----"X>10w"
					}
					/************5、用户等级分析（按微博量）*************/
					/***********6、Top50粉丝UID（按粉丝量）************/
					/***********7、Top50粉丝UID（按微博量）************/
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
					} else {
						quality[1]++;
						verifiedratio[1]++;
					}
					/***********11、用户等级分析（按创建时间）*************/
					/**********12、Top50粉丝UID（按创建时间）************/
					/****************13、平均每天发博量****************/
					/*********14、Top50粉丝UID（按平均发博量）***********/
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
					
				}
				cursor++;
				followers = fm.getFollowersById(uid, 200, cursor*200);
				apicount++;
			}
			/*****************0、粉丝存在比例*****************/
			/******************1、区域分析******************/
			if (province[0] != 0) {
				result.getLocation().put("400", Float.toString((float)Math.round(((float)province[0]/existuids)*10000)/100) + "%");
			}
			for (int j = 1; j < province.length; j++) {
				if (province[j] != 0) {
					result.getLocation().put(Integer.toString(j), Float.toString((float)Math.round(((float)province[j]/existuids)*10000)/100) + "%");
				}
			}
			/******************2、头像分析******************/
			result.getIshead().put("no", Float.toString((float)Math.round(((float)ishead[0]/existuids)*10000)/100) + "%");
			result.getIshead().put("yes", Float.toString((float)Math.round(((float)ishead[1]/existuids)*10000)/100) + "%");
			/******************3、性别分析******************/
			result.getGender().put("m", Float.toString((float)Math.round(((float)gender[0]/existuids)*10000)/100) + "%");
			result.getGender().put("f", Float.toString((float)Math.round(((float)gender[1]/existuids)*10000)/100) + "%");
			/************4、用户等级分析（按粉丝量）*************/
			result.getGradeByFans().put("<100", Float.toString((float)Math.round(((float)fansclassforfans[0]/existuids)*10000)/100) + "%");
			result.getGradeByFans().put("100~1000", Float.toString((float)Math.round(((float)fansclassforfans[1]/existuids)*10000)/100) + "%");
			result.getGradeByFans().put("1000~1w", Float.toString((float)Math.round(((float)fansclassforfans[2]/existuids)*10000)/100) + "%");
			result.getGradeByFans().put("1w~10w", Float.toString((float)Math.round(((float)fansclassforfans[3]/existuids)*10000)/100) + "%");
			result.getGradeByFans().put(">10w", Float.toString((float)Math.round(((float)fansclassforfans[4]/existuids)*10000)/100) + "%");
			/************5、用户等级分析（按微博量）*************/
			/***********6、Top50粉丝UID（按粉丝量）************/
			/***********7、Top50粉丝UID（按微博量）************/
			/******************8、水军分析******************/
			result.getFansQuality().put("mask", Float.toString((float)Math.round(((float)quality[0]/existuids)*10000)/100) + "%");
			result.getFansQuality().put("real", Float.toString((float)Math.round(((float)quality[1]/existuids)*10000)/100) + "%");
			/***************9、Top50水军UID****************/
			/******************10、认证比例******************/
			result.setAddVRatio(Float.toString((float)Math.round(((float)verifiedratio[1]/existuids)*10000)/100) + "%");
			/***********11、用户等级分析（按创建时间）*************/
			/**********12、Top50粉丝UID（按创建时间）************/
			/****************13、平均每天发博量****************/
			/*********14、Top50粉丝UID（按平均发博量）***********/
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
			/************17、用户等级分析（按活跃度）*************/
			/****************18、该用户的活跃度******************/
			float active = 0.00f;
			float alpha = 0.4f;
			float beta = 0.6f;
			int usertimediff = 1;
			if (System.currentTimeMillis()/1000 - usercreateday > 86400) {
				usertimediff = (int) ((System.currentTimeMillis()/1000 - usercreateday)/86400);
			}
			active = 1 - alpha/(1+(float)userfanssum/usertimediff) - beta/(1+(float)userweibosum/usertimediff);
			result.setActivation(Integer.toString(Math.round(Math.abs(active)*10)));
			/**************19、该用户的微博创建时间****;************/
			/*****************20、该用户的影响力****************/
			float inf = 0.00f;
//			inf = (float)(1 - 1/(1+Math.log10(existuids + 1)));
//			result.setInfluence(Integer.toString(Math.round(inf*100)));
			float ifollowers = 0.0f;
			float iflowersclass = 0.0f;
			if (user.getFollowersCount() > 5000) {
				ifollowers = ((float)quality[0] / 5000)*user.getFollowersCount();
				iflowersclass = (float)(fansclassforfans[2]+fansclassforfans[3]+fansclassforfans[4])/5000;
			} else {
				ifollowers = quality[1];
				iflowersclass = (float)(fansclassforfans[2]+fansclassforfans[3]+fansclassforfans[4])/user.getFollowersCount();
			}
			inf = (float) (0.8*(Math.log(ifollowers)/Math.log(1.5)) + 0.1*Math.abs(Math.log(iflowersclass)/Math.log(2)) + 0.1*Math.abs(Math.log(((float)verifiedratio[1]/existuids))/Math.log(2)));
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
