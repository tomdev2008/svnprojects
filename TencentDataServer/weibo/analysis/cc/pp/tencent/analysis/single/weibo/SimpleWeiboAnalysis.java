package cc.pp.tencent.analysis.single.weibo;

import java.io.Serializable;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import net.sf.json.JSONArray;

import org.apache.commons.httpclient.URIException;
import org.codehaus.jackson.JsonNode;

import cc.pp.tencent.algorithms.InsertSort;
import cc.pp.tencent.common.SourceType;
import cc.pp.tencent.jdbc.WeiboJDBC;
import cc.pp.tencent.result.SimpleWeiboResult;
import cc.pp.tencent.utils.JsonUtils;

import com.tencent.weibo.api.TAPI;
import com.tencent.weibo.api.UserAPI;
import com.tencent.weibo.oauthv1.OAuthV1;

public class SimpleWeiboAnalysis implements Serializable {

	/**
	 * 默认的序列化版本号
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		SimpleWeiboAnalysis swa = new SimpleWeiboAnalysis("127.0.0.1", "root", "root");
		int apicount = swa.analysis("http://t.qq.com/p/t/312919011154932");
		swa.closeMysql();
		System.out.println(apicount);

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

	private final WeiboJDBC weibojdbc;

	public SimpleWeiboAnalysis(String ip) {
		weibojdbc = new WeiboJDBC(ip);
		weibojdbc.mysqlStatus();
	}

	public SimpleWeiboAnalysis(String ip, String user, String password) {
		weibojdbc = new WeiboJDBC(ip, user, password);
		weibojdbc.mysqlStatus();
	}

	/**
	 * 分析函数
	 * @throws Exception
	 * @throws WeiboException
	 * @throws URIException
	 */
	public int analysis(String url) throws Exception {

		int apicount = 0;
		/*************结果初始化*************/
		JSONArray jsonresult = null;
		SimpleWeiboResult result = new SimpleWeiboResult();
		SWeiboUtils weiboUtils = new SWeiboUtils();
		//		Emotion emotion = new Emotion();
		/***********结果变量初始化***********/
		int exposionsum = 0; // 1、总曝光量
		int[] emotions = new int[3]; // 4、情感值
		String[] keyusersbyreps = new String[50]; // 7、关键转发用户，按转发量
		weiboUtils.initStrArray(keyusersbyreps);
		String[] keyusersbycoms = new String[50]; // 7、关键用户，按评论量
		weiboUtils.initStrArray(keyusersbycoms);
		int[] reposttimelineby24H = new int[24]; // // 8、24小时内的转发分布
		HashMap<String, Integer> reposttimelinebyDay = new HashMap<String, Integer>(); //9、转发时间线，按照天
		int[] gender = new int[3]; // // 10、性别分析
		int[] location = new int[101]; // // 11、区域分布
		int[] wbsource = new int[6]; // 12、终端分布
		int[] reposterquality = new int[2]; // 13、转发用户质量
		int[] verifiedtype = new int[2]; // 14、认证分布
		HashMap<String, Integer> suminclass = new HashMap<String, Integer>(); // // 15、层级分析  || @个数确定
		String str = ""; // 16、关键词分析，其中str为待分词的所有微博内容
		int[] weibotype = new int[8]; //17、微博类型,1-原创发表，2-转载，3-私信，4-回复，5-空回，6-提及，7-评论
		HashMap<Integer, String> usernames = new HashMap<Integer, String>(); //存放用户名
		// 18、总体评价
		/***********中间变量初始化************/
		int rcount, ccount, city, existwb = 0, wbsum = 0;
		String head, uid, nickname, verifiy, hour, text, source, date, citys;
		long reposttime;
		JsonNode weibodata, jsoninfo;
		int wbtype, isvip; // isvip：VIP类型
		SimpleDateFormat fo1, fo2;
		/***********获取该微博数据************/
		String[] token = weibojdbc.getAccessToken();
		OAuthV1 oauth = new OAuthV1();
		SimpleWeiboAnalysis.oauthInit(oauth, token[0], token[1]);
		String wid = weiboUtils.getWid(url);
		apicount++;
		/*********************************单条微博信息**************************************/
		TAPI weibo = new TAPI(oauth.getOauthVersion());
		String userinfo = weibo.show(oauth, "json", wid);
		JsonNode userdata = JsonUtils.getJsonNode(userinfo, "data");

		//		System.out.println(userdata);

		if (userdata.toString().length() < 6) {
			return -1;
		}
		/********************1、原创用户信息******************/
		try {
			weiboUtils.originalUserArrange(result, userdata);
		} catch (RuntimeException e) {
			return -1;
		}
		/********************2、微博内容******************/
		String oritext = userdata.get("text").toString();
		result.setWbcontent(oritext.substring(1, oritext.length() - 1));
		jsonresult = JSONArray.fromObject(result);
		/********************4、总转发量******************/
		wbsum = Integer.parseInt(userdata.get("count").toString());
		result.setRepostcount(userdata.get("count").toString());
		/********************5、总评论量******************/
		result.setCommentcount(userdata.get("mcount").toString());
		/***********************************转发数据***************************************/
		String weiboinfo = weibo.reList(oauth, "json", "0", wid, "1", "0", "100", "0");
		apicount++;
		weibodata = JsonUtils.getJsonNode(weiboinfo);
		if (!weibodata.get("errcode").toString().equals("0") || (weibodata.get("data") == null)) { //返回错误信息处理
			return -1;
		}
		jsoninfo = weibodata.get("data").get("info");
		if (jsoninfo == null) {
			return -1;
		}
		String lastreposttime = null;
		String lastwid = null;
		int index = 99, cursor = 0;
		while ((cursor * 100 < wbsum) && (apicount < 100)) {

			//			System.out.println(cursor);

			jsoninfo = weibodata.get("data").get("info");
			if (jsoninfo == null) {
				break;
			}
			for (int i = 0; i < 100; i++) {

				//				System.out.println("Page " + cursor + ", " + i);

				if (jsoninfo.get(i) == null) {
					index = i - 1;
					break;
				}
				existwb++;
				uid = jsoninfo.get(i).get("name").toString().replaceAll("\"", "");
				usernames.put(existwb, uid);
				text = jsoninfo.get(i).get("text").toString().replaceAll("\"", "");
				str += text;
				nickname = jsoninfo.get(i).get("nick").toString().replaceAll("\"", "");
				head = jsoninfo.get(i).get("head").toString().replaceAll("\"", "");
				rcount = Integer.parseInt(jsoninfo.get(i).get("count").toString());
				ccount = Integer.parseInt(jsoninfo.get(i).get("mcount").toString());
				wbtype = Integer.parseInt(jsoninfo.get(i).get("type").toString());
				citys = jsoninfo.get(i).get("province_code").toString();
				source = jsoninfo.get(i).get("from").toString().replaceAll("\"", "");
				if (citys.length() == 2) {
					city = 0;
				} else {
					city = Integer.parseInt(citys.substring(1, citys.length() - 1));
				}
				isvip = Integer.parseInt(jsoninfo.get(i).get("isvip").toString());
				verifiy = (isvip == 0) ? "no" : "vip";
				reposttime = Integer.parseInt(jsoninfo.get(i).get("timestamp").toString());
				fo1 = new SimpleDateFormat("HH");
				hour = fo1.format(new Date(reposttime * 1000l));
				if (hour.substring(0, 1).equals("0")) {
					hour = hour.substring(1);
				}
				fo2 = new SimpleDateFormat("yyyy-MM-dd");
				date = fo2.format(new Date(reposttime * 1000l));

				/****************************6、情感值**********************************/
				//				if (cursor < 2) {
				//					weiboUtils.setEmotions(emotion, emotions, text);
				//				}
				/**************************7、关键账号*********************************/
				keyusersbyreps = InsertSort.toptable(keyusersbyreps, uid + "," + nickname + "," + head + "," + wbtype
						+ "," + verifiy + "," + reposttime + "=" + rcount);
				keyusersbycoms = InsertSort.toptable(keyusersbyreps, uid + "," + nickname + "," + head + "," + wbtype
						+ "," + verifiy + "," + reposttime + "=" + ccount);
				/*************************8、转发时间线（24小时）*************************/
				reposttimelineby24H[Integer.parseInt(hour)]++;
				/*************************9、转发时间线--按天****************************/
				weiboUtils.putRepostByDay(reposttimelinebyDay, date);
				/**************************11、区域分布********************************/
				location[weiboUtils.checkCity(city)]++;
				/**************************12、终端设备********************************/
				wbsource[SourceType.getCategory(source)]++;
				/**************************14、认证分布********************************/
				verifiedtype[isvip]++; // 是否为VIP用户
				/**************************15、层级分析********************************/
				weiboUtils.putSumInClass(suminclass, text);
				/**************************17、微博类型********************************/
				weibotype[wbtype]++; //1-原创发表，2-转载，3-私信，4-回复，5-空回，6-提及，7-评论
			}
			lastreposttime = jsoninfo.get(index).get("timestamp").toString();
			lastwid = jsoninfo.get(index).get("id").toString();
			weiboinfo = weibo.reList(oauth, "json", "0", wid, "1", lastreposttime, "100", lastwid);
			cursor++;
			apicount++;
			weibodata = JsonUtils.getJsonNode(weiboinfo);
			if (weibodata.get("data") == null) { //返回错误信息处理
				break;
			}
		}

		//		System.out.println("Over");

		/**************************用户基础数据*********************************/
		int[] t = this.userBaseInfo(oauth, weiboUtils, usernames, gender, reposterquality);
		exposionsum = t[1];
		apicount += t[2];
		if (existwb * t[0] == 0) {
			return -1;
		}
		/******************整理数据结果*******************/
		/****************3、总曝光量****************/
		long allexposion = (long) exposionsum * wbsum / t[0];
		result.setExposionsum(Long.toString(allexposion));
		/****************6、情感值*****************/
		weiboUtils.emotionArrange(result, emotions);
		/****************7、关键账号****************/
		weiboUtils.repskeyusersArrange(result, keyusersbyreps,
				this.getBasedinfo(oauth, weiboUtils.tran2Uids(keyusersbyreps)));
		weiboUtils.comskeyusersArrange(result, keyusersbycoms,
				this.getBasedinfo(oauth, weiboUtils.tran2Uids(keyusersbycoms)));
		/************8、转发时间线（24小时）************/
		weiboUtils.reposttime24HArrange(result, reposttimelineby24H, existwb);
		/************9、转发时间线（按天）**************/
		result.setReposttimelinebyDay(reposttimelinebyDay);
		/***************10、性别分布*****************/
		weiboUtils.genderArrange(result, gender);
		/***************11、区域分布*****************/
		weiboUtils.locationArrange(result, location, existwb);
		/***************12、终端设备*****************/
		weiboUtils.sourceArrange(result, wbsource, existwb);
		/***************13、水军比例*****************/
		weiboUtils.qualityArrange(result, reposterquality);
		/***************14、认证分布*****************/
		weiboUtils.verifiedTypeArrange(result, verifiedtype, existwb);
		/***************15、层级分析*****************/
		result.setSuminclass(suminclass);
		/***************16、关键词提取****************/
		weiboUtils.keywordsArrange(result, str);
		/***************17、微博类型*****************/
		weiboUtils.weibotypeArrange(result, weibotype, existwb);
		/***************18、总体评价*****************/
		weiboUtils.lastCommentArrange(result, allexposion, emotions, gender, location, reposterquality);
		/************结果数据存储************/
		jsonresult = JSONArray.fromObject(result);
		/**
		 * 存储到154上面
		 */
		WeiboJDBC mysql = new WeiboJDBC("192.168.1.154");
		if (mysql.mysqlStatus()) {
			mysql.insertWeiboResult(wid, url, jsonresult.toString());
			mysql.sqlClose();
		}
		//		System.out.println(JSONArray.fromObject(jsonresult));

		return apicount;
	}

