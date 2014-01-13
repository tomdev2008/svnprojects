package cc.pp.tencent.analysis.userfans;

import java.io.Serializable;

import net.sf.json.JSONArray;

import org.codehaus.jackson.JsonNode;

import cc.pp.tencent.jdbc.UserJDBC;
import cc.pp.tencent.result.BasedUserResult;
import cc.pp.tencent.utils.JsonUtils;

import com.tencent.weibo.api.FriendsAPI;
import com.tencent.weibo.api.UserAPI;
import com.tencent.weibo.oauthv1.OAuthV1;

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
	private int limit = 1000;

	public BasedUserAnalysis(String ip) {
		this.ip = ip;
	}
	
	/**
	 * 构造函数
	 * @param ip
	 * @param limit
	 */
	public BasedUserAnalysis(String ip, int limit) {
		this.ip = ip;
		this.limit = limit;
	}

	/**
	 * 测试函数
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {

		BasedUserAnalysis sua = new BasedUserAnalysis("192.168.1.151");
		String uid = new String("game9358"); //game9358、tushisi1008、wghwd_9059
		long time = System.currentTimeMillis();
		String[] result = sua.analysis(uid);
		System.out.println((System.currentTimeMillis() - time)/1000);
		System.out.println(result[0]);
		System.out.println(result[1]);

	}

	/**
	 * @分析函数
	 * @param token
	 * @throws Exception
	 */
	public String[] analysis(String uid) throws Exception {

		String[] tworesult = new String[2];
		JSONArray jsonresult = null;
		UserJDBC usermysql = new UserJDBC(ip);
		int apicount = 0;
		if (usermysql.mysqlStatus()) 
		{
			BasedUserResult result = new BasedUserResult();
			/**********************用户授权****************************/
			//		String[] token = {"2c656d405a1d4587b7b99d143102d002","b36e96d43ed75485258f0fdf0cefe143"};
			String[] token = usermysql.getAccessToken();
			OAuthV1 oauth = new OAuthV1();
			BasedUserAnalysis.oauthInit(oauth, token[0], token[1]);
			/**********************用户信息****************************/
			//获取该用户资料
			UserAPI user = new UserAPI(oauth.getOauthVersion());
			String userinfo = user.otherInfo(oauth, "json", uid, "");
			apicount++;
			JsonNode userdata = JsonUtils.getJsonNode(userinfo);

			if (!userdata.get("errcode").toString().equals("0")) {  //返回错误信息处理
				tworesult[0] = "20003";
				tworesult[0] = "0";
				return tworesult;
			}

			int userfanssum = Integer.parseInt(userdata.get("data").get("fansnum").toString());
			result.setFanscount(Long.toString(userfanssum));
			int userweibosum = Integer.parseInt(userdata.get("data").get("tweetnum").toString());
			long usercreateday = Long.parseLong(userdata.get("data").get("regtime").toString());
			int maxcount = userfanssum < this.limit ? userfanssum : this.limit;
			/**********************粉丝信息****************************/
			int existuids = 0;
			int[] province = new int[101];
			int[] ishead = new int[2];
			int[] gender = new int[3];
			int[] fansclassforfans = new int[5];
			int[] quality = new int[2];
			int[] verifiedratio = new int[2];
			int[] viptype = new int[14];

			String citys;
			int city;
			String headurl;
			int sex;
			int fanssum;
			int addv;
			int isvip;    //是否为名人用户
			int cursor;   //页码
			String nextstartpos;
			JsonNode jsondata;
			JsonNode jsoninfo;

			FriendsAPI userfriends = new FriendsAPI(oauth.getOauthVersion());
			cursor = 0;
			nextstartpos = "0";
			while (cursor*30 < maxcount) 
			{
				String baseinfo = userfriends.userFanslist(oauth, "json", "30", nextstartpos, uid, "", "1", "0");
				apicount++;
				cursor++;
				jsondata = JsonUtils.getJsonNode(baseinfo);
				nextstartpos = jsondata.get("data").get("nextstartpos").toString();
				jsoninfo = jsondata.get("data").get("info");

				for (int i = 0; i < Integer.parseInt(jsondata.get("data").get("curnum").toString()); i++) 
				{
					/*****************0、粉丝存在数*****************/
					if (jsoninfo.get(i) != null) {
						existuids++;
					} else {
						continue;
					}
					//exp：经验值，level：微博等级
					headurl = jsoninfo.get(i).get("head").toString();
					headurl = headurl.substring(1,headurl.length() - 1);
					sex = Integer.parseInt(jsoninfo.get(i).get("sex").toString());
					citys = jsoninfo.get(i).get("province_code").toString();
					if (citys.length() == 2) {
						city = 0;
					} else {
						city = Integer.parseInt(citys.substring(1,citys.length()-1));
					}
					fanssum = Integer.parseInt(jsoninfo.get(i).get("fansnum").toString());
					isvip = Integer.parseInt(jsoninfo.get(i).get("isvip").toString());
					addv = Integer.parseInt(jsoninfo.get(i).get("isrealname").toString());
					/******************1、区域分析******************/
					if (city == 400) {
						province[0]++;
					} else {
						province[city]++;
					}
					/******************2、头像分析******************/
					if (headurl == null) {
						ishead[0]++;
					} else {
						ishead[1]++;
					}			
					/******************3、性别分析******************/
					gender[sex]++; //1-男，2-女，0-未填写
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
					if (fanssum < 50) {
						quality[0]++;
					} else {
						quality[1]++;
					}
					/***************9、Top50水军UID****************/
					/******************10、认证比例******************/
					if (addv == 0) {
						verifiedratio[0]++;
					} else {
						verifiedratio[1]++;
					}
					/***********11、用户等级分析（按创建时间）*************/
					/**********12、Top50粉丝UID（按创建时间）************/
					/****************13、平均每天发博量****************/
					/*********14、Top50粉丝UID（按平均发博量）***********/
					/******************15、vip分布******************/
					viptype[isvip]++;  //0-不是，1-是
					/************16、用户等级分析（按活跃度）*************/					
				}
			}
			/*********************数据整理******************************/
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
			result.getGender().put("m", Float.toString((float)Math.round(((float)gender[2]/(gender[1]+gender[2]))*10000)/100) + "%");
			result.getGender().put("f", Float.toString((float)Math.round(((float)gender[1]/(gender[1]+gender[2]))*10000)/100) + "%");
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
			result.getVipType().put("no", Float.toString((float)Math.round(((float)viptype[0]/existuids)*10000)/100) + "%");
			result.getVipType().put("yes", Float.toString((float)Math.round(((float)viptype[1]/existuids)*10000)/100) + "%");
			/************16、用户等级分析（按活跃度）*************/
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
			if (userfanssum > this.limit) {
				ifollowers = ((float)quality[0] / this.limit)*userfanssum;
				iflowersclass = (float)(fansclassforfans[2]+fansclassforfans[3]+fansclassforfans[4])/this.limit;
			} else {
				ifollowers = quality[1];
				iflowersclass = (float)(fansclassforfans[2]+fansclassforfans[3]+fansclassforfans[4])/userfanssum;
			}
			inf = (float) (0.8*(Math.log(ifollowers)/Math.log(1.5)) + 0.1*Math.abs(Math.log(iflowersclass)/Math.log(2)) + 0.1*Math.abs(Math.log(((float)verifiedratio[1]/existuids))/Math.log(2)));
			result.setInfluence(Integer.toString(Math.round(inf*100/30)));
			/*******************数据转换与存储*****************/
			jsonresult = JSONArray.fromObject(result);
			usermysql.insertResult(uid, userfanssum, jsonresult.toString());

			usermysql.sqlClose();
		}
		tworesult[0] = jsonresult.toString();
		tworesult[1] = Integer.toString(apicount);

		return tworesult;
	}

	/**
	 * @授权参数初始化
	 * @param oauth
	 */
	public static void oauthInit(OAuthV1 oauth, String accesstoken, String tokensecret) 
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
