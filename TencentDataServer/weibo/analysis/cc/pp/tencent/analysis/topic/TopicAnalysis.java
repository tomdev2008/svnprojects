package cc.pp.tencent.analysis.topic;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import net.sf.json.JSONArray;

import org.codehaus.jackson.JsonNode;

import cc.pp.tencent.algorithms.InsertSort;
import cc.pp.tencent.result.TopicResult;
import cc.pp.tencent.utils.JsonUtils;

import com.tencent.weibo.api.TAPI;
import com.tencent.weibo.api.UserAPI;
import com.tencent.weibo.oauthv1.OAuthV1;

/**
 * Title: 话题分析
 * @author wanggang
 * @version 1.1
 * @since 2013-05-27
 */
public class TopicAnalysis implements Serializable {

	/**
	 * 默认的序列化版本号
	 */
	private static final long serialVersionUID = 1L;

	private String ip = "";
	private int apicount = 0;

	/**
	 * @构造函数
	 * @param ip,服务器IP地址
	 * @param pages,新浪微博中话题页码数
	 */
	public TopicAnalysis(String ip) {
		this.ip = ip;
	}

	/**
	 * 测试函数
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {

		TopicAnalysis ta = new TopicAnalysis("192.168.1.151");
		String keywords = new String("复旦投毒");
		String[] result = ta.analysis(keywords, 3);
		System.out.println(result[0]);
		System.out.println(result[1]);

	}

	/**
	 * @分析函数
	 * @return
	 * @throws Exception 
	 */
	@SuppressWarnings("unused")
	public String[] analysis(String keywords, int pages) throws Exception {

		JSONArray jsonresult = null;
		//		TopicJDBC weibomysql = new TopicJDBC(this.ip);
		String[] tworesult = new String[2];
		//		if (weibomysql.mysqlStatus()) 
		//		{
		TopicResult result = new TopicResult();
		/**********提取accesstoken*************/
		/**********************用户授权****************************/
		TopicUtils tpoicUtils = new TopicUtils();
		String[] wids = tpoicUtils.getWids(keywords, pages);
		result.setOriginalUser(Integer.toString(wids.length)); //原创用户数
		String[] tokens = tpoicUtils.getAccessToken(wids.length + 1, this.ip);
		JsonUtils jsonutils = new JsonUtils();
		OAuthV1 oauth = new OAuthV1();
		/**********************转发数据****************************/
		HashMap<String, Integer> reposttimelinebyDay = new HashMap<String, Integer>(); //3、转发时间线--按天
		HashMap<Integer, String> username = new HashMap<Integer, String>(); //存放用户名
		int[] reposttimelineby24H = new int[24]; //按24小时内
		int[] gender = new int[3]; //性别分布
		int[] repostergrade = new int[5]; //9、转发用户等级（按粉丝量）
		int[] reposterquality = new int[2]; //11、转发用户质量（水军比例）
		int exposionsum = 0; //12、总曝光量
		int[] location = new int[101]; //6、区域分布
		int[] viptype = new int[14];
		; //7、VIP类型
		int[] isself = new int[2]; //9、是否自己发的微博
		int[] addVRatio = new int[3]; //10、加V比例,0-老用户，1-已实名认证，2-未实名认证
		int[] weibotype = new int[8]; //12、微博类型,1-原创发表，2-转载，3-私信，4-回复，5-空回，6-提及，7-评论
		//			int[] etype = new int[2]; //13、心情类型
		int[] weibostatus = new int[5]; //14、微博状态,0-正常，1-系统删除，2-审核中，3-用户删除，4-根删除
		String[] keyusersbyrep = new String[50]; //15、关键账号，按照转发量
		for (int i = 0; i < 50; i++) {
			keyusersbyrep[i] = "0=0";
		}
		String[] keyusersbycom = new String[50]; //15、关键账号,按评论量
		for (int i = 0; i < 50; i++) {
			keyusersbycom[i] = "0=0";
		}

		int existwb = 0;
		int wbsum = 0;
		int comsum = 0;
		String name;
		String uid;
		String head;
		int rcount;
		int ccount;
		String rewid; //转发微博wid
		int self; //是否自己发的微博
		int wbtype; //微博类型,1-原创发表，2-转载，3-私信，4-回复，5-空回，6-提及，7-评论
		String citys;
		int city; //区域信息
		int emotiontype; //心情类型
		int addv; //加V信息
		int isvip; //VIP类型
		int wbstatus; //微博状态,0-正常，1-系统删除，2-审核中，3-用户删除，4-根删除
		long reposttime; //转发时间
		SimpleDateFormat fo1;
		String hour;
		SimpleDateFormat fo2;
		String date;
		JsonNode weibodata;
		JsonNode jsoninfo;
		JsonNode oriuserinfo;
		String accesstoken;
		String tokensecret;
		TAPI weibo;
		int cursor = 0;
		String lastreposttime = null;
		String lastwid = null;
		int index = 99;
		/**********************循环计算****************************/
		for (int nums = 0; nums < wids.length; nums++) {
			//				System.out.println(nums);
			accesstoken = tokens[nums].substring(0, tokens[nums].indexOf(","));
			tokensecret = tokens[nums].substring(tokens[nums].indexOf(",") + 1);
			TopicAnalysis.oauthInit(oauth, accesstoken, tokensecret);
			weibo = new TAPI(oauth.getOauthVersion());
			/**********************转发数据****************************/
			String weiboinfo = weibo.reList(oauth, "json", "0", wids[nums], "1", "0", "100", "0");
			this.apicount++;
			weibodata = JsonUtils.getJsonNode(weiboinfo);
			if (!weibodata.get("errcode").toString().equals("0") || (weibodata.get("data").toString().equals("null"))) { //返回错误信息处理
				continue;
			}
			//1、转发量	
			wbsum += Integer.parseInt(weibodata.get("data").get("totalnum").toString());
			jsoninfo = weibodata.get("data").get("info");
			if (jsoninfo == null) {
				continue;
			}

			cursor = 0;
			lastreposttime = null;
			lastwid = null;
			index = 99;
			while ((cursor * 100 < wbsum) && (this.apicount < 100))
			{
				jsoninfo = weibodata.get("data").get("info");
				if (jsoninfo == null) {
					break;
				}
				for (int i = 0; i < 100; i++) {
					if (jsoninfo.get(i) == null) {
						index = i - 1;
						break;
					}
					name = jsoninfo.get(i).get("nick").toString();
					name = name.substring(1, name.length() - 1).replaceAll("\"", "\\\\\"");
					uid = jsoninfo.get(i).get("name").toString();
					uid = uid.substring(1, uid.length() - 1);
					head = jsoninfo.get(i).get("head").toString();
					head = head.substring(1, head.length() - 1);
					rcount = Integer.parseInt(jsoninfo.get(i).get("count").toString());
					ccount = Integer.parseInt(jsoninfo.get(i).get("mcount").toString());
					rewid = jsoninfo.get(i).get("id").toString();
					rewid = rewid.substring(1, rewid.length() - 1);
					self = Integer.parseInt(jsoninfo.get(i).get("self").toString());
					wbtype = Integer.parseInt(jsoninfo.get(i).get("type").toString());
					citys = jsoninfo.get(i).get("province_code").toString();
					if (citys.length() == 2) {
						city = 0;
					} else {
						city = Integer.parseInt(citys.substring(1, citys.length() - 1));
					}
					isvip = Integer.parseInt(jsoninfo.get(i).get("isvip").toString());
					wbstatus = Integer.parseInt(jsoninfo.get(i).get("status").toString());
					addv = Integer.parseInt(jsoninfo.get(i).get("isrealname").toString());
					emotiontype = Integer.parseInt(jsoninfo.get(i).get("emotiontype").toString());
					reposttime = Integer.parseInt(jsoninfo.get(i).get("timestamp").toString());
					fo1 = new SimpleDateFormat("HH");
					hour = fo1.format(new Date(reposttime * 1000l));
					if (hour.substring(0, 1).equals("0")) {
						hour = hour.substring(1);
					}
					fo2 = new SimpleDateFormat("yyyy-MM-dd");
					date = fo2.format(new Date(reposttime * 1000l));
					username.put(existwb, uid);
					existwb++;
					/**************************15、关键账号*********************************/
					keyusersbyrep = InsertSort.toptable(keyusersbyrep, name + "," + head + "=" + rcount);
					keyusersbycom = InsertSort.toptable(keyusersbycom, name + "," + head + "=" + ccount);
					/**************************3、转发时间线--按天*********************************/
					reposttimelineby24H[Integer.parseInt(hour)]++;
					if (reposttimelinebyDay.get(date) == null) {
						reposttimelinebyDay.put(date, 1);
					} else {
						reposttimelinebyDay.put(date, reposttimelinebyDay.get(date) + 1);
					}
					/**************************6、区域分布*********************************/
					if (city == 400) {
						location[0]++;
					} else {
						location[city]++;
					}
					/**************************7、VIP类型*********************************/
					viptype[isvip]++;
					;
					/************************9、是否自己发的微博******************************/
					isself[self]++;
					/**************************10、加V比例*********************************/
					addVRatio[addv]++;
					/********************11、转发用户质量（水军比例）****************************/
					/**************************12、微博类型*********************************/
					weibotype[wbtype]++; //1-原创发表，2-转载，3-私信，4-回复，5-空回，6-提及，7-评论
					/**************************13、心情类型*********************************/
					/**************************14、微博状态*********************************/
					weibostatus[wbstatus]++; //0-正常，1-系统删除，2-审核中，3-用户删除，4-根删除
				}
				lastreposttime = jsoninfo.get(index).get("timestamp").toString();
				lastwid = jsoninfo.get(index).get("id").toString();
				weiboinfo = weibo.reList(oauth, "json", "0", wids[nums], "1", lastreposttime, "100", lastwid);
				cursor++;
				this.apicount++;
				weibodata = JsonUtils.getJsonNode(weiboinfo);
				if (weibodata.get("data") == null) { //返回错误信息处理
					break;
				}
			}
			/**********************评论数据****************************/
			weiboinfo = weibo.reList(oauth, "json", "1", wids[nums], "1", "0", "100", "0");
			this.apicount++;
			weibodata = JsonUtils.getJsonNode(weiboinfo);
			//1、评论量
			if (weibodata.get("data").toString().equals("null")) {
				continue;
			}
			comsum += Integer.parseInt(weibodata.get("data").get("totalnum").toString());
		}
		/**************************用户数据*********************************/
		accesstoken = tokens[wids.length].substring(0, tokens[wids.length].indexOf(","));
		tokensecret = tokens[wids.length].substring(tokens[wids.length].indexOf(",") + 1);
		TopicAnalysis.oauthInit(oauth, accesstoken, tokensecret);
		UserAPI user = new UserAPI(oauth.getOauthVersion());
		String userinfo;
		String uids = null;
		JsonNode userdata;
		int usersum = 0;
		;
		int fanssum;
		int sex;
		int count = username.size() / 30;
		count = (count < 100) ? count : 100;
		for (int i = 0; i < 1; i++) {
			for (int j = 0; j < 30; j++) {
				uids += username.get(i * 30 + j) + ",";
			}
			uids = uids.substring(0, uids.length() - 1);
			userinfo = user.infos(oauth, "json", uids, "");
			userdata = JsonUtils.getJsonNode(userinfo);
			if (!userdata.get("errcode").toString().equals("0")) {
				continue;
			}
			jsoninfo = userdata.get("data").get("info");
			if (jsoninfo == null) {
				continue;
			}
			for (int j = 0; j < 30; j++)
			{
				if (jsoninfo.get(j) == null) {
					break;
				}
				usersum++;
				fanssum = Integer.parseInt(jsoninfo.get(j).get("fansnum").toString());
				sex = Integer.parseInt(jsoninfo.get(j).get("sex").toString());
				/*************************4、性别分布****************************/
				gender[sex]++; //1-男，2-女，0-未填写
				/*********************9、转发用户等级（按粉丝量**********************/
				if (fanssum < 100) {
					repostergrade[0]++; //0-----"X<100"
				} else if (fanssum < 1000) {
					repostergrade[1]++; //1-----"100<X<1000"
				} else if (fanssum < 10000) {
					repostergrade[2]++; //2-----"1000<X<1w"
				} else if (fanssum < 100000) {
					repostergrade[3]++; //3-----"1w<X<10w"
				} else {
					repostergrade[4]++; //4-----"X>10w"
				}
				/*********************11、转发用户质量（水军比例）**********************/
				if (fanssum < 100) {
					reposterquality[0]++;
				} else {
					reposterquality[1]++;
				}
				/*************************12、总曝光****************************/
				exposionsum += fanssum;
			}
			this.apicount++;
		}
		/**************************数据整理*********************************/
		/**********************转发量****************************/
		result.setRepostCount(Integer.toString(wbsum));
		/**********************评论量****************************/
		result.setCommentCount(Integer.toString(comsum));
		/**************************15、关键账号*********************************/
		for (int n = 0; n < 50; n++) {
			if (keyusersbyrep[n].length() > 5) {
				result.getKeyUsersByRep().put(Integer.toString(n), keyusersbyrep[n]);
			} else {
				break;
			}
		}
		for (int n = 0; n < 50; n++) {
			if (keyusersbycom[n].length() > 5) {
				result.getKeyUserByCom().put(Integer.toString(n), keyusersbycom[n]);
			} else {
				break;
			}
		}
		/**************************3、转发时间线--按天*********************************/
		for (int i = 0; i < 24; i++) {
			result.getReposttimelineBy24H().put(Integer.toString(i),
					Float.toString((float) Math.round(((float) reposttimelineby24H[i] / existwb) * 10000) / 100) + "%");
		}
		Set<String> keys = reposttimelinebyDay.keySet();
		Iterator<String> iterator = keys.iterator();
		int sum = 0;
		while (iterator.hasNext()) {
			sum += reposttimelinebyDay.get(iterator.next());
		}
		iterator = keys.iterator();
		String nextkey = null;
		while (iterator.hasNext()) {
			nextkey = iterator.next().toString();
			result.getReposttimelineByDay().put(
					nextkey,
					Float.toString((float) Math.round(((float) reposttimelinebyDay.get(nextkey) / sum) * 10000) / 100)
							+ "%");
		}
		/*************************4、性别分布****************************/
		result.getGender().put("m",
				Float.toString((float) Math.round(((float) gender[2] / (gender[1] + gender[2])) * 10000) / 100) + "%");
		result.getGender().put("f",
				Float.toString((float) Math.round(((float) gender[1] / (gender[1] + gender[2])) * 10000) / 100) + "%");
		/************4、用户等级分析（按粉丝量）*************/
		result.getReposterGrade().put("<100",
				Float.toString((float) Math.round(((float) repostergrade[0] / usersum) * 10000) / 100) + "%");
		result.getReposterGrade().put("100~1000",
				Float.toString((float) Math.round(((float) repostergrade[1] / usersum) * 10000) / 100) + "%");
		result.getReposterGrade().put("1000~1w",
				Float.toString((float) Math.round(((float) repostergrade[2] / usersum) * 10000) / 100) + "%");
		result.getReposterGrade().put("1w~10w",
				Float.toString((float) Math.round(((float) repostergrade[3] / usersum) * 10000) / 100) + "%");
		result.getReposterGrade().put(">10w",
				Float.toString((float) Math.round(((float) repostergrade[4] / usersum) * 10000) / 100) + "%");
		/**************************6、区域分布*********************************/
		if (location[0] != 0) {
			result.getLocation().put("400",
					Float.toString((float) Math.round(((float) location[0] / existwb) * 10000) / 100) + "%");
		}
		for (int j = 1; j < location.length; j++) {
			if (location[j] != 0) {
				result.getLocation().put(Integer.toString(j),
						Float.toString((float) Math.round(((float) location[j] / existwb) * 10000) / 100) + "%");
			}
		}
		/**************************7、VIP类型*********************************/
		result.getVipType().put("no",
				Float.toString((float) Math.round(((float) viptype[0] / existwb) * 10000) / 100) + "%");
		result.getVipType().put("yes",
				Float.toString((float) Math.round(((float) viptype[1] / existwb) * 10000) / 100) + "%");
		/******************8、水军分析******************/
		result.getReposterQuality().put("mask",
				Float.toString((float) Math.round(((float) reposterquality[0] / usersum) * 10000) / 100) + "%");
		result.getReposterQuality().put("real",
				Float.toString((float) Math.round(((float) reposterquality[1] / usersum) * 10000) / 100) + "%");
		/************************9、是否自己发的微博******************************/
		result.getIsSelf().put("no",
				Float.toString((float) Math.round(((float) isself[0] / existwb) * 10000) / 100) + "%");
		result.getIsSelf().put("yes",
				Float.toString((float) Math.round(((float) isself[1] / existwb) * 10000) / 100) + "%");
		/**************************10、加V比例*********************************/
		result.getAddVRatio().put("old",
				Float.toString((float) Math.round(((float) addVRatio[0] / existwb) * 10000) / 100) + "%");
		result.getAddVRatio().put("yes",
				Float.toString((float) Math.round(((float) addVRatio[1] / existwb) * 10000) / 100) + "%");
		result.getAddVRatio().put("no",
				Float.toString((float) Math.round(((float) addVRatio[2] / existwb) * 10000) / 100) + "%");
		/*************************12、总曝光****************************/
		result.setExposionSum(Long.toString((long) exposionsum * existwb / usersum));
		/**************************12、微博类型*********************************/
		//1-原创发表，2-转载，3-私信，4-回复，5-空回，6-提及，7-评论
		for (int i = 0; i < weibotype.length; i++) {
			if (weibotype[i] != 0) {
				result.getWeiboType().put(Integer.toString(i),
						Float.toString((float) Math.round(((float) weibotype[i] / existwb) * 10000) / 100) + "%");
			}
		}
		/**************************13、心情类型*********************************/
		/**************************14、微博状态*********************************/
		//0-正常，1-系统删除，2-审核中，3-用户删除，4-根删除
		result.getWeiboStatus().put("normal",
				Float.toString((float) Math.round(((float) weibostatus[0] / existwb) * 10000) / 100) + "%");
		result.getWeiboStatus().put("sysdel",
				Float.toString((float) Math.round(((float) weibostatus[1] / existwb) * 10000) / 100) + "%");
		result.getWeiboStatus().put("verfy",
				Float.toString((float) Math.round(((float) weibostatus[2] / existwb) * 10000) / 100) + "%");
		result.getWeiboStatus().put("userdel",
				Float.toString((float) Math.round(((float) weibostatus[3] / existwb) * 10000) / 100) + "%");
		result.getWeiboStatus().put("rootdel",
				Float.toString((float) Math.round(((float) weibostatus[4] / existwb) * 10000) / 100) + "%");

		/***************数据转换与存储**************/
		jsonresult = JSONArray.fromObject(result);
		//			weibomysql.sqlClose();
		//		}
		tworesult[0] = jsonresult.toString();
		tworesult[1] = Integer.toString(apicount);

		return tworesult;
	}

	/**
	 * @授权参数初始化
	 * @param oauth
	 */
	public static void oauthInit(OAuthV1 oauth, String accesstoken, String tokensecret) {
		/***************皮皮时光机appkey和appsecret***********************/
		oauth.setOauthConsumerKey("11b5a3c188484c3f8654b83d32e19bab");
		oauth.setOauthConsumerSecret("dc5cd31e1ddf556a42a40a1cff7efd5c");
		/**************************************************************/
		//oauth.setOauthCallback("");
		oauth.setOauthToken(accesstoken);
		oauth.setOauthTokenSecret(tokensecret);
	}

}