	public void closeMysql() throws SQLException {
		weibojdbc.sqlClose();
	}

	/**
	 * 获取用户的信息
	 * @param usernames
	 * @return
	 * @throws Exception
	 */
	public HashMap<String, String> getBasedinfo(OAuthV1 oauth, HashMap<Integer, String> usernames) throws Exception {

		HashMap<String, String> result = new HashMap<String, String>();
		String uids = new String();
		/*****************获取所有pp用户****************/
		for (int i = 0; i < usernames.size();) {

			uids = this.getUids(usernames, i);
			/*****************采集用户信息******************/
			UserAPI user = new UserAPI(oauth.getOauthVersion());
			String usersinfo = user.infos(oauth, "json", uids, "");
			JsonNode jsondata = JsonUtils.getJsonNode(usersinfo, "data");
			JsonNode jsoninfo = JsonUtils.getJsonNode(jsondata, "info");

			if (jsoninfo == null) {
				i++;
				continue;
			}

			for (int j = 0; j < jsoninfo.size(); j++) {
				result.put(jsoninfo.get(j).get("name").toString().replaceAll("\"", ""), jsoninfo.get(j).get("fansnum")
						.toString());
			}
			if (usernames.size() - 30 > i) {
				i = i + 30;
			} else {
				i = usernames.size();
			}
		}

		return result;
	}

