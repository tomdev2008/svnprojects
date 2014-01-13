package cc.pp.tencent.require.ppdongli.single.weibo;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.codehaus.jackson.JsonNode;

import cc.pp.tencent.jdbc.WeiboJDBC;
import cc.pp.tencent.utils.JsonUtils;

import com.tencent.weibo.oauthv1.OAuthV1;

/**
 * 单条微博分析工具类
 * @author Administrator
 *
 */
public class WeiboUtils {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		WeiboUtils weiboUtils = new WeiboUtils();
		String[] array = new String[3];
		weiboUtils.initStrArray(array);
		for (int i = 0; i < array.length; i++) {
			System.out.println(array[i]);
		}

	}

	/**
	 * 字符串数组初始化
	 */
	public void initStrArray(String[] array) {

		for (int i = 0; i < array.length; i++) {
			array[i] = "0=0";
		}
	}

	/**
	 * 性别判断
	 */
	public int checkSex(String sex) {

		if (sex.equals("m")) {
			return 0; //0----m
		} else {
			return 1; //1----f
		}
	}

	/**
	 * 城市编码判断
	 */
	public int checkCity(int city) {

		if (city == 400) {
			return 0;
		} else {
			return city;
		}
	}

	/**
	 * 认证编码判断
	 */
	public int checkVerifyType(int verifytype) {

		if ((verifytype < 9)&&(verifytype >= 0)) {
			return verifytype;
		} else if (verifytype == 200) {
			return 9;
		} else if (verifytype == 220) {
			return 10;
		} else if (verifytype == 400) {
			return 11;
		} else if (verifytype == -1) {
			return 12;
		} else {
			return 13;
		}
	}

	/**
	 * 加V判断
	 */
	public int checkAddV(boolean addv) {

		if (addv == false) {
			return 0;
		} else {
			return 1;
		}
	}

	/**
	 * 水军判断
	 */
	public int checkQuality(String text) {

		if (text.contains("转发微博")) {
			return 0;
		} else {
			return 1;
		}
	}

	public int checkfans(int fanssum) {

		if (fanssum < 100) {
			return 0;
		} else {
			return 1;
		}
	}

	/**
	 * 根据时间戳（秒）获取小时
	 * @param time
	 * @return
	 */
	public String getHour(long time) {

		String result = new String();
		SimpleDateFormat format = new SimpleDateFormat("HH");
		result = format.format(new Date(time*1000l));
		if (result.substring(0,1).equals("0")) {
			result = result.substring(1);
		} 

		return result;
	}

	/**
	 * 原创微博信息整理
	 */
	public String oriWbArrange(UnionResult result, String wbinfo) {

		JsonNode jsondata = JsonUtils.getJsonNode(wbinfo);

		result.getOriwbinfo().setMid(jsondata.get("data").get("id").toString().replaceAll("\"", ""));
		result.getOriwbinfo().setCreatedat(jsondata.get("data").get("timestamp").toString().replaceAll("\"", ""));
		result.getOriwbinfo().setRepostcount(jsondata.get("data").get("count").toString().replaceAll("\"", ""));
		result.getOriwbinfo().setCommentcount(jsondata.get("data").get("mcount").toString().replaceAll("\"", ""));

		return jsondata.get("data").get("name").toString().replaceAll("\"", "");
	}

	public String oriWbArrange(PPDLData result, String wbinfo) {

		JsonNode jsondata = JsonUtils.getJsonNode(wbinfo);

		result.setMid(jsondata.get("data").get("id").toString().replaceAll("\"", ""));
		result.setCreatedat(jsondata.get("data").get("timestamp").toString().replaceAll("\"", ""));
		result.setRepostcount(jsondata.get("data").get("count").toString().replaceAll("\"", ""));
		result.setCommentcount(jsondata.get("data").get("mcount").toString().replaceAll("\"", ""));

		return jsondata.get("data").get("name").toString().replaceAll("\"", "");
	}

	/**
	 * 原创用户信息整理
	 */
	public void oriUserArrange(UnionResult result, String userinfo) {

		JsonNode jsondata = JsonUtils.getJsonNode(userinfo);

		result.getOriuserinfo().setUsername(jsondata.get("data").get("name").toString().replaceAll("\"", ""));
		result.getOriuserinfo().setNickname(jsondata.get("data").get("nick").toString().replaceAll("\"", ""));
		result.getOriuserinfo().setGender(jsondata.get("data").get("sex").toString().replaceAll("\"", ""));
		result.getOriuserinfo().setProvince(jsondata.get("data").get("province_code").toString().replaceAll("\"", ""));
		String str = jsondata.get("data").get("verifyinfo").toString().replaceAll("\"", "");
		if (str.length() > 0) {
			result.getOriuserinfo().setVerifiedtype("1");
		} else {
			result.getOriuserinfo().setVerifiedtype("0");
		}
		result.getOriuserinfo().setFanscount(jsondata.get("data").get("fansnum").toString().replaceAll("\"", ""));
		result.getOriuserinfo().setWeibocount(jsondata.get("data").get("tweetnum").toString().replaceAll("\"", ""));
	}

	public void oriUserArrange(PPDLData result, String userinfo) {

		JsonNode jsondata = JsonUtils.getJsonNode(userinfo);

		result.setUsername(jsondata.get("data").get("name").toString().replaceAll("\"", ""));
		result.setNickname(jsondata.get("data").get("nick").toString().replaceAll("\"", ""));
		result.setGender(jsondata.get("data").get("sex").toString().replaceAll("\"", ""));
		result.setProvince(jsondata.get("data").get("province_code").toString().replaceAll("\"", ""));
		String str = jsondata.get("data").get("verifyinfo").toString().replaceAll("\"", "");
		if (str.length() > 0) {
			result.setVerifiedtype("1");
		} else {
			result.setVerifiedtype("0");
		}
		result.setFanscount(jsondata.get("data").get("fansnum").toString().replaceAll("\"", ""));
		result.setWeibocount(jsondata.get("data").get("tweetnum").toString().replaceAll("\"", ""));
	}

	/**
	 * 转发时间线结果整理（24小时）
	 */
	public void Reposttime24HArrange(RepostedInfo result, int[] reposttimelineby24H, int existwb) {

		for (int i = 0; i < 24; i++) {
			result.getReposttimelineby24H().put(Integer.toString(i), Float.toString((float)Math.round(((float)reposttimelineby24H[i]/existwb)*10000)/100) + "%");
		}
	}

	/**
	 * 性别分布结果整理
	 */
	public void genderArrange(RepostedInfo result, int[] gender, int existwb) {

		result.getGender().put("m", Float.toString((float)Math.round(((float)gender[0]/existwb)*10000)/100) + "%");
		result.getGender().put("f", Float.toString((float)Math.round(((float)gender[1]/existwb)*10000)/100) + "%");
	}

	/**
	 * 区域分布结果整理
	 */
	public void locationArrange(RepostedInfo result, int[] location, int existwb) {

		if (location[0] != 0) {
			result.getLocation().put("400", Float.toString((float)Math.round(((float)location[0]/existwb)*10000)/100) + "%");
		}
		for (int j = 1; j < location.length; j++) {
			if (location[j] != 0) {
				result.getLocation().put(Integer.toString(j), Float.toString((float)Math.round(((float)location[j]/existwb)*10000)/100) + "%");
			}
		}
	}

	/**
	 * VIP类型getVipType
	 */
	public void vipTypeArrange(RepostedInfo result, int[] viptype, int existwb) {

		result.getViptype().put("no", Float.toString((float)Math.round(((float)viptype[0]/existwb)*10000)/100) + "%");
		result.getViptype().put("yes", Float.toString((float)Math.round(((float)viptype[1]/existwb)*10000)/100) + "%");
	}

	/**
	 * 是否自己发的微博
	 */
	public void isselfArrange(RepostedInfo result, int[] isself, int existwb) {

		result.getIsself().put("no", Float.toString((float)Math.round(((float)isself[0]/existwb)*10000)/100) + "%");
		result.getIsself().put("yes", Float.toString((float)Math.round(((float)isself[1]/existwb)*10000)/100) + "%");
	}

	/**
	 * 加V比例
	 */
	public void verifyArrange(RepostedInfo result, int[] addVRatio, int existwb) {

		result.getAddVRatio().put("old", Float.toString((float)Math.round(((float)addVRatio[0]/existwb)*10000)/100) + "%");
		result.getAddVRatio().put("yes", Float.toString((float)Math.round(((float)addVRatio[1]/existwb)*10000)/100) + "%");
		result.getAddVRatio().put("no", Float.toString((float)Math.round(((float)addVRatio[2]/existwb)*10000)/100) + "%");
	}

	/**
	 * 微博类型
	 */
	public void weibotypeArrange(RepostedInfo result, int[] weibotype, int existwb) {
		//1-原创发表，2-转载，3-私信，4-回复，5-空回，6-提及，7-评论
		for (int i = 1; i < weibotype.length; i++) {
			if (weibotype[i] != 0) {
				result.getWeibotype().put(Integer.toString(i), Float.toString((float)Math.round(((float)weibotype[i]/existwb)*10000)/100) + "%");
			}
		}
	}

	/**
	 * 微博状态
	 */
	public void wbStatusArrange(RepostedInfo result, int[] weibostatus, int existwb) {

		//0-正常，1-系统删除，2-审核中，3-用户删除，4-根删除
		result.getWeibostatus().put("normal",
				Float.toString((float) Math.round(((float) weibostatus[0] / existwb) * 10000) / 100) + "%");
		result.getWeibostatus().put("sysdel",
				Float.toString((float) Math.round(((float) weibostatus[1] / existwb) * 10000) / 100) + "%");
		result.getWeibostatus().put("verfy",
				Float.toString((float) Math.round(((float) weibostatus[2] / existwb) * 10000) / 100) + "%");
		result.getWeibostatus().put("userdel",
				Float.toString((float) Math.round(((float) weibostatus[3] / existwb) * 10000) / 100) + "%");
		result.getWeibostatus().put("rootdel",
				Float.toString((float) Math.round(((float) weibostatus[4] / existwb) * 10000) / 100) + "%");
	}

	/**
	 * 水军结果整理
	 */
	public void qualityArrange(RepostedInfo result, int[] reposterquality) {

		int sum = reposterquality[0] + reposterquality[1];
		result.getReposterquality().put("mask", Float.toString((float)Math.round(((float)reposterquality[0]/sum)*10000)/100) + "%");
		result.getReposterquality().put("real", Float.toString((float)Math.round(((float)reposterquality[1]/sum)*10000)/100) + "%");
	}

	public static String[] getAccessToken(String ip) throws SQLException {

		WeiboJDBC jdbc = new WeiboJDBC(ip);
		String[] token = null;
		if (jdbc.mysqlStatus()) {
			token = jdbc.getAccessToken();
			jdbc.sqlClose();
		}

		return token;
	}

	/**
	 * @获取wid
	 * @param url
	 * @return
	 */
	public static String getWid(String url) {
		//http://t.qq.com/p/t/197562129592354
		String wid = url.substring(20);

		return wid;
	}

	/**
	 * @授权参数初始化
	 * @param oauth
	 */
	public void oauthInit(OAuthV1 oauth, String accesstoken, String tokensecret) {
		/***************皮皮时光机appkey和appsecret***********************/
		oauth.setOauthConsumerKey("11b5a3c188484c3f8654b83d32e19bab");
		oauth.setOauthConsumerSecret("dc5cd31e1ddf556a42a40a1cff7efd5c");
		/**************************************************************/
		//oauth.setOauthCallback("");
		oauth.setOauthToken(accesstoken);
		oauth.setOauthTokenSecret(tokensecret);
	}
}
