package cc.pp.tencent.analysis.topic;

import java.io.Serializable;
import java.util.HashMap;

import org.codehaus.jackson.JsonNode;

import cc.pp.tencent.jdbc.TopicJDBC;
import cc.pp.tencent.utils.JsonUtils;

import com.tencent.weibo.api.TAPI;
import com.tencent.weibo.api.UserAPI;
import com.tencent.weibo.oauthv1.OAuthV1;

/**
 * 万网话题分析类
 * @author Administrator
 *
 */
public class WanWangTopicAnalysis implements Serializable {
	/**
	 * 默认的序列化版本号
	 */
	private static final long serialVersionUID = 1L;

	private String ip = "";
	private int apicount = 0;

	/**
	 * @构造函数
	 * @param ip,服务器IP地址
	 * @param pages,腾讯微博中话题页码数
	 */
	public WanWangTopicAnalysis(String ip) {
		this.ip = ip;
	}

	/**
	 * 测试函数
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {

		WanWangTopicAnalysis ta = new WanWangTopicAnalysis("192.168.1.151");
		String keywords = new String("北京男子凝视公交站牌狂哭2小时");
		ta.analysis(keywords, 2);

	}

	/**
	 * @分析函数
	 * @return
	 * @throws Exception 
	 */
	public void analysis(String keywords, int pages) throws Exception {

		/***********根据关键词提取相应的微博id*************/
		TopicUtils tpoicUtils = new TopicUtils();
		String[] wids = tpoicUtils.getWids(keywords, pages);
		/***********提取accesstoken*************/
		String[] tokens = tpoicUtils.getAccessToken(wids.length + 1, this.ip);
		/***************用户授权*****************/
		OAuthV1 oauth = new OAuthV1();

		TopicJDBC weibomysql = new TopicJDBC(this.ip);

		if (weibomysql.mysqlStatus()) {
			/**********************中间变量****************************/
			HashMap<Integer, String> username = new HashMap<Integer, String>(); //存放用户名
			int wbsum = 0, rcount, ccount, t = 0;
			String name, uid, isoriginal, citys, wburl, rewid; //转发微博wid
			int city; //区域信息
			int addv; //加V信息
			int isvip; //VIP类型
			String accesstoken, tokensecret;
			JsonNode weibodata, jsoninfo, swbdata;
			TAPI weibo;
			int cursor = 0, index = 99;
			String lastreposttime = null, lastwid = null;
			/**********************循环计算****************************/
			for (int nums = 0; nums < wids.length; nums++) {

				System.out.println(nums);

				accesstoken = tokens[nums].substring(0, tokens[nums].indexOf(","));
				tokensecret = tokens[nums].substring(tokens[nums].indexOf(",") + 1);
				TopicAnalysis.oauthInit(oauth, accesstoken, tokensecret);
				weibo = new TAPI(oauth.getOauthVersion());
				/******************单条微博数据*******************/
				String singlewbinfo = weibo.show(oauth, "json", wids[nums]);
				swbdata = JsonUtils.getJsonNode(singlewbinfo);
				wburl = "http://t.qq.com/p/t/" + wids[nums];
				if (swbdata.get("data").toString().equals("null")) {
					weibomysql.insertWbUsersInfo("", "", "", wburl, 0, 0, 0, 0, 0, 0, "", 0, 0, "", "");
				} else {
					uid = swbdata.get("data").get("name").toString().replaceAll("\"", "");
					username.put(t++, uid);
					name = swbdata.get("data").get("nick").toString().replaceAll("\"", "");
					citys = swbdata.get("data").get("province_code").toString();
					if (citys.length() == 2) {
						city = 0;
					} else {
						city = Integer.parseInt(citys.substring(1, citys.length() - 1));
					}
					rcount = Integer.parseInt(swbdata.get("data").get("count").toString());
					ccount = Integer.parseInt(swbdata.get("data").get("mcount").toString());
					if ("1".equals(swbdata.get("data").get("type").toString())) {
						isoriginal = "直发微博";
					} else {
						isoriginal = "转发微博";
					}
					isvip = Integer.parseInt(swbdata.get("data").get("isvip").toString());
					addv = Integer.parseInt(swbdata.get("data").get("isrealname").toString());
					/****************存储数据*******************/
					weibomysql.insertWbUsersInfo(uid, name, "", wburl, 0, 0, 0, city, rcount, ccount, isoriginal, addv,
							isvip, "", swbdata.get("data").get("timestamp").toString());
				}
				
				/********************转发数据********************/
				String weiboinfo = weibo.reList(oauth, "json", "0", wids[nums], "1", "0", "100", "0");
				this.apicount++;
				weibodata = JsonUtils.getJsonNode(weiboinfo);

				if (!weibodata.get("errcode").toString().equals("0")
						|| (weibodata.get("data").toString().equals("null"))) { //返回错误信息处理
					continue;
				}
				//1、转发量	
				wbsum = Integer.parseInt(weibodata.get("data").get("totalnum").toString());
				jsoninfo = weibodata.get("data").get("info");
				if (jsoninfo == null) {
					continue;
				}

				cursor = 0;
				lastreposttime = null;
				lastwid = null;
				index = 99;
				while ((cursor * 100 < wbsum) && (this.apicount < 100)) {
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
						uid = jsoninfo.get(i).get("name").toString().replaceAll("\"", "");
						username.put(t++, uid);
						rcount = Integer.parseInt(jsoninfo.get(i).get("count").toString());
						ccount = Integer.parseInt(jsoninfo.get(i).get("mcount").toString());
						rewid = jsoninfo.get(i).get("id").toString().replaceAll("\"", "");
						wburl = "http://t.qq.com/p/t/" + rewid;
						citys = jsoninfo.get(i).get("province_code").toString();
						if (citys.length() == 2) {
							city = 0;
						} else {
							city = Integer.parseInt(citys.substring(1, citys.length() - 1));
						}
						isvip = Integer.parseInt(jsoninfo.get(i).get("isvip").toString());
						addv = Integer.parseInt(jsoninfo.get(i).get("isrealname").toString());
						if ("1".equals(jsoninfo.get(i).get("type").toString())) {
							isoriginal = "直发微博";
						} else {
							isoriginal = "转发微博";
						}
						/****************存储数据*******************/
						weibomysql.insertWbUsersInfo(uid, name, "", wburl, 0, 0, 0, city, rcount, ccount, isoriginal,
								addv, isvip, "", jsoninfo.get(i).get("timestamp").toString());

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
			}
			/**************************用户数据*********************************/
			accesstoken = tokens[wids.length].substring(0, tokens[wids.length].indexOf(","));
			tokensecret = tokens[wids.length].substring(tokens[wids.length].indexOf(",") + 1);
			TopicAnalysis.oauthInit(oauth, accesstoken, tokensecret);
			UserAPI user = new UserAPI(oauth.getOauthVersion());
			String userinfo, uids = "";
			JsonNode userdata;
			int count = 1, fanssum, friendssum, sex;
			if (username.size() > 30) {
				count = username.size() / 30;
			}
			count = (count < 100) ? count : 100;
			for (int i = 0; i < count; i++) {
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
				for (int j = 0; j < 30; j++) {
					if (jsoninfo.get(j) == null) {
						continue;
					}
					fanssum = Integer.parseInt(jsoninfo.get(j).get("fansnum").toString());
					friendssum = Integer.parseInt(jsoninfo.get(j).get("idolnum").toString());
					sex = Integer.parseInt(jsoninfo.get(j).get("sex").toString());
					/****************更新数据*****************/
					weibomysql.updateWbUsersInfo(jsoninfo.get(j).get("name").toString().replaceAll("\"", ""), sex,
							fanssum, friendssum);
				}
			}
			weibomysql.sqlClose();
		}

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