	/**
	 * 获取uid串
	 * @param usernames
	 * @return
	 */
	public String getUids(HashMap<Integer, String> usernames, int i) {

		String uids = null;
		if (usernames.size() - 30 > i) {
			for (int k = 0; k < 30; k++) {
				uids += usernames.get(i + k) + ",";
			}
		} else {
			for (int k = i; k < usernames.size(); k++) {
				uids += usernames.get(k) + ",";
			}
		}
		uids = uids.substring(0, uids.length() - 1);

		return uids;
	}

	/**
	 * 用户基础数据处理
	 * @param oauth
	 * @param weiboUtils
	 * @param usernames
	 * @param gender
	 * @param reposterquality
	 * @param exposionsum
	 * @return
	 * @throws Exception
	 */
	public int[] userBaseInfo(OAuthV1 oauth, SWeiboUtils weiboUtils, HashMap<Integer, String> usernames, int[] gender,
			int[] reposterquality) throws Exception {

		UserAPI user = new UserAPI(oauth.getOauthVersion());
		String uids = null, userinfo;
		JsonNode jsoninfo, userdata;
		int exposionsum = 0, apicount = 0;
		int sex, fanssum, usersum = 0, count = 1;
		if (usernames.size() > 30) {
			count = usernames.size() / 30;
		}
		count = (count < 10) ? count : 10;
		for (int i = 0; i < count; i++) {
			for (int j = 0; j < 30; j++) {
				uids += usernames.get(i * 30 + j) + ",";
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
			for (int j = 0; j < 30; j++) {
				if (jsoninfo.get(i) == null) {
					break;
				}
				usersum++;
				fanssum = Integer.parseInt(jsoninfo.get(i).get("fansnum").toString());
				sex = Integer.parseInt(jsoninfo.get(i).get("sex").toString());
				/*********************10、性别分布****************************/
				gender[sex]++; //1-男，2-女，0-未填写
				/*********************13、转发用户质量（水军比例）*****************/
				reposterquality[weiboUtils.checkfans(fanssum)]++;
				exposionsum += fanssum;
			}
			apicount++;
		}
		int[] result = new int[3];
		result[0] = usersum;
		result[1] = exposionsum;
		result[2] = apicount;

		return result;
	}

}
